package vn.gov.hanoi.dvvl.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_QuanHuyen", schema = "dbo")
public class MQuanHuyen {

    @Id
    @Column(name = "PK_MaQuanHuyen", columnDefinition = "CHAR(5)", length = 5, nullable = false)
    @JsonProperty("pkMaQuanHuyen")
    private String pkMaQuanHuyen;

    @Column(name = "sTenQuanHuyen", length = 100, nullable = false)
    @JsonProperty("sTenQuanHuyen")
    private String sTenQuanHuyen;

    @Column(name = "sKhuVuc", length = 20)
    @JsonProperty("sKhuVuc")
    private String sKhuVuc;

    @Column(name = "iThuTuHienThi")
    @JsonProperty("iThuTuHienThi")
    private Integer iThuTuHienThi;

    @Column(name = "bDaXoa", nullable = false)
    @JsonProperty("bDaXoa")
    private Boolean bDaXoa = false;

    public String getPkMaQuanHuyen() { return pkMaQuanHuyen != null ? pkMaQuanHuyen.trim() : null; }
    public void setPkMaQuanHuyen(String pkMaQuanHuyen) { this.pkMaQuanHuyen = pkMaQuanHuyen; }
    public String getSTenQuanHuyen() { return sTenQuanHuyen; }
    public void setSTenQuanHuyen(String sTenQuanHuyen) { this.sTenQuanHuyen = sTenQuanHuyen; }
    public String getSKhuVuc() { return sKhuVuc; }
    public void setSKhuVuc(String sKhuVuc) { this.sKhuVuc = sKhuVuc; }
    public Integer getIThuTuHienThi() { return iThuTuHienThi; }
    public void setIThuTuHienThi(Integer iThuTuHienThi) { this.iThuTuHienThi = iThuTuHienThi; }
    public Boolean getBDaXoa() { return bDaXoa; }
    public void setBDaXoa(Boolean bDaXoa) { this.bDaXoa = bDaXoa; }
}
