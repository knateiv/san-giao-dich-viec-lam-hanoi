package vn.gov.hanoi.dvvl.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_UngTuyen", schema = "dbo")
public class MUngTuyen {

    @Id
    @Column(name = "PK_MaUngTuyen", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String pkMaUngTuyen;

    @Column(name = "FK_MaTin", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String fkMaTin;

    @Column(name = "FK_MaUngVien", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String fkMaUngVien;

    @Column(name = "dNgayNop", nullable = false)
    private LocalDateTime dNgayNop = LocalDateTime.now();

    @Column(name = "iTrangThaiUngTuyen", nullable = false)
    private Integer iTrangThaiUngTuyen = 0;

    @Column(name = "dThoiGianPhongVan")
    private LocalDateTime dThoiGianPhongVan;

    @Column(name = "sDiaDiemPhongVan", length = 255)
    private String sDiaDiemPhongVan;

    @Column(name = "sHinhThucPhongVan", length = 30)
    private String sHinhThucPhongVan;

    @Column(name = "sGhiChuPhongVan", length = 500)
    private String sGhiChuPhongVan;

    @Column(name = "bDaXoa", nullable = false)
    private Boolean bDaXoa = false;

    public String getPkMaUngTuyen() { return pkMaUngTuyen != null ? pkMaUngTuyen.trim() : null; }
    public void setPkMaUngTuyen(String pkMaUngTuyen) { this.pkMaUngTuyen = pkMaUngTuyen; }
    public String getFkMaTin() { return fkMaTin != null ? fkMaTin.trim() : null; }
    public void setFkMaTin(String fkMaTin) { this.fkMaTin = fkMaTin; }
    public String getFkMaUngVien() { return fkMaUngVien != null ? fkMaUngVien.trim() : null; }
    public void setFkMaUngVien(String fkMaUngVien) { this.fkMaUngVien = fkMaUngVien; }
    public LocalDateTime getDNgayNop() { return dNgayNop; }
    public void setDNgayNop(LocalDateTime dNgayNop) { this.dNgayNop = dNgayNop; }
    public Integer getITrangThaiUngTuyen() { return iTrangThaiUngTuyen; }
    public void setITrangThaiUngTuyen(Integer iTrangThaiUngTuyen) { this.iTrangThaiUngTuyen = iTrangThaiUngTuyen; }
    public LocalDateTime getDThoiGianPhongVan() { return dThoiGianPhongVan; }
    public void setDThoiGianPhongVan(LocalDateTime dThoiGianPhongVan) { this.dThoiGianPhongVan = dThoiGianPhongVan; }
    public String getSDiaDiemPhongVan() { return sDiaDiemPhongVan; }
    public void setSDiaDiemPhongVan(String sDiaDiemPhongVan) { this.sDiaDiemPhongVan = sDiaDiemPhongVan; }
    public String getSHinhThucPhongVan() { return sHinhThucPhongVan; }
    public void setSHinhThucPhongVan(String sHinhThucPhongVan) { this.sHinhThucPhongVan = sHinhThucPhongVan; }
    public String getSGhiChuPhongVan() { return sGhiChuPhongVan; }
    public void setSGhiChuPhongVan(String sGhiChuPhongVan) { this.sGhiChuPhongVan = sGhiChuPhongVan; }
    public Boolean getBDaXoa() { return bDaXoa; }
    public void setBDaXoa(Boolean bDaXoa) { this.bDaXoa = bDaXoa; }
}
