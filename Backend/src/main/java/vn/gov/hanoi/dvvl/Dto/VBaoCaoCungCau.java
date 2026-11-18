package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class VBaoCaoCungCau {

    public static class DongTheoQuan {
        @JsonProperty("pkMaQuanHuyen")
        private String pkMaQuanHuyen;
        @JsonProperty("sTenQuanHuyen")
        private String sTenQuanHuyen;
        @JsonProperty("sKhuVuc")
        private String sKhuVuc;
        @JsonProperty("iTongCung")
        private int iTongCung;
        @JsonProperty("iTongCau")
        private int iTongCau;
        @JsonProperty("iChenhLech")
        private int iChenhLech;
        @JsonProperty("iSoHoSoNop")
        private int iSoHoSoNop;
        @JsonProperty("iSoTrungTuyen")
        private int iSoTrungTuyen;

        public String getPkMaQuanHuyen() { return pkMaQuanHuyen; }
        public void setPkMaQuanHuyen(String pkMaQuanHuyen) { this.pkMaQuanHuyen = pkMaQuanHuyen; }
        public String getSTenQuanHuyen() { return sTenQuanHuyen; }
        public void setSTenQuanHuyen(String sTenQuanHuyen) { this.sTenQuanHuyen = sTenQuanHuyen; }
        public String getSKhuVuc() { return sKhuVuc; }
        public void setSKhuVuc(String sKhuVuc) { this.sKhuVuc = sKhuVuc; }
        public int getITongCung() { return iTongCung; }
        public void setITongCung(int iTongCung) { this.iTongCung = iTongCung; }
        public int getITongCau() { return iTongCau; }
        public void setITongCau(int iTongCau) { this.iTongCau = iTongCau; }
        public int getIChenhLech() { return iChenhLech; }
        public void setIChenhLech(int iChenhLech) { this.iChenhLech = iChenhLech; }
        public int getISoHoSoNop() { return iSoHoSoNop; }
        public void setISoHoSoNop(int iSoHoSoNop) { this.iSoHoSoNop = iSoHoSoNop; }
        public int getISoTrungTuyen() { return iSoTrungTuyen; }
        public void setISoTrungTuyen(int iSoTrungTuyen) { this.iSoTrungTuyen = iSoTrungTuyen; }
    }

    public static class DongTheoNganh {
        @JsonProperty("pkMaNganhNghe")
        private Integer pkMaNganhNghe;
        @JsonProperty("sTenNganhNghe")
        private String sTenNganhNghe;
        @JsonProperty("sNhomNganh")
        private String sNhomNganh;
        @JsonProperty("iSoTinTuyenDung")
        private int iSoTinTuyenDung;
        @JsonProperty("iTongCauChiTieu")
        private int iTongCauChiTieu;
        @JsonProperty("iTongCungHoSo")
        private int iTongCungHoSo;
        @JsonProperty("iTongLuotUngTuyen")
        private int iTongLuotUngTuyen;
        @JsonProperty("iSoTrungTuyen")
        private int iSoTrungTuyen;

        public Integer getPkMaNganhNghe() { return pkMaNganhNghe; }
        public void setPkMaNganhNghe(Integer pkMaNganhNghe) { this.pkMaNganhNghe = pkMaNganhNghe; }
        public String getSTenNganhNghe() { return sTenNganhNghe; }
        public void setSTenNganhNghe(String sTenNganhNghe) { this.sTenNganhNghe = sTenNganhNghe; }
        public String getSNhomNganh() { return sNhomNganh; }
        public void setSNhomNganh(String sNhomNganh) { this.sNhomNganh = sNhomNganh; }
        public int getISoTinTuyenDung() { return iSoTinTuyenDung; }
        public void setISoTinTuyenDung(int iSoTinTuyenDung) { this.iSoTinTuyenDung = iSoTinTuyenDung; }
        public int getITongCauChiTieu() { return iTongCauChiTieu; }
        public void setITongCauChiTieu(int iTongCauChiTieu) { this.iTongCauChiTieu = iTongCauChiTieu; }
        public int getITongCungHoSo() { return iTongCungHoSo; }
        public void setITongCungHoSo(int iTongCungHoSo) { this.iTongCungHoSo = iTongCungHoSo; }
        public int getITongLuotUngTuyen() { return iTongLuotUngTuyen; }
        public void setITongLuotUngTuyen(int iTongLuotUngTuyen) { this.iTongLuotUngTuyen = iTongLuotUngTuyen; }
        public int getISoTrungTuyen() { return iSoTrungTuyen; }
        public void setISoTrungTuyen(int iSoTrungTuyen) { this.iSoTrungTuyen = iSoTrungTuyen; }
    }

    public static class TongHopView {
        @JsonProperty("tongCung")
        private int tongCung;
        @JsonProperty("tongCau")
        private int tongCau;
        @JsonProperty("tongHoSoUngTuyen")
        private int tongHoSoUngTuyen;
        @JsonProperty("tongTrungTuyen")
        private int tongTrungTuyen;
        @JsonProperty("tongDoanhNghiepDaXacMinh")
        private int tongDoanhNghiepDaXacMinh;
        @JsonProperty("tongTinCongKhai")
        private int tongTinCongKhai;
        @JsonProperty("theoQuanHuyen")
        private List<DongTheoQuan> theoQuanHuyen;
        @JsonProperty("theoNganhNghe")
        private List<DongTheoNganh> theoNganhNghe;

        public int getTongCung() { return tongCung; }
        public void setTongCung(int tongCung) { this.tongCung = tongCung; }
        public int getTongCau() { return tongCau; }
        public void setTongCau(int tongCau) { this.tongCau = tongCau; }
        public int getTongHoSoUngTuyen() { return tongHoSoUngTuyen; }
        public void setTongHoSoUngTuyen(int tongHoSoUngTuyen) { this.tongHoSoUngTuyen = tongHoSoUngTuyen; }
        public int getTongTrungTuyen() { return tongTrungTuyen; }
        public void setTongTrungTuyen(int tongTrungTuyen) { this.tongTrungTuyen = tongTrungTuyen; }
        public int getTongDoanhNghiepDaXacMinh() { return tongDoanhNghiepDaXacMinh; }
        public void setTongDoanhNghiepDaXacMinh(int tongDoanhNghiepDaXacMinh) { this.tongDoanhNghiepDaXacMinh = tongDoanhNghiepDaXacMinh; }
        public int getTongTinCongKhai() { return tongTinCongKhai; }
        public void setTongTinCongKhai(int tongTinCongKhai) { this.tongTinCongKhai = tongTinCongKhai; }
        public List<DongTheoQuan> getTheoQuanHuyen() { return theoQuanHuyen; }
        public void setTheoQuanHuyen(List<DongTheoQuan> theoQuanHuyen) { this.theoQuanHuyen = theoQuanHuyen; }
        public List<DongTheoNganh> getTheoNganhNghe() { return theoNganhNghe; }
        public void setTheoNganhNghe(List<DongTheoNganh> theoNganhNghe) { this.theoNganhNghe = theoNganhNghe; }
    }
}
