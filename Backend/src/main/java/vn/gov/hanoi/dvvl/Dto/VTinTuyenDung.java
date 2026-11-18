package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class VTinTuyenDung {

    public static class TinRequest {
        @NotBlank(message = "Vui lòng nhập tiêu đề tin tuyển dụng.")
        @JsonProperty("sTieuDe")
        private String sTieuDe;

        @NotBlank(message = "Vui lòng nhập vị trí / chức danh công việc.")
        @JsonProperty("sViTriChucDanh")
        private String sViTriChucDanh;

        @NotNull(message = "Vui lòng chọn ngành nghề tuyển dụng.")
        @JsonProperty("fkMaNganhNghe")
        private Integer fkMaNganhNghe;

        @NotBlank(message = "Vui lòng chọn quận/huyện làm việc.")
        @JsonProperty("fkMaQuanLamViec")
        private String fkMaQuanLamViec;

        @JsonProperty("sDiaChiCuThe")
        private String sDiaChiCuThe;

        @NotNull(message = "Vui lòng nhập số lượng cần tuyển.")
        @Min(value = 1, message = "Số lượng tuyển phải tối thiểu là 1.")
        @JsonProperty("iSoLuongTuyen")
        private Integer iSoLuongTuyen;

        @JsonProperty("sCapBac")
        private String sCapBac;

        @JsonProperty("sHinhThucLamViec")
        private String sHinhThucLamViec;

        @NotNull(message = "Vui lòng nhập mức lương tối thiểu.")
        @JsonProperty("fLuongToiThieu")
        private Double fLuongToiThieu;

        @NotNull(message = "Vui lòng nhập mức lương tối đa.")
        @JsonProperty("fLuongToiDa")
        private Double fLuongToiDa;

        @JsonProperty("sYeuCauKinhNghiem")
        private String sYeuCauKinhNghiem;

        @JsonProperty("sYeuCauHocVan")
        private String sYeuCauHocVan;

        @NotBlank(message = "Vui lòng nhập mô tả công việc.")
        @JsonProperty("sMoTaCongViec")
        private String sMoTaCongViec;

        @JsonProperty("sCheDoDaiNgo")
        private String sCheDoDaiNgo;

        @NotNull(message = "Vui lòng chọn hạn nộp hồ sơ.")
        @JsonProperty("dHanNopHoSo")
        private LocalDate dHanNopHoSo;

        public String getSTieuDe() { return sTieuDe; }
        public void setSTieuDe(String sTieuDe) { this.sTieuDe = sTieuDe; }
        public String getSViTriChucDanh() { return sViTriChucDanh; }
        public void setSViTriChucDanh(String sViTriChucDanh) { this.sViTriChucDanh = sViTriChucDanh; }
        public Integer getFkMaNganhNghe() { return fkMaNganhNghe; }
        public void setFkMaNganhNghe(Integer fkMaNganhNghe) { this.fkMaNganhNghe = fkMaNganhNghe; }
        public String getFkMaQuanLamViec() { return fkMaQuanLamViec; }
        public void setFkMaQuanLamViec(String fkMaQuanLamViec) { this.fkMaQuanLamViec = fkMaQuanLamViec; }
        public String getSDiaChiCuThe() { return sDiaChiCuThe; }
        public void setSDiaChiCuThe(String sDiaChiCuThe) { this.sDiaChiCuThe = sDiaChiCuThe; }
        public Integer getISoLuongTuyen() { return iSoLuongTuyen; }
        public void setISoLuongTuyen(Integer iSoLuongTuyen) { this.iSoLuongTuyen = iSoLuongTuyen; }
        public String getSCapBac() { return sCapBac; }
        public void setSCapBac(String sCapBac) { this.sCapBac = sCapBac; }
        public String getSHinhThucLamViec() { return sHinhThucLamViec; }
        public void setSHinhThucLamViec(String sHinhThucLamViec) { this.sHinhThucLamViec = sHinhThucLamViec; }
        public Double getFLuongToiThieu() { return fLuongToiThieu; }
        public void setFLuongToiThieu(Double fLuongToiThieu) { this.fLuongToiThieu = fLuongToiThieu; }
        public Double getFLuongToiDa() { return fLuongToiDa; }
        public void setFLuongToiDa(Double fLuongToiDa) { this.fLuongToiDa = fLuongToiDa; }
        public String getSYeuCauKinhNghiem() { return sYeuCauKinhNghiem; }
        public void setSYeuCauKinhNghiem(String sYeuCauKinhNghiem) { this.sYeuCauKinhNghiem = sYeuCauKinhNghiem; }
        public String getSYeuCauHocVan() { return sYeuCauHocVan; }
        public void setSYeuCauHocVan(String sYeuCauHocVan) { this.sYeuCauHocVan = sYeuCauHocVan; }
        public String getSMoTaCongViec() { return sMoTaCongViec; }
        public void setSMoTaCongViec(String sMoTaCongViec) { this.sMoTaCongViec = sMoTaCongViec; }
        public String getSCheDoDaiNgo() { return sCheDoDaiNgo; }
        public void setSCheDoDaiNgo(String sCheDoDaiNgo) { this.sCheDoDaiNgo = sCheDoDaiNgo; }
        public LocalDate getDHanNopHoSo() { return dHanNopHoSo; }
        public void setDHanNopHoSo(LocalDate dHanNopHoSo) { this.dHanNopHoSo = dHanNopHoSo; }
    }

    public static class GiaHanRequest {
        @NotNull(message = "Vui lòng chọn ngày hết hạn mới.")
        @JsonProperty("dHanNopMoi")
        private LocalDate dHanNopMoi;

        public LocalDate getDHanNopMoi() { return dHanNopMoi; }
        public void setDHanNopMoi(LocalDate dHanNopMoi) { this.dHanNopMoi = dHanNopMoi; }
    }

    public static class TinTuyenDungView {
        @JsonProperty("pkMaTin")
        private String pkMaTin;
        @JsonProperty("fkMaDoanhNghiep")
        private String fkMaDoanhNghiep;
        @JsonProperty("sTenDoanhNghiep")
        private String sTenDoanhNghiep;
        @JsonProperty("sTieuDe")
        private String sTieuDe;
        @JsonProperty("sViTriChucDanh")
        private String sViTriChucDanh;
        @JsonProperty("fkMaNganhNghe")
        private Integer fkMaNganhNghe;
        @JsonProperty("sTenNganhNghe")
        private String sTenNganhNghe;
        @JsonProperty("fkMaQuanLamViec")
        private String fkMaQuanLamViec;
        @JsonProperty("sTenQuanLamViec")
        private String sTenQuanLamViec;
        @JsonProperty("sDiaChiCuThe")
        private String sDiaChiCuThe;
        @JsonProperty("iSoLuongTuyen")
        private Integer iSoLuongTuyen;
        @JsonProperty("sCapBac")
        private String sCapBac;
        @JsonProperty("sHinhThucLamViec")
        private String sHinhThucLamViec;
        @JsonProperty("fLuongToiThieu")
        private Double fLuongToiThieu;
        @JsonProperty("fLuongToiDa")
        private Double fLuongToiDa;
        @JsonProperty("sYeuCauKinhNghiem")
        private String sYeuCauKinhNghiem;
        @JsonProperty("sYeuCauHocVan")
        private String sYeuCauHocVan;
        @JsonProperty("sMoTaCongViec")
        private String sMoTaCongViec;
        @JsonProperty("sCheDoDaiNgo")
        private String sCheDoDaiNgo;
        @JsonProperty("dHanNopHoSo")
        private LocalDate dHanNopHoSo;
        @JsonProperty("iTrangThaiDuyet")
        private Integer iTrangThaiDuyet;
        @JsonProperty("tenTrangThaiDuyet")
        private String tenTrangThaiDuyet;
        @JsonProperty("fkMaCanBoDuyet")
        private String fkMaCanBoDuyet;
        @JsonProperty("dNgayDuyet")
        private LocalDateTime dNgayDuyet;
        @JsonProperty("sLyDoTuChoi")
        private String sLyDoTuChoi;
        @JsonProperty("bDaDong")
        private Boolean bDaDong;
        @JsonProperty("soHoSoUngTuyen")
        private Long soHoSoUngTuyen;
        @JsonProperty("daNop")
        private Boolean daNop;

        public String getPkMaTin() { return pkMaTin; }
        public void setPkMaTin(String pkMaTin) { this.pkMaTin = pkMaTin; }
        public String getFkMaDoanhNghiep() { return fkMaDoanhNghiep; }
        public void setFkMaDoanhNghiep(String fkMaDoanhNghiep) { this.fkMaDoanhNghiep = fkMaDoanhNghiep; }
        public String getSTenDoanhNghiep() { return sTenDoanhNghiep; }
        public void setSTenDoanhNghiep(String sTenDoanhNghiep) { this.sTenDoanhNghiep = sTenDoanhNghiep; }
        public String getSTieuDe() { return sTieuDe; }
        public void setSTieuDe(String sTieuDe) { this.sTieuDe = sTieuDe; }
        public String getSViTriChucDanh() { return sViTriChucDanh; }
        public void setSViTriChucDanh(String sViTriChucDanh) { this.sViTriChucDanh = sViTriChucDanh; }
        public Integer getFkMaNganhNghe() { return fkMaNganhNghe; }
        public void setFkMaNganhNghe(Integer fkMaNganhNghe) { this.fkMaNganhNghe = fkMaNganhNghe; }
        public String getSTenNganhNghe() { return sTenNganhNghe; }
        public void setSTenNganhNghe(String sTenNganhNghe) { this.sTenNganhNghe = sTenNganhNghe; }
        public String getFkMaQuanLamViec() { return fkMaQuanLamViec; }
        public void setFkMaQuanLamViec(String fkMaQuanLamViec) { this.fkMaQuanLamViec = fkMaQuanLamViec; }
        public String getSTenQuanLamViec() { return sTenQuanLamViec; }
        public void setSTenQuanLamViec(String sTenQuanLamViec) { this.sTenQuanLamViec = sTenQuanLamViec; }
        public String getSDiaChiCuThe() { return sDiaChiCuThe; }
        public void setSDiaChiCuThe(String sDiaChiCuThe) { this.sDiaChiCuThe = sDiaChiCuThe; }
        public Integer getISoLuongTuyen() { return iSoLuongTuyen; }
        public void setISoLuongTuyen(Integer iSoLuongTuyen) { this.iSoLuongTuyen = iSoLuongTuyen; }
        public String getSCapBac() { return sCapBac; }
        public void setSCapBac(String sCapBac) { this.sCapBac = sCapBac; }
        public String getSHinhThucLamViec() { return sHinhThucLamViec; }
        public void setSHinhThucLamViec(String sHinhThucLamViec) { this.sHinhThucLamViec = sHinhThucLamViec; }
        public Double getFLuongToiThieu() { return fLuongToiThieu; }
        public void setFLuongToiThieu(Double fLuongToiThieu) { this.fLuongToiThieu = fLuongToiThieu; }
        public Double getFLuongToiDa() { return fLuongToiDa; }
        public void setFLuongToiDa(Double fLuongToiDa) { this.fLuongToiDa = fLuongToiDa; }
        public String getSYeuCauKinhNghiem() { return sYeuCauKinhNghiem; }
        public void setSYeuCauKinhNghiem(String sYeuCauKinhNghiem) { this.sYeuCauKinhNghiem = sYeuCauKinhNghiem; }
        public String getSYeuCauHocVan() { return sYeuCauHocVan; }
        public void setSYeuCauHocVan(String sYeuCauHocVan) { this.sYeuCauHocVan = sYeuCauHocVan; }
        public String getSMoTaCongViec() { return sMoTaCongViec; }
        public void setSMoTaCongViec(String sMoTaCongViec) { this.sMoTaCongViec = sMoTaCongViec; }
        public String getSCheDoDaiNgo() { return sCheDoDaiNgo; }
        public void setSCheDoDaiNgo(String sCheDoDaiNgo) { this.sCheDoDaiNgo = sCheDoDaiNgo; }
        public LocalDate getDHanNopHoSo() { return dHanNopHoSo; }
        public void setDHanNopHoSo(LocalDate dHanNopHoSo) { this.dHanNopHoSo = dHanNopHoSo; }
        public Integer getITrangThaiDuyet() { return iTrangThaiDuyet; }
        public void setITrangThaiDuyet(Integer iTrangThaiDuyet) { this.iTrangThaiDuyet = iTrangThaiDuyet; }
        public String getTenTrangThaiDuyet() { return tenTrangThaiDuyet; }
        public void setTenTrangThaiDuyet(String tenTrangThaiDuyet) { this.tenTrangThaiDuyet = tenTrangThaiDuyet; }
        public String getFkMaCanBoDuyet() { return fkMaCanBoDuyet; }
        public void setFkMaCanBoDuyet(String fkMaCanBoDuyet) { this.fkMaCanBoDuyet = fkMaCanBoDuyet; }
        public LocalDateTime getDNgayDuyet() { return dNgayDuyet; }
        public void setDNgayDuyet(LocalDateTime dNgayDuyet) { this.dNgayDuyet = dNgayDuyet; }
        public String getSLyDoTuChoi() { return sLyDoTuChoi; }
        public void setSLyDoTuChoi(String sLyDoTuChoi) { this.sLyDoTuChoi = sLyDoTuChoi; }
        public Boolean getBDaDong() { return bDaDong; }
        public void setBDaDong(Boolean bDaDong) { this.bDaDong = bDaDong; }
        public Long getSoHoSoUngTuyen() { return soHoSoUngTuyen; }
        public void setSoHoSoUngTuyen(Long soHoSoUngTuyen) { this.soHoSoUngTuyen = soHoSoUngTuyen; }
        public Boolean getDaNop() { return daNop; }
        public void setDaNop(Boolean daNop) { this.daNop = daNop; }
    }
}
