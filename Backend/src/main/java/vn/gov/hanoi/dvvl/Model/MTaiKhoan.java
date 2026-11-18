package vn.gov.hanoi.dvvl.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_TaiKhoan", schema = "dbo")
public class MTaiKhoan {

    @Id
    @Column(name = "PK_MaTaiKhoan", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String pkMaTaiKhoan;

    @Column(name = "sEmail", length = 150, nullable = false, unique = true)
    private String sEmail;

    @Column(name = "sMatKhau", columnDefinition = "CHAR(60)", length = 60, nullable = false)
    private String sMatKhau;

    @Column(name = "FK_MaVaiTro", nullable = false)
    private Integer fkMaVaiTro;

    @Column(name = "sHoTen", length = 100)
    private String sHoTen;

    @Column(name = "sSDT", length = 10)
    private String sSDT;

    @Column(name = "sDuongDanAnhDaiDien", length = 255)
    private String sDuongDanAnhDaiDien;

    @Column(name = "bDaKichHoat", nullable = false)
    private Boolean bDaKichHoat = false;

    @Column(name = "bBiKhoa", nullable = false)
    private Boolean bBiKhoa = false;

    @Column(name = "iSoLanDangNhapSai", nullable = false)
    private Integer iSoLanDangNhapSai = 0;

    @Column(name = "dKhoaDenLuc")
    private LocalDateTime dKhoaDenLuc;

    @Column(name = "sLyDoKhoa", length = 255)
    private String sLyDoKhoa;

    @Column(name = "bDaXoa", nullable = false)
    private Boolean bDaXoa = false;

    @Column(name = "dNgayTao", nullable = false)
    private LocalDateTime dNgayTao = LocalDateTime.now();

    @Column(name = "dNgayCapNhat")
    private LocalDateTime dNgayCapNhat;

    public String getPkMaTaiKhoan() { return pkMaTaiKhoan != null ? pkMaTaiKhoan.trim() : null; }
    public void setPkMaTaiKhoan(String pkMaTaiKhoan) { this.pkMaTaiKhoan = pkMaTaiKhoan; }
    public String getSEmail() { return sEmail; }
    public void setSEmail(String sEmail) { this.sEmail = sEmail; }
    public String getSMatKhau() { return sMatKhau != null ? sMatKhau.trim() : null; }
    public void setSMatKhau(String sMatKhau) { this.sMatKhau = sMatKhau; }
    public Integer getFkMaVaiTro() { return fkMaVaiTro; }
    public void setFkMaVaiTro(Integer fkMaVaiTro) { this.fkMaVaiTro = fkMaVaiTro; }
    public String getSHoTen() { return sHoTen; }
    public void setSHoTen(String sHoTen) { this.sHoTen = sHoTen; }
    public String getSSDT() { return sSDT; }
    public void setSSDT(String sSDT) { this.sSDT = sSDT; }
    public String getSDuongDanAnhDaiDien() { return sDuongDanAnhDaiDien; }
    public void setSDuongDanAnhDaiDien(String sDuongDanAnhDaiDien) { this.sDuongDanAnhDaiDien = sDuongDanAnhDaiDien; }
    public Boolean getBDaKichHoat() { return bDaKichHoat; }
    public void setBDaKichHoat(Boolean bDaKichHoat) { this.bDaKichHoat = bDaKichHoat; }
    public Boolean getBBiKhoa() { return bBiKhoa; }
    public void setBBiKhoa(Boolean bBiKhoa) { this.bBiKhoa = bBiKhoa; }
    public Integer getISoLanDangNhapSai() { return iSoLanDangNhapSai; }
    public void setISoLanDangNhapSai(Integer iSoLanDangNhapSai) { this.iSoLanDangNhapSai = iSoLanDangNhapSai; }
    public LocalDateTime getDKhoaDenLuc() { return dKhoaDenLuc; }
    public void setDKhoaDenLuc(LocalDateTime dKhoaDenLuc) { this.dKhoaDenLuc = dKhoaDenLuc; }
    public String getSLyDoKhoa() { return sLyDoKhoa; }
    public void setSLyDoKhoa(String sLyDoKhoa) { this.sLyDoKhoa = sLyDoKhoa; }
    public Boolean getBDaXoa() { return bDaXoa; }
    public void setBDaXoa(Boolean bDaXoa) { this.bDaXoa = bDaXoa; }
    public LocalDateTime getDNgayTao() { return dNgayTao; }
    public void setDNgayTao(LocalDateTime dNgayTao) { this.dNgayTao = dNgayTao; }
    public LocalDateTime getDNgayCapNhat() { return dNgayCapNhat; }
    public void setDNgayCapNhat(LocalDateTime dNgayCapNhat) { this.dNgayCapNhat = dNgayCapNhat; }
}
