package org.project.sfc.com.SfcImpl.Test_SFC_driver;

import org.project.sfc.com.SfcModel.SFCCdict.SFCCdict;
import org.project.sfc.com.SfcModel.SFCdict.SfcDictWrapper;
import org.project.sfc.com.SfcModel.SFCdict.VNFdict;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.project.sfc.com.SfcInterfaces.SFC;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mah on 1/13/17.
 */
@Service
@Scope(value = "prototype")
public class Test_SFC extends SFC {

  @Override
  public void CreateSFC(SfcDictWrapper sfc_dict, HashMap<Integer, VNFdict> vnf_dict) {
    //create Test_SFC

  }

  @Override
  public void CreateSFs(Map<Integer, VNFdict> vnf_dict) {
    //create SFs
  }

  @Override
  public void DeleteSFs() {
    //delete all SFs
    //
  }

  @Override
  public String CreateSFP(SfcDictWrapper sfc_dict, Map<Integer, VNFdict> vnf_dict) {
    return "created_new_sfp";
  }

  @Override
  public String GetBytesCount(SFCCdict SFCC_dict) {
    return "Bytes_count";
  }

  @Override
  public ResponseEntity<String> DeleteSFC(String instance_id, boolean isSymmetric) {
    return null;
  }

  @Override
  public ResponseEntity<String> DeleteSFP(String instance_id, boolean isSymmetric) {
    return null;
  }

  @Override
  public String GetConnectedSFF(String SF_name) {
    return "SFF-x";
  }

  @Override
  public String GetHostID(String neutron_port_id) {
    return "OVS-181881182";
  }
}
