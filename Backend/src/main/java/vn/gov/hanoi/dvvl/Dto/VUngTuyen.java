package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class VUngTuyen {

    public static class NopHoSoRequest {
        @NotBlank(message = "Vui lòng chọn tin tuyển dụng.")
        @JsonProperty("fkMaTin")
        private String fkMaTin;
        @JsonProperty("sGhiChu")
        private String sGhiChu;

        public String getFkMaTin() { return fkMaTin; }
        public void setFkMaTin(String fkMaTin) { this.fkMaTin = fkMaTin; }
        public String getSGhiChu() { return sGhiChu; }
        public void setSGhiChu(String sGhiChu) { this.sGhiChu = sGhiChu; }
    }

    public static class CapNhatTienTrinhRequest {
        @NotNull(message = "Vui lòng chọn trạng thái xét duyệt.")
        @JsonProperty("iTrangThaiUngTuyen")
        private Integer iTrangThaiUngTuyen; // 1, 2, 3, 4
        @JsonProperty("dThoiGianPhongVan")
        private LocalDateTime dThoiGianPhongVan;
        @JsonProperty("sDiaDiemPhongVan")
        private String sDiaDiemPhongVan;
        @JsonProperty("sHinhThucPhongVan")
        private String sHinhThucPhongVan;
        @JsonProperty("sGhiChuPhongVan")
        private String sGhiChuPhongVan;

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
    }

    public static class HuyUngTuyenRequest {
        @JsonProperty("sLyDo")
        private String sLyDo;

        public String getSLyDo() { return sLyDo; }
        public void setSLyDo(String sLyDo) { this.sLyDo = sLyDo; }
    }

    public static class LichSuView {
        @JsonProperty("pkMaLichSu")
        private Long pkMaLichSu;
        @JsonProperty("iTrangThaiCu")
        private Integer iTrangThaiCu;
        @JsonProperty("iTrangThaiMoi")
        private Integer iTrangThaiMoi;
        @JsonProperty("tenTrangThaiMoi")
        private String tenTrangThaiMoi;
        @JsonProperty("fkMaNguoiThucHien")
        private String fkMaNguoiThucHien;
        @JsonProperty("sGhiChu")
        private String sGhiChu;
        @JsonProperty("dThoiGianThayDoi")
        private LocalDateTime dThoiGianThayDoi;

        public Long getPkMaLichSu() { return pkMaLichSu; }
        public void setPkMaLichSu(Long pkMaLichSu) { this.pkMaLichSu = pkMaLichSu; }
        public Integer getITrangThaiCu() { return iTrangThaiCu; }
        public void setITrangThaiCu(Integer iTrangThaiCu) { this.iTrangThaiCu = iTrangThaiCu; }
        public Integer getITrangThaiMoi() { return iTrangThaiMoi; }
        public void setITrangThaiMoi(Integer iTrangThaiMoi) { this.iTrangThaiMoi = iTrangThaiMoi; }
        public String getTenTrangThaiMoi() { return tenTrangThaiMoi; }
        public void setTenTrangThaiMoi(String tenTrangThaiMoi) { this.tenTrangThaiMoi = tenTrangThaiMoi; }
        public String getFkMaNguoiThucHien() { return fkMaNguoiThucHien; }
        public void setFkMaNguoiThucHien(String fkMaNguoiThucHien) { this.fkMaNguoiThucHien = fkMaNguoiThucHien; }
        public String getSGhiChu() { return sGhiChu; }
        public void setSGhiChu(String sGhiChu) { this.sGhiChu = sGhiChu; }
        public LocalDateTime getDThoiGianThayDoi() { return dThoiGianThayDoi; }
        public void setDThoiGianThayDoi(LocalDateTime dThoiGianThayDoi) { this.dThoiGianThayDoi = dThoiGianThayDoi; }
    }

    public static class UngTuyenView {
        @JsonProperty("pkMaUngTuyen")
        private String pkMaUngTuyen;
        @JsonProperty("fkMaTin")
        private String fkMaTin;
        @JsonProperty("sTieuDeTin")
        private String sTieuDeTin;
        @JsonProperty("sViTriChucDanh")
        private String sViTriChucDanh;
        @JsonProperty("sTenDoanhNghiep")
        private String sTenDoanhNghiep;
        @JsonProperty("fkMaUngVien")
        private String fkMaUngVien;
        @JsonProperty("sHoTenUngVien")
        private String sHoTenUngVien;
        @JsonProperty("sSDTUngVien")
        private String sSDTUngVien;
        @JsonProperty("sEmailUngVien")
        private String sEmailUngVien;
        @JsonProperty("sTrinhDoChuyenMon")
        private String sTrinhDoChuyenMon;
        @JsonProperty("sKyNangTinHoc")
        private String sKyNangTinHoc;
        @JsonProperty("sKinhNghiemLamViec")
        private String sKinhNghiemLamViec;
        @JsonProperty("sDuongDanCV")
        private String sDuongDanCV;
        @JsonProperty("dNgayNop")
        private LocalDateTime dNgayNop;
        @JsonProperty("iTrangThaiUngTuyen")
        private Integer iTrangThaiUngTuyen;
        @JsonProperty("tenTrangThaiUngTuyen")
        private String tenTrangThaiUngTuyen;
        @JsonProperty("dThoiGianPhongVan")
        private LocalDateTime dThoiGianPhongVan;
        @JsonProperty("sDiaDiemPhongVan")
        private String sDiaDiemPhongVan;
        @JsonProperty("sHinhThucPhongVan")
        private String sHinhThucPhongVan;
        @JsonProperty("sGhiChuPhongVan")
        private String sGhiChuPhongVan;
        @JsonProperty("lichSu")
        private List<LichSuView> lichSu;

        public String getPkMaUngTuyen() { return pkMaUngTuyen; }
        public void setPkMaUngTuyen(String pkMaUngTuyen) { this.pkMaUngTuyen = pkMaUngTuyen; }
        public String getFkMaTin() { return fkMaTin; }
        public void setFkMaTin(String fkMaTin) { this.fkMaTin = fkMaTin; }
        public String getSTieuDeTin() { return sTieuDeTin; }
        public void setSTieuDeTin(String sTieuDeTin) { this.sTieuDeTin = sTieuDeTin; }
        public String getSViTriChucDanh() { return sViTriChucDanh; }
        public void setSViTriChucDanh(String sViTriChucDanh) { this.sViTriChucDanh = sViTriChucDanh; }
        public String getSTenDoanhNghiep() { return sTenDoanhNghiep; }
        public void setSTenDoanhNghiep(String sTenDoanhNghiep) { this.sTenDoanhNghiep = sTenDoanhNghiep; }
        public String getFkMaUngVien() { return fkMaUngVien; }
        public void setFkMaUngVien(String fkMaUngVien) { this.fkMaUngVien = fkMaUngVien; }
        public String getSHoTenUngVien() { return sHoTenUngVien; }
        public void setSHoTenUngVien(String sHoTenUngVien) { this.sHoTenUngVien = sHoTenUngVien; }
        public String getSSDTUngVien() { return sSDTUngVien; }
        public void setSSDTUngVien(String sSDTUngVien) { this.sSDTUngVien = sSDTUngVien; }
        public String getSEmailUngVien() { return sEmailUngVien; }
        public void setSEmailUngVien(String sEmailUngVien) { this.sEmailUngVien = sEmailUngVien; }
        public String getSTrinhDoChuyenMon() { return sTrinhDoChuyenMon; }
        public void setSTrinhDoChuyenMon(String sTrinhDoChuyenMon) { this.sTrinhDoChuyenMon = sTrinhDoChuyenMon; }
        public String getSKyNangTinHoc() { return sKyNangTinHoc; }
        public void setSKyNangTinHoc(String sKyNangTinHoc) { this.sKyNangTinHoc = sKyNangTinHoc; }
        public String getSKinhNghiemLamViec() { return sKinhNghiemLamViec; }
        public void setSKinhNghiemLamViec(String sKinhNghiemLamViec) { this.sKinhNghiemLamViec = sKinhNghiemLamViec; }
        public String getSDuongDanCV() { return sDuongDanCV; }
        public void setSDuongDanCV(String sDuongDanCV) { this.sDuongDanCV = sDuongDanCV; }
        public LocalDateTime getDNgayNop() { return dNgayNop; }
        public void setDNgayNop(LocalDateTime dNgayNop) { this.dNgayNop = dNgayNop; }
        public Integer getITrangThaiUngTuyen() { return iTrangThaiUngTuyen; }
        public void setITrangThaiUngTuyen(Integer iTrangThaiUngTuyen) { this.iTrangThaiUngTuyen = iTrangThaiUngTuyen; }
        public String getTenTrangThaiUngTuyen() { return tenTrangThaiUngTuyen; }
        public void setTenTrangThaiUngTuyen(String tenTrangThaiUngTuyen) { this.tenTrangThaiUngTuyen = tenTrangThaiUngTuyen; }
        public LocalDateTime getDThoiGianPhongVan() { return dThoiGianPhongVan; }
        public void setDThoiGianPhongVan(LocalDateTime dThoiGianPhongVan) { this.dThoiGianPhongVan = dThoiGianPhongVan; }
        public String getSDiaDiemPhongVan() { return sDiaDiemPhongVan; }
        public void setSDiaDiemPhongVan(String sDiaDiemPhongVan) { this.sDiaDiemPhongVan = sDiaDiemPhongVan; }
        public String getSHinhThucPhongVan() { return sHinhThucPhongVan; }
        public void setSHinhThucPhongVan(String sHinhThucPhongVan) { this.sHinhThucPhongVan = sHinhThucPhongVan; }
        public String getSGhiChuPhongVan() { return sGhiChuPhongVan; }
        public void setSGhiChuPhongVan(String sGhiChuPhongVan) { this.sGhiChuPhongVan = sGhiChuPhongVan; }
        public List<LichSuView> getLichSu() { return lichSu; }
        public void setLichSu(List<LichSuView> lichSu) { this.lichSu = lichSu; }
    }
}
