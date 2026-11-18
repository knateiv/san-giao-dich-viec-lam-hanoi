package vn.gov.hanoi.dvvl.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_DoanhNghiep", schema = "dbo")
public class MDoanhNghiep {

    @Id
    @Column(name = "PK_MaDoanhNghiep", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String pkMaDoanhNghiep;

    @Column(name = "FK_MaTaiKhoan", columnDefinition = "CHAR(10)", length = 10, nullable = false, unique = true)
    private String fkMaTaiKhoan;

    @Column(name = "sTenDoanhNghiep", length = 200, nullable = false)
    private String sTenDoanhNghiep;

    @Column(name = "sTenVietTat", length = 100)
    private String sTenVietTat;

    @Column(name = "sMaSoThue", length = 14, nullable = false, unique = true)
    private String sMaSoThue;

    @Column(name = "sDuongDanGiayPhep", length = 255)
    private String sDuongDanGiayPhep;

    @Column(name = "sDiaChiTruSo", length = 255)
    private String sDiaChiTruSo;

    @Column(name = "FK_MaQuanTruSo", columnDefinition = "CHAR(5)", length = 5)
    private String fkMaQuanTruSo;

    @Column(name = "sLinhVucHoatDong", length = 200)
    private String sLinhVucHoatDong;

    @Column(name = "sNguoiDaiDien", length = 100)
    private String sNguoiDaiDien;

    @Column(name = "sTenNguoiLienHe", length = 100)
    private String sTenNguoiLienHe;

    @Column(name = "sSDTNguoiLienHe", length = 10)
    private String sSDTNguoiLienHe;

    @Column(name = "sEmailNguoiLienHe", length = 150)
    private String sEmailNguoiLienHe;

    @Column(name = "iTrangThaiThamDinh", nullable = false)
    private Integer iTrangThaiThamDinh = 0;

    @Column(name = "sGhiChuThamDinh", length = 500)
    private String sGhiChuThamDinh;

    @Column(name = "bDaXoa", nullable = false)
    private Boolean bDaXoa = false;

    public String getPkMaDoanhNghiep() { return pkMaDoanhNghiep != null ? pkMaDoanhNghiep.trim() : null; }
    public void setPkMaDoanhNghiep(String pkMaDoanhNghiep) { this.pkMaDoanhNghiep = pkMaDoanhNghiep; }
    public String getFkMaTaiKhoan() { return fkMaTaiKhoan != null ? fkMaTaiKhoan.trim() : null; }
    public void setFkMaTaiKhoan(String fkMaTaiKhoan) { this.fkMaTaiKhoan = fkMaTaiKhoan; }
    public String getSTenDoanhNghiep() { return sTenDoanhNghiep; }
    public void setSTenDoanhNghiep(String sTenDoanhNghiep) { this.sTenDoanhNghiep = sTenDoanhNghiep; }
    public String getSTenVietTat() { return sTenVietTat; }
    public void setSTenVietTat(String sTenVietTat) { this.sTenVietTat = sTenVietTat; }
    public String getSMaSoThue() { return sMaSoThue; }
    public void setSMaSoThue(String sMaSoThue) { this.sMaSoThue = sMaSoThue; }
    public String getSDuongDanGiayPhep() { return sDuongDanGiayPhep; }
    public void setSDuongDanGiayPhep(String sDuongDanGiayPhep) { this.sDuongDanGiayPhep = sDuongDanGiayPhep; }
    public String getSDiaChiTruSo() { return sDiaChiTruSo; }
    public void setSDiaChiTruSo(String sDiaChiTruSo) { this.sDiaChiTruSo = sDiaChiTruSo; }
    public String getFkMaQuanTruSo() { return fkMaQuanTruSo != null ? fkMaQuanTruSo.trim() : null; }
    public void setFkMaQuanTruSo(String fkMaQuanTruSo) { this.fkMaQuanTruSo = fkMaQuanTruSo; }
    public String getSLinhVucHoatDong() { return sLinhVucHoatDong; }
    public void setSLinhVucHoatDong(String sLinhVucHoatDong) { this.sLinhVucHoatDong = sLinhVucHoatDong; }
    public String getSNguoiDaiDien() { return sNguoiDaiDien; }
    public void setSNguoiDaiDien(String sNguoiDaiDien) { this.sNguoiDaiDien = sNguoiDaiDien; }
    public String getSTenNguoiLienHe() { return sTenNguoiLienHe; }
    public void setSTenNguoiLienHe(String sTenNguoiLienHe) { this.sTenNguoiLienHe = sTenNguoiLienHe; }
    public String getSSDTNguoiLienHe() { return sSDTNguoiLienHe; }
    public void setSSDTNguoiLienHe(String sSDTNguoiLienHe) { this.sSDTNguoiLienHe = sSDTNguoiLienHe; }
    public String getSEmailNguoiLienHe() { return sEmailNguoiLienHe; }
    public void setSEmailNguoiLienHe(String sEmailNguoiLienHe) { this.sEmailNguoiLienHe = sEmailNguoiLienHe; }
    public Integer getITrangThaiThamDinh() { return iTrangThaiThamDinh; }
    public void setITrangThaiThamDinh(Integer iTrangThaiThamDinh) { this.iTrangThaiThamDinh = iTrangThaiThamDinh; }
    public String getSGhiChuThamDinh() { return sGhiChuThamDinh; }
    public void setSGhiChuThamDinh(String sGhiChuThamDinh) { this.sGhiChuThamDinh = sGhiChuThamDinh; }
    public Boolean getBDaXoa() { return bDaXoa; }
    public void setBDaXoa(Boolean bDaXoa) { this.bDaXoa = bDaXoa; }
}
