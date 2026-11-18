package vn.gov.hanoi.dvvl.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.gov.hanoi.dvvl.Model.MNhatKyHeThong;

public interface NhatKyHeThongRepository extends JpaRepository<MNhatKyHeThong, Long> {

    @Query("SELECT n FROM MNhatKyHeThong n ORDER BY n.dThoiGianGhi DESC, n.pkMaNhatKy DESC")
    List<MNhatKyHeThong> findAllOrderedDesc();
}
