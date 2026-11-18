package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class VTaiKhoan {

    public static class DangKyRequest {
        @JsonProperty("fkMaVaiTro")
        private Integer fkMaVaiTro; // 1 = NTV, 2 = DN

        @NotBlank(message = "Vui lòng nhập địa chỉ Email.")
        @Email(message = "Email không đúng định dạng.")
        @JsonProperty("sEmail")
        private String sEmail;

        @NotBlank(message = "Vui lòng nhập mật khẩu.")
        @JsonProperty("sMatKhau")
        private String sMatKhau;

        @NotBlank(message = "Vui lòng nhập xác nhận mật khẩu.")
        @JsonProperty("xacNhanMatKhau")
        private String xacNhanMatKhau;

        @NotBlank(message = "Vui lòng nhập họ và tên.")
        @JsonProperty("sHoTen")
        private String sHoTen;

        @NotBlank(message = "Vui lòng nhập số điện thoại 10 chữ số.")
        @JsonProperty("sSDT")
        private String sSDT;

        @JsonProperty("sTenDoanhNghiep")
        private String sTenDoanhNghiep;

        @JsonProperty("sMaSoThue")
        private String sMaSoThue;

        public Integer getFkMaVaiTro() { return fkMaVaiTro; }
        public void setFkMaVaiTro(Integer fkMaVaiTro) { this.fkMaVaiTro = fkMaVaiTro; }
        public String getSEmail() { return sEmail; }
        public void setSEmail(String sEmail) { this.sEmail = sEmail; }
        public String getSMatKhau() { return sMatKhau; }
        public void setSMatKhau(String sMatKhau) { this.sMatKhau = sMatKhau; }
        public String getXacNhanMatKhau() { return xacNhanMatKhau; }
        public void setXacNhanMatKhau(String xacNhanMatKhau) { this.xacNhanMatKhau = xacNhanMatKhau; }
        public String getSHoTen() { return sHoTen; }
        public void setSHoTen(String sHoTen) { this.sHoTen = sHoTen; }
        public String getSSDT() { return sSDT; }
        public void setSSDT(String sSDT) { this.sSDT = sSDT; }
        public String getSTenDoanhNghiep() { return sTenDoanhNghiep; }
        public void setSTenDoanhNghiep(String sTenDoanhNghiep) { this.sTenDoanhNghiep = sTenDoanhNghiep; }
        public String getSMaSoThue() { return sMaSoThue; }
        public void setSMaSoThue(String sMaSoThue) { this.sMaSoThue = sMaSoThue; }
    }

    public static class KichHoatRequest {
        @JsonProperty("sEmail")
        private String sEmail;
        @JsonProperty("maOtp")
        private String maOtp;

        public String getSEmail() { return sEmail; }
        public void setSEmail(String sEmail) { this.sEmail = sEmail; }
        public String getMaOtp() { return maOtp; }
        public void setMaOtp(String maOtp) { this.maOtp = maOtp; }
    }

    public static class DangNhapRequest {
        @NotBlank(message = "Vui lòng nhập Email đăng nhập.")
        @JsonProperty("sEmail")
        private String sEmail;
        @NotBlank(message = "Vui lòng nhập mật khẩu.")
        @JsonProperty("sMatKhau")
        private String sMatKhau;

        public String getSEmail() { return sEmail; }
        public void setSEmail(String sEmail) { this.sEmail = sEmail; }
        public String getSMatKhau() { return sMatKhau; }
        public void setSMatKhau(String sMatKhau) { this.sMatKhau = sMatKhau; }
    }

    public static class CapNhatTaiKhoanRequest {
        @NotBlank(message = "Họ và tên không được để trống.")
        @JsonProperty("sHoTen")
        private String sHoTen;
        @NotBlank(message = "Số điện thoại không được để trống.")
        @JsonProperty("sSDT")
        private String sSDT;

        public String getSHoTen() { return sHoTen; }
        public void setSHoTen(String sHoTen) { this.sHoTen = sHoTen; }
        public String getSSDT() { return sSDT; }
        public void setSSDT(String sSDT) { this.sSDT = sSDT; }
    }

    public static class DoiMatKhauRequest {
        @NotBlank(message = "Vui lòng nhập mật khẩu hiện tại.")
        @JsonProperty("matKhauHienTai")
        private String matKhauHienTai;
        @NotBlank(message = "Vui lòng nhập mật khẩu mới.")
        @JsonProperty("matKhauMoi")
        private String matKhauMoi;
        @NotBlank(message = "Vui lòng xác nhận mật khẩu mới.")
        @JsonProperty("xacNhanMatKhauMoi")
        private String xacNhanMatKhauMoi;

        public String getMatKhauHienTai() { return matKhauHienTai; }
        public void setMatKhauHienTai(String matKhauHienTai) { this.matKhauHienTai = matKhauHienTai; }
        public String getMatKhauMoi() { return matKhauMoi; }
        public void setMatKhauMoi(String matKhauMoi) { this.matKhauMoi = matKhauMoi; }
        public String getXacNhanMatKhauMoi() { return xacNhanMatKhauMoi; }
        public void setXacNhanMatKhauMoi(String xacNhanMatKhauMoi) { this.xacNhanMatKhauMoi = xacNhanMatKhauMoi; }
    }

    public static class PhanQuyenRequest {
        @JsonProperty("fkMaVaiTro")
        private Integer fkMaVaiTro;

        public Integer getFkMaVaiTro() { return fkMaVaiTro; }
        public void setFkMaVaiTro(Integer fkMaVaiTro) { this.fkMaVaiTro = fkMaVaiTro; }
    }

    public static class KhoaMoRequest {
        @JsonProperty("bBiKhoa")
        private Boolean bBiKhoa;
        @JsonProperty("sLyDoKhoa")
        private String sLyDoKhoa;

        public Boolean getBBiKhoa() { return bBiKhoa; }
        public void setBBiKhoa(Boolean bBiKhoa) { this.bBiKhoa = bBiKhoa; }
        public String getSLyDoKhoa() { return sLyDoKhoa; }
        public void setSLyDoKhoa(String sLyDoKhoa) { this.sLyDoKhoa = sLyDoKhoa; }
    }

    public static class TaiKhoanView {
        @JsonProperty("pkMaTaiKhoan")
        private String pkMaTaiKhoan;
        @JsonProperty("sEmail")
        private String sEmail;
        @JsonProperty("fkMaVaiTro")
        private Integer fkMaVaiTro;
        @JsonProperty("sTenVaiTro")
        private String sTenVaiTro;
        @JsonProperty("sHoTen")
        private String sHoTen;
        @JsonProperty("sSDT")
        private String sSDT;
        @JsonProperty("sDuongDanAnhDaiDien")
        private String sDuongDanAnhDaiDien;
        @JsonProperty("bDaKichHoat")
        private Boolean bDaKichHoat;
        @JsonProperty("bBiKhoa")
        private Boolean bBiKhoa;
        @JsonProperty("iSoLanDangNhapSai")
        private Integer iSoLanDangNhapSai;
        @JsonProperty("dKhoaDenLuc")
        private LocalDateTime dKhoaDenLuc;
        @JsonProperty("sLyDoKhoa")
        private String sLyDoKhoa;
        @JsonProperty("dNgayTao")
        private LocalDateTime dNgayTao;
        @JsonProperty("duongDanDich")
        private String duongDanDich;
        @JsonProperty("token")
        private String token;

        public String getPkMaTaiKhoan() { return pkMaTaiKhoan; }
        public void setPkMaTaiKhoan(String pkMaTaiKhoan) { this.pkMaTaiKhoan = pkMaTaiKhoan; }
        public String getSEmail() { return sEmail; }
        public void setSEmail(String sEmail) { this.sEmail = sEmail; }
        public Integer getFkMaVaiTro() { return fkMaVaiTro; }
        public void setFkMaVaiTro(Integer fkMaVaiTro) { this.fkMaVaiTro = fkMaVaiTro; }
        public String getSTenVaiTro() { return sTenVaiTro; }
        public void setSTenVaiTro(String sTenVaiTro) { this.sTenVaiTro = sTenVaiTro; }
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
        public LocalDateTime getDNgayTao() { return dNgayTao; }
        public void setDNgayTao(LocalDateTime dNgayTao) { this.dNgayTao = dNgayTao; }
        public String getDuongDanDich() { return duongDanDich; }
        public void setDuongDanDich(String duongDanDich) { this.duongDanDich = duongDanDich; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
