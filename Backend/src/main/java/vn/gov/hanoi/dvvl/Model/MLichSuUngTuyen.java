package vn.gov.hanoi.dvvl.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_LichSuUngTuyen", schema = "dbo")
public class MLichSuUngTuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_MaLichSu", nullable = false)
    private Long pkMaLichSu;

    @Column(name = "FK_MaUngTuyen", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String fkMaUngTuyen;

    @Column(name = "iTrangThaiCu")
    private Integer iTrangThaiCu;

    @Column(name = "iTrangThaiMoi", nullable = false)
    private Integer iTrangThaiMoi;

    @Column(name = "FK_MaNguoiThucHien", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String fkMaNguoiThucHien;

    @Column(name = "sGhiChu", length = 500)
    private String sGhiChu;

    @Column(name = "dThoiGianThayDoi", nullable = false)
    private LocalDateTime dThoiGianThayDoi = LocalDateTime.now();

    public Long getPkMaLichSu() { return pkMaLichSu; }
    public void setPkMaLichSu(Long pkMaLichSu) { this.pkMaLichSu = pkMaLichSu; }
    public String getFkMaUngTuyen() { return fkMaUngTuyen != null ? fkMaUngTuyen.trim() : null; }
    public void setFkMaUngTuyen(String fkMaUngTuyen) { this.fkMaUngTuyen = fkMaUngTuyen; }
    public Integer getITrangThaiCu() { return iTrangThaiCu; }
    public void setITrangThaiCu(Integer iTrangThaiCu) { this.iTrangThaiCu = iTrangThaiCu; }
    public Integer getITrangThaiMoi() { return iTrangThaiMoi; }
    public void setITrangThaiMoi(Integer iTrangThaiMoi) { this.iTrangThaiMoi = iTrangThaiMoi; }
    public String getFkMaNguoiThucHien() { return fkMaNguoiThucHien != null ? fkMaNguoiThucHien.trim() : null; }
    public void setFkMaNguoiThucHien(String fkMaNguoiThucHien) { this.fkMaNguoiThucHien = fkMaNguoiThucHien; }
    public String getSGhiChu() { return sGhiChu; }
    public void setSGhiChu(String sGhiChu) { this.sGhiChu = sGhiChu; }
    public LocalDateTime getDThoiGianThayDoi() { return dThoiGianThayDoi; }
    public void setDThoiGianThayDoi(LocalDateTime dThoiGianThayDoi) { this.dThoiGianThayDoi = dThoiGianThayDoi; }
}
