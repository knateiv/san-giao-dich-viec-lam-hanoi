package vn.gov.hanoi.dvvl.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_NganhNghe", schema = "dbo")
public class MNganhNghe {

    @Id
    @Column(name = "PK_MaNganhNghe", nullable = false)
    @JsonProperty("pkMaNganhNghe")
    private Integer pkMaNganhNghe;

    @Column(name = "sTenNganhNghe", length = 100, nullable = false)
    @JsonProperty("sTenNganhNghe")
    private String sTenNganhNghe;

    @Column(name = "sNhomNganh", length = 100)
    @JsonProperty("sNhomNganh")
    private String sNhomNganh;

    @Column(name = "bTrangThaiHoatDong", nullable = false)
    @JsonProperty("bTrangThaiHoatDong")
    private Boolean bTrangThaiHoatDong = true;

    @Column(name = "bDaXoa", nullable = false)
    @JsonProperty("bDaXoa")
    private Boolean bDaXoa = false;

    public Integer getPkMaNganhNghe() { return pkMaNganhNghe; }
    public void setPkMaNganhNghe(Integer pkMaNganhNghe) { this.pkMaNganhNghe = pkMaNganhNghe; }
    public String getSTenNganhNghe() { return sTenNganhNghe; }
    public void setSTenNganhNghe(String sTenNganhNghe) { this.sTenNganhNghe = sTenNganhNghe; }
    public String getSNhomNganh() { return sNhomNganh; }
    public void setSNhomNganh(String sNhomNganh) { this.sNhomNganh = sNhomNganh; }
    public Boolean getBTrangThaiHoatDong() { return bTrangThaiHoatDong; }
    public void setBTrangThaiHoatDong(Boolean bTrangThaiHoatDong) { this.bTrangThaiHoatDong = bTrangThaiHoatDong; }
    public Boolean getBDaXoa() { return bDaXoa; }
    public void setBDaXoa(Boolean bDaXoa) { this.bDaXoa = bDaXoa; }
}
