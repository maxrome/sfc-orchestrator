package org.project.sfc.com.SfcModel.SFCdict;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mah on 2/8/16.
 */
@Entity
public class CPDict implements Serializable {

  @SerializedName("id")
  @Expose
  @Id
  private String id;

  @SerializedName("portIdList")
  private List<String> portIdList;

  @SerializedName("interfaceId")
  private int interfaceId;

  /**
   *
   * @return The id
   */
  public String getId() {
    return id;
  }

  public void setId(String ID) {
    this.id = ID;
  }

  public List<String> getPortIdList() {
    return portIdList;
  }

  public void setPortIdList(List<String> portIdList) {
    this.portIdList = portIdList;
  }

  public int getInterfaceId() {
    return interfaceId;
  }

  public void setInterfaceId(int interfaceId) {
    this.interfaceId = interfaceId;
  }

  @Override
  public String toString() {
    return "CPDict{"
        + "id='"
        + id
        + '\''
        + ", portIdList="
        + portIdList
        + ", interfaceId="
        + interfaceId
        + '}';
  }
}
