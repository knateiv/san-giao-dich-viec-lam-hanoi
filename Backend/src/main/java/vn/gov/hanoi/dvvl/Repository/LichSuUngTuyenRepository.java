package vn.gov.hanoi.dvvl.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gov.hanoi.dvvl.Model.MLichSuUngTuyen;

public interface LichSuUngTuyenRepository extends JpaRepository<MLichSuUngTuyen, Long> {

    @Query("SELECT l FROM MLichSuUngTuyen l WHERE l.fkMaUngTuyen = :maUngTuyen ORDER BY l.dThoiGianThayDoi ASC, l.pkMaLichSu ASC")
    List<MLichSuUngTuyen> findByUngTuyenOrdered(@Param("maUngTuyen") String maUngTuyen);
}
