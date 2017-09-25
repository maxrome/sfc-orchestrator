package org.project.sfc.com.SfcModel.SFCdict;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by mah on 2/8/16.
 */
@Entity
public class VDUDict implements Serializable {

  @SerializedName("id")
  @Expose
  @Id
  private String id;

  @SerializedName("VNFCDict")
  private List<VNFCDict> vfncDict;

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

  public List<VNFCDict> getVfncDict() {
    return vfncDict;
  }

  public void setVfncDict(List<VNFCDict> vfncDict) {
    this.vfncDict = vfncDict;
  }

  @Override
  public String toString() {
    return "VDUDict{" + "id='" + id + '\'' + ", vfncDict=" + vfncDict + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    VDUDict vduDict = (VDUDict) o;
    return Objects.equals(vfncDict, vduDict.vfncDict);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vfncDict);
  }
}
