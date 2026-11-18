package vn.gov.hanoi.dvvl.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_VaiTro", schema = "dbo")
public class MVaiTro {

    @Id
    @Column(name = "PK_MaVaiTro", nullable = false)
    private Integer pkMaVaiTro;

    @Column(name = "sTenVaiTro", nullable = false, length = 100, unique = true)
    private String sTenVaiTro;

    public Integer getPkMaVaiTro() { return pkMaVaiTro; }
    public void setPkMaVaiTro(Integer pkMaVaiTro) { this.pkMaVaiTro = pkMaVaiTro; }
    public String getSTenVaiTro() { return sTenVaiTro; }
    public void setSTenVaiTro(String sTenVaiTro) { this.sTenVaiTro = sTenVaiTro; }
}
