package org.project.sfc.com.SfcImpl.OPENSTACK_SFC_driver;

import com.google.gson.Gson;
import com.sun.xml.internal.bind.v2.TODO;
import org.openbaton.catalogue.mano.descriptor.Connection;
import org.openbaton.catalogue.mano.descriptor.VNFDConnectionPoint;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.network.ext.FlowClassifier;
import org.openstack4j.model.network.ext.FlowClassifierProtocol;
import org.openstack4j.openstack.OSFactory;
import org.project.sfc.com.SfcImpl.ODL_SFC_driver.JSON.SFCJSON.SFCJSON;
import org.project.sfc.com.SfcImpl.ODL_SFC_driver.JSON.SFFJSON.*;
import org.project.sfc.com.SfcImpl.ODL_SFC_driver.JSON.SFJSON.*;
import org.project.sfc.com.SfcImpl.ODL_SFC_driver.ODL_SFC.Opendaylight;
import org.project.sfc.com.SfcInterfaces.SFC;
import org.project.sfc.com.SfcModel.SFCCdict.SFCCdict;
import org.project.sfc.com.SfcModel.SFCdict.SFPdict;
import org.project.sfc.com.SfcModel.SFCdict.SfcDictWrapper;
import org.project.sfc.com.SfcModel.SFCdict.VNFdict;
import org.project.sfc.com.openbaton_nfvo.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

/**
 * Created by c.navisse on 14/09/17.
 */
@Service
@Scope(value = "prototype")
public class OpenstackSFC extends SFC {

  private OpenstackUtils openstackUtils;
  private OSClient.OSClientV3 os = null;
  private Properties properties;

  public String ODL_ip;
  public String ODL_port;
  public String ODL_username;
  public String ODL_password;

  public String openstack_ip;
  public String openstack_username;
  public String openstack_password;
  public String openstack_tenantname;

  public String Config_SF_URL =
      "restconf/config/service-function:service-functions/service-function/{0}/";
  public String Config_SFs_URL = "restconf/config/service-function:service-functions/";
  public String Config_SFF_URL =
      "restconf/config/service-function-forwarder:service-function-forwarders/service-function-forwarder/{0}/";
  public String Config_SFFs_URL =
      "restconf/config/service-function-forwarder:service-function-forwarders/";
  public String Config_SFC_URL =
      "restconf/config/service-function-chain:service-function-chains/service-function-chain/{0}/";
  public String Config_SFP_URL =
      "restconf/config/service-function-path:service-function-paths/service-function-path/{0}";
  //public int sff_counter = 1;
  public String Config_sfc_of_render_URL = "restconf/config/sfc-of-renderer:sfc-of-renderer-config";
  public String Config_netvirt_URL =
      "restconf/config/netvirt-providers-config:netvirt-providers-config";

  private static Logger logger = LoggerFactory.getLogger(OpenstackSFC.class);

  private void init() {

    this.Config_SF_URL = Config_SF_URL;
    this.Config_SFC_URL = Config_SFC_URL;
    this.Config_SFF_URL = Config_SFF_URL;
    this.Config_SFP_URL = Config_SFP_URL;

    // this.sff_counter = sff_counter;
  }

  public OpenstackSFC() throws IOException {

    this.openstackUtils = new OpenstackUtils();
  }

  private VNFdict searchVNFByName(HashMap<Integer, VNFdict> vnfDictMap, String vnfName){
    for(VNFdict vnfDict : vnfDictMap.values()){
      if(vnfDict.getName().equals(vnfName))
        return vnfDict;
    }

    return null;

  }

  @Override
  public void CreateSFC(SfcDictWrapper sfcDict, HashMap<Integer, VNFdict> vnfDictMap) {
    logger.debug("CreateSFC");
    //create Test_SFC

    //TODO

    for (Connection conn : sfcDict.getSfcDict().getChain()){
      VNFdict vnf = searchVNFByName(vnfDictMap, conn.getVNFD());
    }






    for (SFPdict p : sfcDict.getSfcDict().getPaths()){
      Map<Integer, VNFdict> aa = p.getPath_SFs();
    }


    SFCJSON sfc_json = create_sfc_json(sfc_dict, vnf_dict);
    ResponseEntity<String> sfc_result = createODLsfc(sfc_json);
    if (sfc_result == null) {
      logger.error("Unable to create ODL Test_SFC");
    }
    /*
    if (!sfc_result.getStatusCode().is2xxSuccessful()) {
      logger.error("Unable to create ODL Test_SFC");
    }*/
  }

  @Override
  public void CreateSFs(Map<Integer, VNFdict> vnf_dict) throws IOException {
    logger.debug("CreateSFs");

    //FlowClassifier fc = os.networking().flowClassifiers().create(Builders.flowClassifier()
    //       .name("fc-test")
    //       .description("fc-test")
    //      .destinationIpPrefix("192.168.100.11/32")
    //      .destinationPortRangeMin(8080)
    //      .destinationPortRangeMin(8080)
    //      .logicalSourcePort("9c40cd5a-2c98-4b5d-a220-dc73d7003bee")
    //      .protocol(FlowClassifierProtocol.TCP)
    //      .build());*/

    /*String dp_loc = "sf-data-plane-locator";
    ServiceFunctions sfs_json = new ServiceFunctions();
    HashMap<Integer, VNFdict> sf_net_map = new HashMap<Integer, VNFdict>();
    SFJSON FullSFjson = new SFJSON();
    Integer SF_ID;
    List<ServiceFunction> list_sfs = new ArrayList<ServiceFunction>();

    for (int sf_i = 0; sf_i < vnf_dict.size(); sf_i++) {
        logger.info("[ODL Create SF-Name]:" + vnf_dict.get(sf_i).getName());

        ServiceFunction sf_json = new ServiceFunction();
        SF_ID = sf_i;
        sf_json.setName(vnf_dict.get(sf_i).getName());
        SfDataPlaneLocator dplocDict = new SfDataPlaneLocator();
        //dplocDict.setName("vxlan");

        dplocDict.setName(vnf_dict.get(sf_i).getName() + "-dpl");
        dplocDict.setIp(vnf_dict.get(sf_i).getIP());
        dplocDict.setPort("6633");
        dplocDict.setTransport("service-locator:vxlan-gpe");
        dplocDict.setServiceFunctionForwarder("dummy");

        sf_json.setNshAware("true");
        sf_json.setIpMgmtAddress(vnf_dict.get(sf_i).getIP());
        //sf_json.setType("service-function-type:"+vnf_dict.get(sf_i).getType());
        sf_json.setType(vnf_dict.get(sf_i).getType());
        List<SfDataPlaneLocator> list_dploc = new ArrayList<SfDataPlaneLocator>();
        list_dploc.add(dplocDict);
        sf_json.setSfDataPlaneLocator(list_dploc);
        list_sfs.add(SF_ID, sf_json);
        sfs_json.setServiceFunction(list_sfs);
        //   FullSFjson.setServiceFunctions(sfs_json);

        sf_net_map.put(SF_ID, vnf_dict.get(sf_i));



        //debug by max
        //sf_json.
    }




    // need to be adjusted
    logger.debug("service-function-Net-Map:" + sf_net_map.get(0).getName());
    HashMap<String, Opendaylight.BridgeMapping> ovs_mapping = Locate_ovs_to_sf(sf_net_map);
    //logger.debug("OVS MAP: " + ovs_mapping.toString());
    Iterator br_map = ovs_mapping.entrySet().iterator();
    int counter = 0;

    while (br_map.hasNext()) {
        int position = Integer.MAX_VALUE;

        Map.Entry br_map_counter = (Map.Entry) br_map.next();
        for (int sf_id_counter = 0;
             sf_id_counter < ovs_mapping.get(br_map_counter.getKey()).sfs.size();
             sf_id_counter++) {
            logger.debug(
                    " ---> SF NAME: " + ovs_mapping.get(br_map_counter.getKey()).sfs.get(sf_id_counter));

            logger.debug(
                    "OVS Port:"
                            + ovs_mapping
                            .get(br_map_counter.getKey())
                            .getSFdict()
                            .get(ovs_mapping.get(br_map_counter.getKey()).getSfs().get(sf_id_counter))
                            .getTap_port());

            for (int sf_counter = 0; sf_counter < vnf_dict.size(); sf_counter++) {
                if (vnf_dict
                        .get(sf_counter)
                        .getName()
                        .equals(ovs_mapping.get(br_map_counter.getKey()).sfs.get(sf_id_counter))) {

                    position = sf_counter;
                    logger.debug(
                            " --->Find the SF: NAME: "
                                    + ovs_mapping.get(br_map_counter.getKey()).sfs.get(sf_id_counter)
                                    + " Position: "
                                    + position);

                    logger.debug(
                            "Found the Position of the Service Function "
                                    + ovs_mapping.get(br_map_counter.getKey()).sfs.get(sf_id_counter));
                }
            }
            if (position == Integer.MAX_VALUE) {
                logger.error(
                        " Could not find the position of the Service Function "
                                + ovs_mapping.get(br_map_counter.getKey()).sfs.get(sf_id_counter));
                break;
            }

            sfs_json
                    .getServiceFunction()
                    .get(position)
                    .getSfDataPlaneLocator()
                    .get(0)
                    .setServiceFunctionForwarder(ovs_mapping.get(br_map_counter.getKey()).getSFFname());
            sfs_json
                    .getServiceFunction()
                    .get(position)
                    .getSfDataPlaneLocator()
                    .get(0)
                    .setServiceFunctionOvsOvsPort(new ServiceFunctionOvsOvsPort());
            sfs_json
                    .getServiceFunction()
                    .get(position)
                    .getSfDataPlaneLocator()
                    .get(0)
                    .getServiceFunctionOvsOvsPort()
                    .setPortID(
                            ovs_mapping
                                    .get(br_map_counter.getKey())
                                    .getSFdict()
                                    .get(ovs_mapping.get(br_map_counter.getKey()).getSfs().get(sf_id_counter))
                                    .getTap_port());
            logger.debug(
                    "SF updated with SFF:" + ovs_mapping.get(br_map_counter.getKey()).getSFFname());
        }
        logger.debug(" LATER ---> SF NAME: " + sfs_json.getServiceFunction().get(position).getName());

        logger.debug(
                " LATER ---> SFF NAME: "
                        + sfs_json
                        .getServiceFunction()
                        .get(position)
                        .getSfDataPlaneLocator()
                        .get(0)
                        .getServiceFunctionForwarder());

        logger.debug("OVS_MAPPING >> " + ovs_mapping);
        counter++;
    }

    for (int sf_j = 0; sf_j < sfs_json.getServiceFunction().size(); sf_j++) {

        //check SF Exist in ODL ?
        ServiceFunctions service_functions = new ServiceFunctions();
        List<ServiceFunction> list_service_function = new ArrayList<ServiceFunction>();
        list_service_function.add(sfs_json.getServiceFunction().get(sf_j));
        service_functions.setServiceFunction(list_service_function);
        FullSFjson.setServiceFunctions(service_functions);
        ResponseEntity<String> sf_result = createODLsf(FullSFjson);
        logger.debug(
                "create ODL SF with name >> "
                        + FullSFjson.getServiceFunctions().getServiceFunction().get(0).getName());
        if (!sf_result.getStatusCode().is2xxSuccessful()) {
            logger.error("Unable to create ODL SF " + FullSFjson.toString());
        }
    }

    SFJSON All_SFs_JSON = new SFJSON();
    All_SFs_JSON.setServiceFunctions(sfs_json);
    List<ServiceFunctionForwarder> sff_list = new ArrayList<ServiceFunctionForwarder>();
    //building SFF
    ServiceFunctionForwarders prev_sff_dict = find_existing_sff(ovs_mapping);
    logger.debug("PREVIOUS sff_dict >> " + prev_sff_dict);

    if (prev_sff_dict != null) {
        logger.debug("Previous SFF is detected ");
        sff_list = create_sff_json(ovs_mapping, All_SFs_JSON, prev_sff_dict);

    } else {
        sff_list = create_sff_json(ovs_mapping, All_SFs_JSON, null);
    }
    for (int i = 0; i < sff_list.size(); i++) {
        for (int x = 0; x < sff_list.get(i).getServiceFunctionDictionary().size(); x++) {
            logger.debug(
                    i
                            + ")"
                            + x
                            + ") SFF LIST includes SF: "
                            + sff_list.get(i).getServiceFunctionDictionary().get(x).getName());
        }
    }

    for (int sff_index = 0; sff_index < sff_list.size(); sff_index++) {
        SFFJSON sff_json = new SFFJSON();
        sff_json.setServiceFunctionForwarders(new ServiceFunctionForwarders());
        sff_json
                .getServiceFunctionForwarders()
                .setServiceFunctionForwarder(new ArrayList<ServiceFunctionForwarder>());
        sff_json
                .getServiceFunctionForwarders()
                .getServiceFunctionForwarder()
                .add(sff_list.get(sff_index));
        Gson mapper = new Gson();
        for (int x = 0; x < sff_list.get(sff_index).getServiceFunctionDictionary().size(); x++) {

            logger.debug(
                    "json request formatted sff json: SF NAME  "
                            + sff_json
                            .getServiceFunctionForwarders()
                            .getServiceFunctionForwarder()
                            .get(0)
                            .getServiceFunctionDictionary()
                            .get(x)
                            .getName());
        }
        ResponseEntity<String> sff_result = createODLsff(sff_json);
        if (!sff_result.getStatusCode().is2xxSuccessful()) {
            logger.error("Unable to create ODL SFF ");
        }
    }*/
  }

  @Override
  public void DeleteSFs() {
    logger.debug("DeleteSFs");
  }

  /**
   * create a Service Function Path
   *
   * @param sfc_dict
   * @param vnf_dict
   * @return
   */
  @Override
  public String CreateSFP(SfcDictWrapper sfc_dict, Map<Integer, VNFdict> vnf_dict) {
    logger.debug("CreateSFP");
    return "";
  }

  /**
   * delete a Service Function Chain
   *
   * @param instance_id
   * @param isSymmetric
   * @return
   */
  @Override
  public ResponseEntity<String> DeleteSFC(String instance_id, boolean isSymmetric) {
    logger.debug("DeleteSFC");
    return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
  }

  /**
   * delete a Service Function Path
   *
   * @param instance_id
   * @param isSymmetric
   * @return
   */
  @Override
  public ResponseEntity<String> DeleteSFP(String instance_id, boolean isSymmetric) {
    logger.debug("DeleteSFP");
    return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
  }

  @Override
  public String GetBytesCount(SFCCdict SFCC_dict) {
    logger.debug("GetBytesCount");
    return "";
  }

  /**
   * get the Service Function Forwarder
   *
   * @param SF_name
   * @return
   * @throws IOException
   */
  @Override
  public String GetConnectedSFF(String SF_name) throws IOException {

    logger.debug("GetConnectedSFF " + SF_name);

    /*In openstack-sfc doesn't exists service function forwarder, hence we return the SF_name just to keep track*/

    return SF_name;
  }

  @Override
  public String GetHostID(String SF_Neutron_port_id) {
    logger.debug("GetHostID");
    return "";
  }
}
