package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class VDoanhNghiep {

    public static class PhapLyRequest {
        @NotBlank(message = "Vui lòng nhập tên doanh nghiệp theo ĐKKD.")
        @JsonProperty("sTenDoanhNghiep")
        private String sTenDoanhNghiep;

        @JsonProperty("sTenVietTat")
        private String sTenVietTat;

        @NotBlank(message = "Vui lòng nhập mã số thuế (10 hoặc 13 số).")
        @JsonProperty("sMaSoThue")
        private String sMaSoThue;

        @NotBlank(message = "Vui lòng nhập địa chỉ trụ sở chính.")
        @JsonProperty("sDiaChiTruSo")
        private String sDiaChiTruSo;

        @NotBlank(message = "Vui lòng chọn quận/huyện trụ sở.")
        @JsonProperty("fkMaQuanTruSo")
        private String fkMaQuanTruSo;

        @NotBlank(message = "Vui lòng nhập lĩnh vực hoạt động chính.")
        @JsonProperty("sLinhVucHoatDong")
        private String sLinhVucHoatDong;

        @NotBlank(message = "Vui lòng nhập người đại diện pháp luật.")
        @JsonProperty("sNguoiDaiDien")
        private String sNguoiDaiDien;

        @NotBlank(message = "Vui lòng nhập tên người liên hệ tuyển dụng.")
        @JsonProperty("sTenNguoiLienHe")
        private String sTenNguoiLienHe;

        @NotBlank(message = "Vui lòng nhập số điện thoại liên hệ.")
        @JsonProperty("sSDTNguoiLienHe")
        private String sSDTNguoiLienHe;

        @NotBlank(message = "Vui lòng nhập email liên hệ.")
        @JsonProperty("sEmailNguoiLienHe")
        private String sEmailNguoiLienHe;

        public String getSTenDoanhNghiep() { return sTenDoanhNghiep; }
        public void setSTenDoanhNghiep(String sTenDoanhNghiep) { this.sTenDoanhNghiep = sTenDoanhNghiep; }
        public String getSTenVietTat() { return sTenVietTat; }
        public void setSTenVietTat(String sTenVietTat) { this.sTenVietTat = sTenVietTat; }
        public String getSMaSoThue() { return sMaSoThue; }
        public void setSMaSoThue(String sMaSoThue) { this.sMaSoThue = sMaSoThue; }
        public String getSDiaChiTruSo() { return sDiaChiTruSo; }
        public void setSDiaChiTruSo(String sDiaChiTruSo) { this.sDiaChiTruSo = sDiaChiTruSo; }
        public String getFkMaQuanTruSo() { return fkMaQuanTruSo; }
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
    }

    public static class DoanhNghiepView {
        @JsonProperty("pkMaDoanhNghiep")
        private String pkMaDoanhNghiep;
        @JsonProperty("fkMaTaiKhoan")
        private String fkMaTaiKhoan;
        @JsonProperty("sTenDoanhNghiep")
        private String sTenDoanhNghiep;
        @JsonProperty("sTenVietTat")
        private String sTenVietTat;
        @JsonProperty("sMaSoThue")
        private String sMaSoThue;
        @JsonProperty("sDuongDanGiayPhep")
        private String sDuongDanGiayPhep;
        @JsonProperty("sDiaChiTruSo")
        private String sDiaChiTruSo;
        @JsonProperty("fkMaQuanTruSo")
        private String fkMaQuanTruSo;
        @JsonProperty("sTenQuanTruSo")
        private String sTenQuanTruSo;
        @JsonProperty("sLinhVucHoatDong")
        private String sLinhVucHoatDong;
        @JsonProperty("sNguoiDaiDien")
        private String sNguoiDaiDien;
        @JsonProperty("sTenNguoiLienHe")
        private String sTenNguoiLienHe;
        @JsonProperty("sSDTNguoiLienHe")
        private String sSDTNguoiLienHe;
        @JsonProperty("sEmailNguoiLienHe")
        private String sEmailNguoiLienHe;
        @JsonProperty("iTrangThaiThamDinh")
        private Integer iTrangThaiThamDinh;
        @JsonProperty("tenTrangThaiThamDinh")
        private String tenTrangThaiThamDinh;
        @JsonProperty("sGhiChuThamDinh")
        private String sGhiChuThamDinh;

        public String getPkMaDoanhNghiep() { return pkMaDoanhNghiep; }
        public void setPkMaDoanhNghiep(String pkMaDoanhNghiep) { this.pkMaDoanhNghiep = pkMaDoanhNghiep; }
        public String getFkMaTaiKhoan() { return fkMaTaiKhoan; }
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
        public String getFkMaQuanTruSo() { return fkMaQuanTruSo; }
        public void setFkMaQuanTruSo(String fkMaQuanTruSo) { this.fkMaQuanTruSo = fkMaQuanTruSo; }
        public String getSTenQuanTruSo() { return sTenQuanTruSo; }
        public void setSTenQuanTruSo(String sTenQuanTruSo) { this.sTenQuanTruSo = sTenQuanTruSo; }
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
        public String getTenTrangThaiThamDinh() { return tenTrangThaiThamDinh; }
        public void setTenTrangThaiThamDinh(String tenTrangThaiThamDinh) { this.tenTrangThaiThamDinh = tenTrangThaiThamDinh; }
        public String getSGhiChuThamDinh() { return sGhiChuThamDinh; }
        public void setSGhiChuThamDinh(String sGhiChuThamDinh) { this.sGhiChuThamDinh = sGhiChuThamDinh; }
    }
}
