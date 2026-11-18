package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class VNhatKyHeThong {
    @JsonProperty("pkMaNhatKy")
    private Long pkMaNhatKy;
    @JsonProperty("fkMaTaiKhoan")
    private String fkMaTaiKhoan;
    @JsonProperty("tenNguoiThucHien")
    private String tenNguoiThucHien;
    @JsonProperty("sEmail")
    private String sEmail;
    @JsonProperty("sLoaiHanhDong")
    private String sLoaiHanhDong;
    @JsonProperty("sMucDo")
    private String sMucDo;
    @JsonProperty("sDiaChiIP")
    private String sDiaChiIP;
    @JsonProperty("sTrinhDuyet")
    private String sTrinhDuyet;
    @JsonProperty("dThoiGianGhi")
    private LocalDateTime dThoiGianGhi;
    @JsonProperty("sDuLieuCu")
    private String sDuLieuCu;
    @JsonProperty("sDuLieuMoi")
    private String sDuLieuMoi;

    public Long getPkMaNhatKy() { return pkMaNhatKy; }
    public void setPkMaNhatKy(Long pkMaNhatKy) { this.pkMaNhatKy = pkMaNhatKy; }
    public String getFkMaTaiKhoan() { return fkMaTaiKhoan; }
    public void setFkMaTaiKhoan(String fkMaTaiKhoan) { this.fkMaTaiKhoan = fkMaTaiKhoan; }
    public String getTenNguoiThucHien() { return tenNguoiThucHien; }
    public void setTenNguoiThucHien(String tenNguoiThucHien) { this.tenNguoiThucHien = tenNguoiThucHien; }
    public String getSEmail() { return sEmail; }
    public void setSEmail(String sEmail) { this.sEmail = sEmail; }
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
