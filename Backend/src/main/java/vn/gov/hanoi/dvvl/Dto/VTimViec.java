package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VTimViec {

    public static class UngVienCongKhaiView {
        @JsonProperty("pkMaUngVien")
        private String pkMaUngVien;
        @JsonProperty("sHoTen")
        private String sHoTen;
        @JsonProperty("sGioiTinh")
        private String sGioiTinh;
        @JsonProperty("iDoTuoi")
        private Integer iDoTuoi;
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
        @JsonProperty("iTyLeHoanThien")
        private Integer iTyLeHoanThien;
        @JsonProperty("bCoTepCV")
        private Boolean bCoTepCV;
        @JsonProperty("sSoCCCDChe")
        private String sSoCCCDChe;
        @JsonProperty("sSDTChe")
        private String sSDTChe;
        @JsonProperty("sEmailChe")
        private String sEmailChe;

        public String getPkMaUngVien() { return pkMaUngVien; }
        public void setPkMaUngVien(String pkMaUngVien) { this.pkMaUngVien = pkMaUngVien; }
        public String getSHoTen() { return sHoTen; }
        public void setSHoTen(String sHoTen) { this.sHoTen = sHoTen; }
        public String getSGioiTinh() { return sGioiTinh; }
        public void setSGioiTinh(String sGioiTinh) { this.sGioiTinh = sGioiTinh; }
        public Integer getIDoTuoi() { return iDoTuoi; }
        public void setIDoTuoi(Integer iDoTuoi) { this.iDoTuoi = iDoTuoi; }
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
        public Integer getITyLeHoanThien() { return iTyLeHoanThien; }
        public void setITyLeHoanThien(Integer iTyLeHoanThien) { this.iTyLeHoanThien = iTyLeHoanThien; }
        public Boolean getBCoTepCV() { return bCoTepCV; }
        public void setBCoTepCV(Boolean bCoTepCV) { this.bCoTepCV = bCoTepCV; }
        public String getSSoCCCDChe() { return sSoCCCDChe; }
        public void setSSoCCCDChe(String sSoCCCDChe) { this.sSoCCCDChe = sSoCCCDChe; }
        public String getSSDTChe() { return sSDTChe; }
        public void setSSDTChe(String sSDTChe) { this.sSDTChe = sSDTChe; }
        public String getSEmailChe() { return sEmailChe; }
        public void setSEmailChe(String sEmailChe) { this.sEmailChe = sEmailChe; }
    }

    public static class MoiUngTuyenRequest {
        @JsonProperty("fkMaUngVien")
        private String fkMaUngVien;
        @JsonProperty("fkMaTin")
        private String fkMaTin;
        @JsonProperty("loiNhan")
        private String loiNhan;

        public String getFkMaUngVien() { return fkMaUngVien; }
        public void setFkMaUngVien(String fkMaUngVien) { this.fkMaUngVien = fkMaUngVien; }
        public String getFkMaTin() { return fkMaTin; }
        public void setFkMaTin(String fkMaTin) { this.fkMaTin = fkMaTin; }
        public String getLoiNhan() { return loiNhan; }
        public void setLoiNhan(String loiNhan) { this.loiNhan = loiNhan; }
    }
}
