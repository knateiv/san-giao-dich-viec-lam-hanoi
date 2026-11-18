package vn.gov.hanoi.dvvl.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_TinTuyenDung", schema = "dbo")
public class MTinTuyenDung {

    @Id
    @Column(name = "PK_MaTin", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String pkMaTin;

    @Column(name = "FK_MaDoanhNghiep", columnDefinition = "CHAR(10)", length = 10, nullable = false)
    private String fkMaDoanhNghiep;

    @Column(name = "sTieuDe", length = 200, nullable = false)
    private String sTieuDe;

    @Column(name = "sViTriChucDanh", length = 200, nullable = false)
    private String sViTriChucDanh;

    @Column(name = "FK_MaNganhNghe", nullable = false)
    private Integer fkMaNganhNghe;

    @Column(name = "FK_MaQuanLamViec", columnDefinition = "CHAR(5)", length = 5, nullable = false)
    private String fkMaQuanLamViec;

    @Column(name = "sDiaChiCuThe", length = 255)
    private String sDiaChiCuThe;

    @Column(name = "iSoLuongTuyen", nullable = false)
    private Integer iSoLuongTuyen = 1;

    @Column(name = "sCapBac", length = 50)
    private String sCapBac;

    @Column(name = "sHinhThucLamViec", length = 50)
    private String sHinhThucLamViec;

    @Column(name = "fLuongToiThieu", nullable = false)
    private Double fLuongToiThieu = 0.0;

    @Column(name = "fLuongToiDa", nullable = false)
    private Double fLuongToiDa = 0.0;

    @Column(name = "sYeuCauKinhNghiem", length = 200)
    private String sYeuCauKinhNghiem;

    @Column(name = "sYeuCauHocVan", length = 100)
    private String sYeuCauHocVan;

    @Column(name = "sMoTaCongViec", columnDefinition = "NVARCHAR(MAX)")
    private String sMoTaCongViec;

    @Column(name = "sCheDoDaiNgo", columnDefinition = "NVARCHAR(MAX)")
    private String sCheDoDaiNgo;

    @Column(name = "dHanNopHoSo", nullable = false)
    private LocalDate dHanNopHoSo;

    @Column(name = "iTrangThaiDuyet", nullable = false)
    private Integer iTrangThaiDuyet = 0;

    @Column(name = "FK_MaCanBoDuyet", columnDefinition = "CHAR(10)", length = 10)
    private String fkMaCanBoDuyet;

    @Column(name = "dNgayDuyet")
    private LocalDateTime dNgayDuyet;

    @Column(name = "sLyDoTuChoi", length = 500)
    private String sLyDoTuChoi;

    @Column(name = "bDaDong", nullable = false)
    private Boolean bDaDong = false;

    @Column(name = "bDaXoa", nullable = false)
    private Boolean bDaXoa = false;

    public String getPkMaTin() { return pkMaTin != null ? pkMaTin.trim() : null; }
    public void setPkMaTin(String pkMaTin) { this.pkMaTin = pkMaTin; }
    public String getFkMaDoanhNghiep() { return fkMaDoanhNghiep != null ? fkMaDoanhNghiep.trim() : null; }
    public void setFkMaDoanhNghiep(String fkMaDoanhNghiep) { this.fkMaDoanhNghiep = fkMaDoanhNghiep; }
    public String getSTieuDe() { return sTieuDe; }
    public void setSTieuDe(String sTieuDe) { this.sTieuDe = sTieuDe; }
    public String getSViTriChucDanh() { return sViTriChucDanh; }
    public void setSViTriChucDanh(String sViTriChucDanh) { this.sViTriChucDanh = sViTriChucDanh; }
    public Integer getFkMaNganhNghe() { return fkMaNganhNghe; }
    public void setFkMaNganhNghe(Integer fkMaNganhNghe) { this.fkMaNganhNghe = fkMaNganhNghe; }
    public String getFkMaQuanLamViec() { return fkMaQuanLamViec != null ? fkMaQuanLamViec.trim() : null; }
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
    public Integer getITrangThaiDuyet() { return iTrangThaiDuyet; }
    public void setITrangThaiDuyet(Integer iTrangThaiDuyet) { this.iTrangThaiDuyet = iTrangThaiDuyet; }
    public String getFkMaCanBoDuyet() { return fkMaCanBoDuyet != null ? fkMaCanBoDuyet.trim() : null; }
    public void setFkMaCanBoDuyet(String fkMaCanBoDuyet) { this.fkMaCanBoDuyet = fkMaCanBoDuyet; }
    public LocalDateTime getDNgayDuyet() { return dNgayDuyet; }
    public void setDNgayDuyet(LocalDateTime dNgayDuyet) { this.dNgayDuyet = dNgayDuyet; }
    public String getSLyDoTuChoi() { return sLyDoTuChoi; }
    public void setSLyDoTuChoi(String sLyDoTuChoi) { this.sLyDoTuChoi = sLyDoTuChoi; }
    public Boolean getBDaDong() { return bDaDong; }
    public void setBDaDong(Boolean bDaDong) { this.bDaDong = bDaDong; }
    public Boolean getBDaXoa() { return bDaXoa; }
    public void setBDaXoa(Boolean bDaXoa) { this.bDaXoa = bDaXoa; }
}
