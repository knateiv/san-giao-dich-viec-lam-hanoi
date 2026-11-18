package vn.gov.hanoi.dvvl.Config;

public class UserPrincipal {
    private final String pkMaTaiKhoan;
    private final Integer fkMaVaiTro;
    private final String sEmail;
    private final String sHoTen;

    public UserPrincipal(String pkMaTaiKhoan, Integer fkMaVaiTro, String sEmail, String sHoTen) {
        this.pkMaTaiKhoan = pkMaTaiKhoan;
        this.fkMaVaiTro = fkMaVaiTro;
        this.sEmail = sEmail;
        this.sHoTen = sHoTen;
    }

    public String getPkMaTaiKhoan() { return pkMaTaiKhoan; }
    public Integer getFkMaVaiTro() { return fkMaVaiTro; }
    public String getSEmail() { return sEmail; }
    public String getSHoTen() { return sHoTen; }
}
