package org.project.sfc.com.SfcInterfaces;

import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.project.sfc.com.SfcModel.SFCCdict.SFCCdict;
import org.project.sfc.com.SfcModel.SFCdict.SfcDictWrapper;
import org.project.sfc.com.SfcModel.SFCdict.VNFdict;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mah on 6/10/16.
 */
public interface SFCinterfaces {
  String interfaceVersion = "1.0";

  void CreateSFC(SfcDictWrapper sfc_dict, HashMap<Integer, VNFdict> vnf_dict);

  void CreateSFs(Map<Integer, VNFdict> vnf_dict) throws IOException;

  void DeleteSFs();

  String CreateSFP(SfcDictWrapper sfc_dict, Map<Integer, VNFdict> vnf_dict);

  ResponseEntity<String> DeleteSFC(String instance_id, boolean isSymmetric);

  ResponseEntity<String> DeleteSFP(String instance_id, boolean isSymmetric);

  String GetBytesCount(SFCCdict SFCC_dict);

  String GetConnectedSFF(String SF_name) throws IOException;

  String GetHostID(String SF_Neutron_port_id);
}
