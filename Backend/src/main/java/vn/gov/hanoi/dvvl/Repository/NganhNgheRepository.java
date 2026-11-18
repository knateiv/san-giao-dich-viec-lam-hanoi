package vn.gov.hanoi.dvvl.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gov.hanoi.dvvl.Model.MNganhNghe;

public interface NganhNgheRepository extends JpaRepository<MNganhNghe, Integer> {

    @Query("SELECT n FROM MNganhNghe n WHERE n.bDaXoa = false ORDER BY n.pkMaNganhNghe ASC")
    List<MNganhNghe> findAllActive();

    @Query("SELECT n FROM MNganhNghe n WHERE n.bDaXoa = false AND n.bTrangThaiHoatDong = true ORDER BY n.pkMaNganhNghe ASC")
    List<MNganhNghe> findAllEnabled();

    @Query("SELECT n FROM MNganhNghe n WHERE n.pkMaNganhNghe = :ma AND n.bDaXoa = false")
    Optional<MNganhNghe> findActiveById(@Param("ma") Integer ma);

    @Query("SELECT COALESCE(MAX(n.pkMaNganhNghe), 0) FROM MNganhNghe n")
    Integer findMaxId();

    @Query("SELECT COUNT(t) FROM MTinTuyenDung t WHERE t.fkMaNganhNghe = :ma AND t.bDaXoa = false")
    long countJobReferences(@Param("ma") Integer ma);
}
