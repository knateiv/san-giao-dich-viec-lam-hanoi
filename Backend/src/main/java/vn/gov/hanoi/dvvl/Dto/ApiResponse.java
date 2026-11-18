package vn.gov.hanoi.dvvl.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean thanhCong;
    private String thongBao;
    private T duLieu;
    private Long thoiGianXuLyMs;

    public ApiResponse() {}

    public ApiResponse(boolean thanhCong, String thongBao, T duLieu, Long thoiGianXuLyMs) {
        this.thanhCong = thanhCong;
        this.thongBao = thongBao;
        this.duLieu = duLieu;
        this.thoiGianXuLyMs = thoiGianXuLyMs;
    }

    public static <T> ApiResponse<T> ok(String thongBao, T duLieu) {
        return new ApiResponse<>(true, thongBao, duLieu, null);
    }

    public static <T> ApiResponse<T> ok(String thongBao, T duLieu, long ms) {
        return new ApiResponse<>(true, thongBao, duLieu, ms);
    }

    public static <T> ApiResponse<T> fail(String thongBao) {
        return new ApiResponse<>(false, thongBao, null, null);
    }

    public boolean isThanhCong() { return thanhCong; }
    public void setThanhCong(boolean thanhCong) { this.thanhCong = thanhCong; }
    public String getThongBao() { return thongBao; }
    public void setThongBao(String thongBao) { this.thongBao = thongBao; }
    public T getDuLieu() { return duLieu; }
    public void setDuLieu(T duLieu) { this.duLieu = duLieu; }
    public Long getThoiGianXuLyMs() { return thoiGianXuLyMs; }
    public void setThoiGianXuLyMs(Long thoiGianXuLyMs) { this.thoiGianXuLyMs = thoiGianXuLyMs; }
}
