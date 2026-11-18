package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class VNguoiTimViec {

    public static class HoSoRequest {
        @NotBlank(message = "Vui lòng nhập họ và tên.")
        @JsonProperty("sHoTen")
        private String sHoTen;

        @NotBlank(message = "Vui lòng nhập số CCCD 12 chữ số.")
        @JsonProperty("sSoCCCD")
        private String sSoCCCD;

        @NotNull(message = "Vui lòng chọn ngày sinh.")
        @JsonProperty("dNgaySinh")
        private LocalDate dNgaySinh;

        @NotBlank(message = "Vui lòng chọn giới tính.")
        @JsonProperty("sGioiTinh")
        private String sGioiTinh;

        @NotBlank(message = "Vui lòng nhập số điện thoại 10 chữ số.")
        @JsonProperty("sSDT")
        private String sSDT;

        @JsonProperty("sEmailLienHe")
        private String sEmailLienHe;

        @NotBlank(message = "Vui lòng chọn quận/huyện cư trú tại Hà Nội.")
        @JsonProperty("fkMaQuanCuTru")
        private String fkMaQuanCuTru;

        @JsonProperty("sTrinhDoVanHoa")
        private String sTrinhDoVanHoa;

        @JsonProperty("sTrinhDoChuyenMon")
        private String sTrinhDoChuyenMon;

        @JsonProperty("sKyNangTinHoc")
        private String sKyNangTinHoc;

        @JsonProperty("sTrinhDoNgoaiNgu")
        private String sTrinhDoNgoaiNgu;

        @JsonProperty("sKinhNghiemLamViec")
        private String sKinhNghiemLamViec;

        @JsonProperty("sViTriMongMuon")
        private String sViTriMongMuon;

        @JsonProperty("fMucLuongKyVong")
        private Double fMucLuongKyVong;

        @JsonProperty("sDiaBanMongMuon")
        private String sDiaBanMongMuon;

        @JsonProperty("bDangTimViecCongKhai")
        private Boolean bDangTimViecCongKhai;

        public String getSHoTen() { return sHoTen; }
        public void setSHoTen(String sHoTen) { this.sHoTen = sHoTen; }
        public String getSSoCCCD() { return sSoCCCD; }
        public void setSSoCCCD(String sSoCCCD) { this.sSoCCCD = sSoCCCD; }
        public LocalDate getDNgaySinh() { return dNgaySinh; }
        public void setDNgaySinh(LocalDate dNgaySinh) { this.dNgaySinh = dNgaySinh; }
        public String getSGioiTinh() { return sGioiTinh; }
        public void setSGioiTinh(String sGioiTinh) { this.sGioiTinh = sGioiTinh; }
        public String getSSDT() { return sSDT; }
        public void setSSDT(String sSDT) { this.sSDT = sSDT; }
        public String getSEmailLienHe() { return sEmailLienHe; }
        public void setSEmailLienHe(String sEmailLienHe) { this.sEmailLienHe = sEmailLienHe; }
        public String getFkMaQuanCuTru() { return fkMaQuanCuTru; }
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
        public Boolean getBDangTimViecCongKhai() { return bDangTimViecCongKhai; }
        public void setBDangTimViecCongKhai(Boolean bDangTimViecCongKhai) { this.bDangTimViecCongKhai = bDangTimViecCongKhai; }
    }

    public static class HoSoView {
        @JsonProperty("pkMaUngVien")
        private String pkMaUngVien;
        @JsonProperty("fkMaTaiKhoan")
        private String fkMaTaiKhoan;
        @JsonProperty("sHoTen")
        private String sHoTen;
        @JsonProperty("sSoCCCD")
        private String sSoCCCD;
        @JsonProperty("dNgaySinh")
        private LocalDate dNgaySinh;
        @JsonProperty("sGioiTinh")
        private String sGioiTinh;
        @JsonProperty("sSDT")
        private String sSDT;
        @JsonProperty("sEmailLienHe")
        private String sEmailLienHe;
        @JsonProperty("fkMaQuanCuTru")
        private String fkMaQuanCuTru;
        @JsonProperty("sTenQuanCuTru")
        private String sTenQuanCuTru;
        @JsonProperty("sTrinhDoVanHoa")
        private String sTrinhDoVanHoa;
        @JsonProperty("sTrinhDoChuyenMon")
        private String sTrinhDoChuyenMon;
        @JsonProperty("sKyNangTinHoc")
        private String sKyNangTinHoc;
        @JsonProperty("sTrinhDoNgoaiNgu")
        private String sTrinhDoNgoaiNgu;
        @JsonProperty("sKinhNghiemLamViec")
        private String sKinhNghiemLamViec;
        @JsonProperty("sViTriMongMuon")
        private String sViTriMongMuon;
        @JsonProperty("fMucLuongKyVong")
        private Double fMucLuongKyVong;
        @JsonProperty("sDiaBanMongMuon")
        private String sDiaBanMongMuon;
        @JsonProperty("sDuongDanCV")
        private String sDuongDanCV;
        @JsonProperty("iTyLeHoanThien")
        private Integer iTyLeHoanThien;
        @JsonProperty("bDangTimViecCongKhai")
        private Boolean bDangTimViecCongKhai;

        public String getPkMaUngVien() { return pkMaUngVien; }
        public void setPkMaUngVien(String pkMaUngVien) { this.pkMaUngVien = pkMaUngVien; }
        public String getFkMaTaiKhoan() { return fkMaTaiKhoan; }
        public void setFkMaTaiKhoan(String fkMaTaiKhoan) { this.fkMaTaiKhoan = fkMaTaiKhoan; }
        public String getSHoTen() { return sHoTen; }
        public void setSHoTen(String sHoTen) { this.sHoTen = sHoTen; }
        public String getSSoCCCD() { return sSoCCCD; }
        public void setSSoCCCD(String sSoCCCD) { this.sSoCCCD = sSoCCCD; }
        public LocalDate getDNgaySinh() { return dNgaySinh; }
        public void setDNgaySinh(LocalDate dNgaySinh) { this.dNgaySinh = dNgaySinh; }
        public String getSGioiTinh() { return sGioiTinh; }
        public void setSGioiTinh(String sGioiTinh) { this.sGioiTinh = sGioiTinh; }
        public String getSSDT() { return sSDT; }
        public void setSSDT(String sSDT) { this.sSDT = sSDT; }
        public String getSEmailLienHe() { return sEmailLienHe; }
        public void setSEmailLienHe(String sEmailLienHe) { this.sEmailLienHe = sEmailLienHe; }
        public String getFkMaQuanCuTru() { return fkMaQuanCuTru; }
        public void setFkMaQuanCuTru(String fkMaQuanCuTru) { this.fkMaQuanCuTru = fkMaQuanCuTru; }
        public String getSTenQuanCuTru() { return sTenQuanCuTru; }
        public void setSTenQuanCuTru(String sTenQuanCuTru) { this.sTenQuanCuTru = sTenQuanCuTru; }
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
    }
}
