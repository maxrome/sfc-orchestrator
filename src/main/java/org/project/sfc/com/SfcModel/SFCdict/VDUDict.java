package org.project.sfc.com.SfcModel.SFCdict;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mah on 2/8/16.
 */
@Entity
public class VDUDict implements Serializable {

  @SerializedName("id")
  @Expose
  @Id
  private String id;

  @SerializedName("cpList")
  private List<CPDict> cpList;

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

  public List<CPDict> getCPList() {
    return cpList;
  }

  public void setCPList(List<CPDict> portIdList) {
    this.cpList = cpList;
  }

  @Override
  public String toString() {
    return "VDUDict{" + "id='" + id + '}';
  }
}
