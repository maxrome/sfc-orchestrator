package org.project.sfc.com.SfcImpl.OPENSTACK_SFC_driver;

import org.openbaton.catalogue.nfvo.VimInstance;
import org.openbaton.exceptions.VimDriverException;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.AuthenticationException;
import org.openstack4j.api.networking.PortService;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.v3.Region;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.ext.PortPair;
import org.openstack4j.model.network.ext.PortPairGroup;
import org.openstack4j.model.network.options.PortListOptions;
import org.openstack4j.openstack.OSFactory;
import org.project.sfc.com.openbaton_nfvo.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by c.navisse on 19/09/17.
 */
public class OpenstackUtils {

  private Logger log = LoggerFactory.getLogger(this.getClass());

  private Properties properties = ConfigReader.readProperties();

  public OpenstackUtils() throws IOException {
    String sslChecksDisabled = properties.getProperty("disable-ssl-certificate-checks", "false");
    log.debug("Disable SSL certificate checks: {}", sslChecksDisabled);
    //OpenStack4JDriver.lock = new ReentrantLock();
  }

    /**
     *
     * @param portIdIngress
     * @param portIdEgress
     * @param tenanatId
     * @param VmId
     * @return
     * @throws VimDriverException
     */

  public String createPortPair(String portIdIngress, String portIdEgress,String tenanatId,String VmId) throws VimDriverException {

    try {

      OSClient os = authenticate(tenanatId);

      PortPair portPair = os.networking().portPairs()
                .create(Builders.portPair()
                        .name("portPair- vnfc - " + VmId)
                        .ingressPortId(portIdIngress)
                        .egressPortId(portIdEgress)
                        .build());

      return portPair.getId();

    } catch (VimDriverException e) {
      throw e;
    }

  }

    /**
     *
     * @param portPairIdList
     * @param tenanatId
     * @param vnfId
     * @return
     * @throws VimDriverException
     */

  public String createPortPairGroups(List<String> portPairIdList,String tenanatId,String vnfId) throws VimDriverException {

    try {

        OSClient os = authenticate(tenanatId);

        PortPairGroup ppg = os.networking().portPairGroups().create(Builders.portPairGroup()
                .name("portPairGroup - " + vnfId)
                .description("portPairGroup - " + vnfId)
                .portPairs(portPairIdList).build());

        return ppg.getId();

    } catch (VimDriverException e) {
        throw e;
    }

  }

    /**
     *
     * @param deviceId
     * @param tenantId
     * @param networkName
     * @return
     * @throws VimDriverException
     */
  public HashMap<String, String> getNetworkPortIdMap(
      String deviceId, String tenantId, String networkName) throws VimDriverException {

    HashMap<String, String> networkPortIdMap = new HashMap<String, String>();

    OSClient os = authenticate(tenantId);

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("tenant_id", tenantId);
    map.put("name", networkName); //check if the filter is name!!!

    List<? extends Network> list = os.networking().network().list(map);
    if (!list.isEmpty()) {
      log.warn(
          "OpenstackUtils - getPortIdList - List of Network is empty to deviceId : "
              + deviceId
              + " - tenant_id : "
              + tenantId);
      return networkPortIdMap;
    }

    Network network = list.get(0);
    String networkId = network.getId();
    //Filtering also networking to match the connection point
    PortListOptions plo =
        PortListOptions.create().deviceId(deviceId).tenantId(tenantId).networkId(networkId);
    List<? extends Port> ports = os.networking().port().list(plo);

    for (Port p : ports) {
      networkPortIdMap.put(networkName, p.getId());
    }

    return networkPortIdMap;
  }

    /**
     *
     * @param tenantId
     * @return
     * @throws VimDriverException
     */
  public OSClient authenticate(String tenantId) throws VimDriverException {

    VimInstance viminstance = new VimInstance();
    String openstack_ip = properties.getProperty("openstack.ip");
    String openstack_username = properties.getProperty("openstack.username");
    String openstack_password = properties.getProperty("openstack.password");
    //String openstack_tenantname = properties.getProperty("openstack.tenantname");

    viminstance.setAuthUrl(openstack_ip);
    viminstance.setUsername(openstack_username);
    viminstance.setPassword(openstack_password);
    viminstance.setProjectId(tenantId);

    return authenticate(viminstance);
  }

    /**
     *
     * @param vimInstance
     * @return
     * @throws VimDriverException
     */
  public OSClient authenticate(VimInstance vimInstance) throws VimDriverException {

    OSClient os;
    try {
      if (isV3API(vimInstance)) {

        Identifier domain = Identifier.byName("Default");
        Identifier project = Identifier.byId(vimInstance.getTenant());
        log.trace("Domain id: " + domain.getId());
        log.trace("Project id: " + project.getId());

        os =
            OSFactory.builderV3()
                .endpoint(vimInstance.getAuthUrl())
                .scopeToProject(project)
                .credentials(vimInstance.getUsername(), vimInstance.getPassword(), domain)
                .authenticate();
        if (vimInstance.getLocation() != null
            && vimInstance.getLocation().getName() != null
            && !vimInstance.getLocation().getName().isEmpty()) {
          try {
            Region region =
                ((OSClient.OSClientV3) os)
                    .identity()
                    .regions()
                    .get(vimInstance.getLocation().getName());

            if (region != null) {
              ((OSClient.OSClientV3) os).useRegion(vimInstance.getLocation().getName());
            }
          } catch (Exception ignored) {
            log.warn(
                "Not found region '"
                    + vimInstance.getLocation().getName()
                    + "'. Use default one...");
            return os;
          }
        }
      } else {
        os =
            OSFactory.builderV2()
                .endpoint(vimInstance.getAuthUrl())
                .credentials(vimInstance.getUsername(), vimInstance.getPassword())
                .tenantName(vimInstance.getTenant())
                .authenticate();
        if (vimInstance.getLocation() != null
            && vimInstance.getLocation().getName() != null
            && !vimInstance.getLocation().getName().isEmpty()) {
          try {
            ((OSClient.OSClientV2) os).useRegion(vimInstance.getLocation().getName());
            ((OSClient.OSClientV2) os).identity().listTokenEndpoints();
          } catch (Exception e) {
            log.warn(
                "Not found region '"
                    + vimInstance.getLocation().getName()
                    + "'. Use default one...");
            ((OSClient.OSClientV2) os).removeRegion();
          }
        }
      }
    } catch (AuthenticationException e) {
      throw new VimDriverException(e.getMessage(), e);
    }
    return os;
  }

    /**
     *
     * @param vimInstance
     * @return
     */
  private boolean isV3API(VimInstance vimInstance) {
    return vimInstance.getAuthUrl().endsWith("/v3") || vimInstance.getAuthUrl().endsWith("/v3.0");
  }
}
