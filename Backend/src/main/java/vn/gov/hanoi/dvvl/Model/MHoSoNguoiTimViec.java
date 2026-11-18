package vn.gov.hanoi.dvvl.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_HoSoNguoiTimViec", schema = "dbo")
public class MHoSoNguoiTimViec {

    @Id
    @Column(name = "PK_MaUngVien", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String pkMaUngVien;

    @Column(name = "FK_MaTaiKhoan", columnDefinition = "CHAR(10)", length = 10, nullable = false, unique = true)
    private String fkMaTaiKhoan;

    @Column(name = "sHoTen", length = 100, nullable = false)
    private String sHoTen;

    @Column(name = "sSoCCCD", columnDefinition = "CHAR(12)", length = 12, nullable = false, unique = true)
    private String sSoCCCD;

    @Column(name = "dNgaySinh", nullable = false)
    private LocalDate dNgaySinh;

    @Column(name = "sGioiTinh", length = 10, nullable = false)
    private String sGioiTinh;

    @Column(name = "sSDT", length = 10, nullable = false)
    private String sSDT;

    @Column(name = "sEmailLienHe", length = 150)
    private String sEmailLienHe;

    @Column(name = "FK_MaQuanCuTru", columnDefinition = "CHAR(5)", length = 5, nullable = false)
    private String fkMaQuanCuTru;

    @Column(name = "sTrinhDoVanHoa", length = 50)
    private String sTrinhDoVanHoa;

    @Column(name = "sTrinhDoChuyenMon", length = 100)
    private String sTrinhDoChuyenMon;

    @Column(name = "sKyNangTinHoc", length = 200)
    private String sKyNangTinHoc;

    @Column(name = "sTrinhDoNgoaiNgu", length = 200)
    private String sTrinhDoNgoaiNgu;

    @Column(name = "sKinhNghiemLamViec", columnDefinition = "NVARCHAR(MAX)")
    private String sKinhNghiemLamViec;

    @Column(name = "sViTriMongMuon", length = 200)
    private String sViTriMongMuon;

    @Column(name = "fMucLuongKyVong")
    private Double fMucLuongKyVong;

    @Column(name = "sDiaBanMongMuon", length = 200)
    private String sDiaBanMongMuon;

    @Column(name = "sDuongDanCV", length = 255)
    private String sDuongDanCV;

    @Column(name = "iTyLeHoanThien", nullable = false)
    private Integer iTyLeHoanThien = 0;

    @Column(name = "bDangTimViecCongKhai", nullable = false)
    private Boolean bDangTimViecCongKhai = true;

    @Column(name = "bDaXoa", nullable = false)
    private Boolean bDaXoa = false;

    public String getPkMaUngVien() { return pkMaUngVien != null ? pkMaUngVien.trim() : null; }
    public void setPkMaUngVien(String pkMaUngVien) { this.pkMaUngVien = pkMaUngVien; }
    public String getFkMaTaiKhoan() { return fkMaTaiKhoan != null ? fkMaTaiKhoan.trim() : null; }
    public void setFkMaTaiKhoan(String fkMaTaiKhoan) { this.fkMaTaiKhoan = fkMaTaiKhoan; }
    public String getSHoTen() { return sHoTen; }
    public void setSHoTen(String sHoTen) { this.sHoTen = sHoTen; }
    public String getSSoCCCD() { return sSoCCCD != null ? sSoCCCD.trim() : null; }
    public void setSSoCCCD(String sSoCCCD) { this.sSoCCCD = sSoCCCD; }
    public LocalDate getDNgaySinh() { return dNgaySinh; }
    public void setDNgaySinh(LocalDate dNgaySinh) { this.dNgaySinh = dNgaySinh; }
    public String getSGioiTinh() { return sGioiTinh; }
    public void setSGioiTinh(String sGioiTinh) { this.sGioiTinh = sGioiTinh; }
    public String getSSDT() { return sSDT; }
    public void setSSDT(String sSDT) { this.sSDT = sSDT; }
    public String getSEmailLienHe() { return sEmailLienHe; }
    public void setSEmailLienHe(String sEmailLienHe) { this.sEmailLienHe = sEmailLienHe; }
    public String getFkMaQuanCuTru() { return fkMaQuanCuTru != null ? fkMaQuanCuTru.trim() : null; }
    public void setFkMaQuanCuTru(String fkMaQuanCuTru) { this.fkMaQuanCuTru = fkMaQuanCuTru; }
    public String getSTrinhDoVanHoa() { return sTrinhDoVanHoa; }
    public void setSTrinhDoVanHoa(String sTrinhDoVanHoa) { this.sTrinhDoVanHoa = sTrinhDoVanHoa; }
    public String getSTrinhDoChuyenMon() { return sTrinhDoChuyenMon; }
    public void setSTrinhDoChuyenMon(String sTrinhDoChuyenMon) { this.sTrinhDoChuyenMon = sTrinhDoChuyenMon; }
    public String getSKyNangTinHoc() { return sKyNangTinHoc; }
    public void setSKyNangTinHoc(String sKyNangTinHoc) { this.sKyNangTinHoc = sKyNangTinHoc; }
    public String getSTrinhDoNgoaiNgu() { return sTrinhDoNgoaiNgu; }
    public void setSTrinhDoNgoaiNgu(String sTrinhDoNgoaiNgu) { this.sTrinhDoNgoaiNgu = sTrinhDoNgoaiNgu; }
    public String getSKinhNghiemLamViec() { return sKinhNghiemLamViec; }
    public void setSKinhNghiemLamViec(String sKinhNghiemLamViec) { this.sKinhNghiemLamViec = sKinhNghiemLamViec; }
    public String getSViTriMongMuon() { return sViTriMongMuon; }
    public void setSViTriMongMuon(String sViTriMongMuon) { this.sViTriMongMuon = sViTriMongMuon; }
    public Double getFMucLuongKyVong() { return fMucLuongKyVong; }
    public void setFMucLuongKyVong(Double fMucLuongKyVong) { this.fMucLuongKyVong = fMucLuongKyVong; }
    public String getSDiaBanMongMuon() { return sDiaBanMongMuon; }
    public void setSDiaBanMongMuon(String sDiaBanMongMuon) { this.sDiaBanMongMuon = sDiaBanMongMuon; }
    public String getSDuongDanCV() { return sDuongDanCV; }
    public void setSDuongDanCV(String sDuongDanCV) { this.sDuongDanCV = sDuongDanCV; }
    public Integer getITyLeHoanThien() { return iTyLeHoanThien; }
    public void setITyLeHoanThien(Integer iTyLeHoanThien) { this.iTyLeHoanThien = iTyLeHoanThien; }
    public Boolean getBDangTimViecCongKhai() { return bDangTimViecCongKhai; }
    public void setBDangTimViecCongKhai(Boolean bDangTimViecCongKhai) { this.bDangTimViecCongKhai = bDangTimViecCongKhai; }
    public Boolean getBDaXoa() { return bDaXoa; }
    public void setBDaXoa(Boolean bDaXoa) { this.bDaXoa = bDaXoa; }
}
