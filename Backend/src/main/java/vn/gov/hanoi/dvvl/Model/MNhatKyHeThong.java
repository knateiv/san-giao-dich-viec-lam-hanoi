package vn.gov.hanoi.dvvl.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_NhatKyHeThong", schema = "dbo")
public class MNhatKyHeThong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_MaNhatKy", nullable = false)
    private Long pkMaNhatKy;

    @Column(name = "FK_MaTaiKhoan", columnDefinition = "CHAR(10)", length = 10)
    private String fkMaTaiKhoan;

    @Column(name = "sLoaiHanhDong", length = 50, nullable = false)
    private String sLoaiHanhDong;

    @Column(name = "sMucDo", length = 20, nullable = false)
    private String sMucDo;

    @Column(name = "sDiaChiIP", length = 45)
    private String sDiaChiIP;

    @Column(name = "sTrinhDuyet", length = 255)
    private String sTrinhDuyet;

    @Column(name = "dThoiGianGhi", nullable = false)
    private LocalDateTime dThoiGianGhi = LocalDateTime.now();

    @Column(name = "sDuLieuCu", columnDefinition = "NVARCHAR(MAX)")
    private String sDuLieuCu;

    @Column(name = "sDuLieuMoi", columnDefinition = "NVARCHAR(MAX)")
    private String sDuLieuMoi;

    public Long getPkMaNhatKy() { return pkMaNhatKy; }
    public void setPkMaNhatKy(Long pkMaNhatKy) { this.pkMaNhatKy = pkMaNhatKy; }
    public String getFkMaTaiKhoan() { return fkMaTaiKhoan != null ? fkMaTaiKhoan.trim() : null; }
    public void setFkMaTaiKhoan(String fkMaTaiKhoan) { this.fkMaTaiKhoan = fkMaTaiKhoan; }
    public String getSLoaiHanhDong() { return sLoaiHanhDong; }
    public void setSLoaiHanhDong(String sLoaiHanhDong) { this.sLoaiHanhDong = sLoaiHanhDong; }
    public String getSMucDo() { return sMucDo; }
    public void setSMucDo(String sMucDo) { this.sMucDo = sMucDo; }
    public String getSDiaChiIP() { return sDiaChiIP; }
    public void setSDiaChiIP(String sDiaChiIP) { this.sDiaChiIP = sDiaChiIP; }
    public String getSTrinhDuyet() { return sTrinhDuyet; }
    public void setSTrinhDuyet(String sTrinhDuyet) { this.sTrinhDuyet = sTrinhDuyet; }
    public LocalDateTime getDThoiGianGhi() { return dThoiGianGhi; }
    public void setDThoiGianGhi(LocalDateTime dThoiGianGhi) { this.dThoiGianGhi = dThoiGianGhi; }
    public String getSDuLieuCu() { return sDuLieuCu; }
    public void setSDuLieuCu(String sDuLieuCu) { this.sDuLieuCu = sDuLieuCu; }
    public String getSDuLieuMoi() { return sDuLieuMoi; }
    public void setSDuLieuMoi(String sDuLieuMoi) { this.sDuLieuMoi = sDuLieuMoi; }
}
