package vn.gov.hanoi.dvvl.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.gov.hanoi.dvvl.Model.MNhatKyHeThong;
import vn.gov.hanoi.dvvl.Repository.NhatKyHeThongRepository;

@Service
public class AuditLogService {

    private final NhatKyHeThongRepository nhatKyRepo;

    public AuditLogService(NhatKyHeThongRepository nhatKyRepo) {
        this.nhatKyRepo = nhatKyRepo;
    }

    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = BusinessException.class)
    public void ghiNhatKy(
            String maTaiKhoan,
            String loaiHanhDong,
            String mucDo,
            String duLieuCu,
            String duLieuMoi,
            HttpServletRequest request
    ) {
        MNhatKyHeThong log = new MNhatKyHeThong();
        log.setFkMaTaiKhoan(maTaiKhoan);
        log.setSLoaiHanhDong(loaiHanhDong);
        log.setSMucDo(mucDo);
        log.setSDiaChiIP(extractIp(request));
        log.setSTrinhDuyet(extractUserAgent(request));
        log.setDThoiGianGhi(LocalDateTime.now());
        log.setSDuLieuCu(duLieuCu);
        log.setSDuLieuMoi(duLieuMoi);
        nhatKyRepo.save(log);
    }

    private String extractIp(HttpServletRequest req) {
        if (req == null) return "127.0.0.1";
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return req.getRemoteAddr() != null ? req.getRemoteAddr() : "127.0.0.1";
    }

    private String extractUserAgent(HttpServletRequest req) {
        if (req == null) return "System";
        String ua = req.getHeader("User-Agent");
        if (ua == null || ua.isBlank()) return "API Client";
        return ua.length() > 250 ? ua.substring(0, 250) : ua;
    }
}
