package vn.gov.hanoi.dvvl.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gov.hanoi.dvvl.Model.MUngTuyen;

public interface UngTuyenRepository extends JpaRepository<MUngTuyen, String> {

    @Query("SELECT u FROM MUngTuyen u WHERE u.pkMaUngTuyen = :id AND u.bDaXoa = false")
    Optional<MUngTuyen> findActiveById(@Param("id") String id);

    @Query("SELECT u FROM MUngTuyen u WHERE u.fkMaUngVien = :maUngVien AND u.bDaXoa = false ORDER BY u.dNgayNop DESC")
    List<MUngTuyen> findByCandidate(@Param("maUngVien") String maUngVien);

    @Query("SELECT u FROM MUngTuyen u WHERE u.fkMaTin = :maTin AND u.bDaXoa = false ORDER BY u.dNgayNop DESC")
    List<MUngTuyen> findByJob(@Param("maTin") String maTin);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM MUngTuyen u WHERE u.fkMaTin = :maTin AND u.fkMaUngVien = :maUngVien AND u.bDaXoa = false")
    boolean existsApplication(@Param("maTin") String maTin, @Param("maUngVien") String maUngVien);
}
