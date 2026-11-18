package vn.gov.hanoi.dvvl.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gov.hanoi.dvvl.Model.MTinTuyenDung;

public interface TinTuyenDungRepository extends JpaRepository<MTinTuyenDung, String> {

    @Query("SELECT t FROM MTinTuyenDung t WHERE t.pkMaTin = :maTin AND t.bDaXoa = false")
    Optional<MTinTuyenDung> findActiveById(@Param("maTin") String maTin);

    @Query("SELECT t FROM MTinTuyenDung t WHERE t.fkMaDoanhNghiep = :maDn AND t.bDaXoa = false ORDER BY t.pkMaTin DESC")
    List<MTinTuyenDung> findByEmployer(@Param("maDn") String maDn);

    @Query("SELECT t FROM MTinTuyenDung t WHERE t.bDaXoa = false ORDER BY t.iTrangThaiDuyet ASC, t.pkMaTin DESC")
    List<MTinTuyenDung> findAllActiveForModeration();
}
