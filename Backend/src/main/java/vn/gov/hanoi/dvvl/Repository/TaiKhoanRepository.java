package vn.gov.hanoi.dvvl.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.gov.hanoi.dvvl.Model.MTaiKhoan;

public interface TaiKhoanRepository extends JpaRepository<MTaiKhoan, String> {

    @Query("SELECT t FROM MTaiKhoan t WHERE LOWER(t.sEmail) = LOWER(:email) AND t.bDaXoa = false")
    Optional<MTaiKhoan> findActiveByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM MTaiKhoan t WHERE LOWER(t.sEmail) = LOWER(:email) AND t.bDaXoa = false")
    boolean existsActiveByEmail(@Param("email") String email);

    @Query("SELECT t FROM MTaiKhoan t WHERE t.bDaXoa = false ORDER BY t.dNgayTao DESC")
    List<MTaiKhoan> findAllActiveOrderByCreatedDesc();

    @Query("SELECT t FROM MTaiKhoan t WHERE t.pkMaTaiKhoan = :id AND t.bDaXoa = false")
    Optional<MTaiKhoan> findActiveById(@Param("id") String id);

    @Query("SELECT t FROM MTaiKhoan t WHERE t.bDaXoa = false AND t.fkMaVaiTro = :roleId")
    List<MTaiKhoan> findAllByRoleId(@Param("roleId") Integer roleId);
}
