package org.project.sfc.com.SfcDriver.PathCreation.DeploymentPathCreation;

import org.openbaton.catalogue.mano.common.Ip;
import org.openbaton.catalogue.mano.descriptor.Connection;
import org.openbaton.catalogue.mano.descriptor.NetworkForwardingPath;
import org.openbaton.catalogue.mano.descriptor.VNFDConnectionPoint;
import org.openbaton.catalogue.mano.descriptor.VirtualDeploymentUnit;
import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.mano.record.VNFCInstance;
import org.openbaton.catalogue.mano.record.VNFForwardingGraphRecord;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.exceptions.VimDriverException;
import org.project.sfc.com.SfcImpl.ODL_SFC_driver.ODL_SFC.NeutronClient;
import org.project.sfc.com.SfcImpl.OPENSTACK_SFC_driver.OpenstackUtils;
import org.project.sfc.com.SfcModel.SFCdict.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by mah on 4/22/16.
 */
public class OpenstackSFCPathSelection {

  NeutronClient NC;
  OpenstackUtils osUtils;
  private Logger logger;

  public OpenstackSFCPathSelection() throws IOException {
    NC = new NeutronClient();
    osUtils = new OpenstackUtils();
    this.logger = LoggerFactory.getLogger(this.getClass());
  }

  public VNFdict selectVNF(VirtualNetworkFunctionRecord vnfr) throws VimDriverException {
    VNFdict new_vnf = new VNFdict();

    for (VirtualDeploymentUnit vdu_x : vnfr.getVdu()) {

      VDUDict vduDict = new VDUDict();
      new_vnf.getVduList().add(vduDict);

      for (VNFCInstance vnfc_instance : vdu_x.getVnfc_instance()) {

        new_vnf.setName(vnfc_instance.getHostname()); //ci mette l'hostname
        new_vnf.setType(vnfr.getType());

        new_vnf.setId(vnfc_instance.getId());
        new_vnf.setStatus(Status.ACTIVE);

        Set<VNFDConnectionPoint> listConnectionPoints = vnfc_instance.getConnection_point();

        List<VNFCDict> listVNFC = new ArrayList<VNFCDict>();

        for (VNFDConnectionPoint VNFDCP : listConnectionPoints) {

          VNFCDict vnfcDict = new VNFCDict();
          vnfcDict.setPortIdMap(
              osUtils.getNetworkPortIdMap(
                  vnfc_instance.getId(), vdu_x.getProjectId(), VNFDCP.getVirtual_link_reference()));
        }

        vduDict.setVfncDict(listVNFC);

        for (Ip ip : vnfc_instance.getIps()) {
          new_vnf.setIP(ip.getIp());
          logger.debug(
              "[Select-VNF] Setting the IP  for " + new_vnf.getName() + " : " + new_vnf.getIP());

          new_vnf.setNeutronPortId(NC.getNeutronPortID(ip.getIp()));

          break;
        }

        //take only the first vnfc on descriptor array
        break;
        //}
      }
    }

    return new_vnf;
  }

  public HashMap<Integer, VNFdict> CreatePath(
      Set<VirtualNetworkFunctionRecord> vnfrs,
      VNFForwardingGraphRecord vnffgr,
      NetworkServiceRecord nsr)
      throws VimDriverException {
    logger.info("[SFP-Create] Creating Path started ");

    HashMap<Integer, VNFdict> vnfdicts = new HashMap<Integer, VNFdict>();
    List<VNFdict> vnf_test = new ArrayList<VNFdict>();
    List<String> chain = new ArrayList<String>();

    int i = 0;
    // for getting the VNF instance NAME
    String VNF_NAME;

    for (NetworkForwardingPath nfp : vnffgr.getNetwork_forwarding_path()) {

      for (int counter = 0; counter < nfp.getConnection().size(); counter++) {

        //for (Map.Entry<String, String> entry : nfp.getConnection()) {
        Connection entry = nfp.getConnection().get(counter);
        //Integer k = Integer.valueOf(entry.getKey());

        //int x = k.intValue();

        //need to be adjusted again (use put(entry.key()
        //if (counter == x) {

        for (VirtualNetworkFunctionRecord vnfr : vnfrs) {
          if (vnfr.getName().equals(entry.getVNFD())) {

            VNFdict new_vnf = selectVNF(vnfr);

            vnf_test.add(new_vnf);

            vnfdicts.put(counter, vnf_test.get(counter));
          }
        }
        //}
      }
    }
    //}

    logger.info("[SFP-Create] Creating Path Finished ");

    return vnfdicts;
  }
}
