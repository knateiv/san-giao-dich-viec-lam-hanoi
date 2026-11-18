package vn.gov.hanoi.dvvl.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gov.hanoi.dvvl.Model.MDoanhNghiep;

public interface DoanhNghiepRepository extends JpaRepository<MDoanhNghiep, String> {

    @Query("SELECT dn FROM MDoanhNghiep dn WHERE dn.fkMaTaiKhoan = :maTaiKhoan AND dn.bDaXoa = false")
    Optional<MDoanhNghiep> findActiveByTaiKhoan(@Param("maTaiKhoan") String maTaiKhoan);

    @Query("SELECT dn FROM MDoanhNghiep dn WHERE dn.pkMaDoanhNghiep = :ma AND dn.bDaXoa = false")
    Optional<MDoanhNghiep> findActiveById(@Param("ma") String ma);

    @Query("SELECT CASE WHEN COUNT(dn) > 0 THEN true ELSE false END FROM MDoanhNghiep dn WHERE dn.sMaSoThue = :mst AND dn.bDaXoa = false AND (:excludeId IS NULL OR dn.pkMaDoanhNghiep <> :excludeId)")
    boolean existsActiveByTaxCode(@Param("mst") String mst, @Param("excludeId") String excludeId);

    @Query("SELECT dn FROM MDoanhNghiep dn WHERE dn.bDaXoa = false ORDER BY dn.iTrangThaiThamDinh ASC, dn.pkMaDoanhNghiep DESC")
    List<MDoanhNghiep> findAllActiveForModeration();
}
