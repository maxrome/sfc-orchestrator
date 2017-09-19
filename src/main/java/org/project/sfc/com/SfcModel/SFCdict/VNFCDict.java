package org.project.sfc.com.SfcModel.SFCdict;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 19/09/17.
 */
@Entity
public class VNFCDict implements Serializable {

  @SerializedName("id")
  @Expose
  @Id
  private String id;

  @SerializedName("portIdList")
  private List<String> portIdList;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getPortIdList() {
    return portIdList;
  }

  public void setPortIdList(List<String> portIdList) {
    this.portIdList = portIdList;
  }
}
