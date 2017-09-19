package org.project.sfc.com.SfcImpl.OPENSTACK_SFC_driver;

import org.openbaton.catalogue.nfvo.VimInstance;
import org.openbaton.exceptions.VimDriverException;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.AuthenticationException;
import org.openstack4j.api.networking.PortService;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.v3.Region;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.options.PortListOptions;
import org.openstack4j.openstack.OSFactory;
import org.project.sfc.com.openbaton_nfvo.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
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
   * @param deviceId Instance id of vm
   */
  public List<String> getPortIdList(String deviceId, String tenantId) throws VimDriverException {

    List<String> portIdlist = new ArrayList<String>();

    OSClient os = authenticate(tenantId);

    PortListOptions plo = PortListOptions.create().deviceId(deviceId).tenantId(tenantId);
    List<? extends Port> ports = os.networking().port().list(plo);

    for (Port p : ports) {
      portIdlist.add(p.getId());
    }

    return portIdlist;
  }

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

  private boolean isV3API(VimInstance vimInstance) {
    return vimInstance.getAuthUrl().endsWith("/v3") || vimInstance.getAuthUrl().endsWith("/v3.0");
  }
}
