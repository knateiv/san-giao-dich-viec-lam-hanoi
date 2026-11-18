package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VPheDuyet {

    public static class ThamDinhDnRequest {
        @JsonProperty("iTrangThaiThamDinh")
        private Integer iTrangThaiThamDinh; // 1 = Hợp lệ, 2 = Từ chối
        @JsonProperty("sGhiChuThamDinh")
        private String sGhiChuThamDinh;

        public Integer getITrangThaiThamDinh() { return iTrangThaiThamDinh; }
        public void setITrangThaiThamDinh(Integer iTrangThaiThamDinh) { this.iTrangThaiThamDinh = iTrangThaiThamDinh; }
        public String getSGhiChuThamDinh() { return sGhiChuThamDinh; }
        public void setSGhiChuThamDinh(String sGhiChuThamDinh) { this.sGhiChuThamDinh = sGhiChuThamDinh; }
    }

    public static class DuyetTinRequest {
        @JsonProperty("iTrangThaiDuyet")
        private Integer iTrangThaiDuyet; // 1 = Phê duyệt, 2 = Từ chối
        @JsonProperty("sLyDoTuChoi")
        private String sLyDoTuChoi;

        public Integer getITrangThaiDuyet() { return iTrangThaiDuyet; }
        public void setITrangThaiDuyet(Integer iTrangThaiDuyet) { this.iTrangThaiDuyet = iTrangThaiDuyet; }
        public String getSLyDoTuChoi() { return sLyDoTuChoi; }
        public void setSLyDoTuChoi(String sLyDoTuChoi) { this.sLyDoTuChoi = sLyDoTuChoi; }
    }

    public static class GoKhanCapRequest {
        @JsonProperty("sLyDo")
        private String sLyDo;

        public String getSLyDo() { return sLyDo; }
        public void setSLyDo(String sLyDo) { this.sLyDo = sLyDo; }
    }
}
