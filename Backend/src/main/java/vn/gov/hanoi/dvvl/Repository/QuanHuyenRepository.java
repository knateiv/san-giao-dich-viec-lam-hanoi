package vn.gov.hanoi.dvvl.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gov.hanoi.dvvl.Model.MQuanHuyen;

public interface QuanHuyenRepository extends JpaRepository<MQuanHuyen, String> {

    @Query("SELECT q FROM MQuanHuyen q WHERE q.bDaXoa = false ORDER BY q.iThuTuHienThi ASC, q.sTenQuanHuyen ASC")
    List<MQuanHuyen> findAllActiveOrdered();

    @Query("SELECT q FROM MQuanHuyen q WHERE q.pkMaQuanHuyen = :ma AND q.bDaXoa = false")
    Optional<MQuanHuyen> findActiveById(@Param("ma") String ma);

    @Query("SELECT COUNT(hs) FROM MHoSoNguoiTimViec hs WHERE hs.fkMaQuanCuTru = :ma AND hs.bDaXoa = false")
    long countCandidateReferences(@Param("ma") String ma);

    @Query("SELECT COUNT(t) FROM MTinTuyenDung t WHERE t.fkMaQuanLamViec = :ma AND t.bDaXoa = false")
    long countJobReferences(@Param("ma") String ma);

    @Query("SELECT COUNT(dn) FROM MDoanhNghiep dn WHERE dn.fkMaQuanTruSo = :ma AND dn.bDaXoa = false")
    long countEmployerReferences(@Param("ma") String ma);
}
