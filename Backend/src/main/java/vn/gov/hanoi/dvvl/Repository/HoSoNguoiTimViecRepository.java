package vn.gov.hanoi.dvvl.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gov.hanoi.dvvl.Model.MHoSoNguoiTimViec;

public interface HoSoNguoiTimViecRepository extends JpaRepository<MHoSoNguoiTimViec, String> {

    @Query("SELECT hs FROM MHoSoNguoiTimViec hs WHERE hs.fkMaTaiKhoan = :maTaiKhoan AND hs.bDaXoa = false")
    Optional<MHoSoNguoiTimViec> findActiveByTaiKhoan(@Param("maTaiKhoan") String maTaiKhoan);

    @Query("SELECT hs FROM MHoSoNguoiTimViec hs WHERE hs.pkMaUngVien = :maUngVien AND hs.bDaXoa = false")
    Optional<MHoSoNguoiTimViec> findActiveById(@Param("maUngVien") String maUngVien);

    @Query("SELECT CASE WHEN COUNT(hs) > 0 THEN true ELSE false END FROM MHoSoNguoiTimViec hs WHERE hs.sSoCCCD = :cccd AND hs.bDaXoa = false AND (:excludeId IS NULL OR hs.pkMaUngVien <> :excludeId)")
    boolean existsActiveByCccd(@Param("cccd") String cccd, @Param("excludeId") String excludeId);

    @Query("SELECT hs FROM MHoSoNguoiTimViec hs WHERE hs.bDaXoa = false AND hs.bDangTimViecCongKhai = true")
    List<MHoSoNguoiTimViec> findAllPublicActive();

    @Query("SELECT hs FROM MHoSoNguoiTimViec hs WHERE hs.bDaXoa = false ORDER BY hs.sHoTen ASC")
    List<MHoSoNguoiTimViec> findAllActive();
}
