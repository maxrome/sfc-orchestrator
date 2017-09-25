package org.project.sfc.com.SfcModel.SFCdict;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by root on 19/09/17.
 */
@Entity
public class VNFCDict implements Serializable {

  @SerializedName("id")
  @Expose
  @Id
  private String id;

  @SerializedName("vmId")
  private String vmId;

  @SerializedName("portIdMap")
  private HashMap<String, String> portIdMap;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public HashMap<String, String> getPortIdMap() {
    return portIdMap;
  }

  public void setPortIdMap(HashMap<String, String> portIdMap) {
    this.portIdMap = portIdMap;
  }

  public String getVmId() {
    return vmId;
  }

  public void setVmId(String vmId) {
    this.vmId = vmId;
  }

  @Override
  public String toString() {
    return "VNFCDict{" + "vmId='" + vmId + '\'' + ", portIdMap=" + portIdMap + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    VNFCDict vnfcDict = (VNFCDict) o;
    return Objects.equals(vmId, vnfcDict.vmId) && Objects.equals(portIdMap, vnfcDict.portIdMap);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vmId, portIdMap);
  }
}
