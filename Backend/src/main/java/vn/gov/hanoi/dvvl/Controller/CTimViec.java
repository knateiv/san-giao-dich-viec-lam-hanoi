package vn.gov.hanoi.dvvl.Controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.gov.hanoi.dvvl.Config.UserPrincipal;
import vn.gov.hanoi.dvvl.Dto.ApiResponse;
import vn.gov.hanoi.dvvl.Dto.VTinTuyenDung;
import vn.gov.hanoi.dvvl.Dto.VTimViec;
import vn.gov.hanoi.dvvl.Model.MDoanhNghiep;
import vn.gov.hanoi.dvvl.Model.MHoSoNguoiTimViec;
import vn.gov.hanoi.dvvl.Model.MQuanHuyen;
import vn.gov.hanoi.dvvl.Model.MTinTuyenDung;
import vn.gov.hanoi.dvvl.Repository.DoanhNghiepRepository;
import vn.gov.hanoi.dvvl.Repository.HoSoNguoiTimViecRepository;
import vn.gov.hanoi.dvvl.Repository.NganhNgheRepository;
import vn.gov.hanoi.dvvl.Repository.QuanHuyenRepository;
import vn.gov.hanoi.dvvl.Repository.TinTuyenDungRepository;
import vn.gov.hanoi.dvvl.Repository.UngTuyenRepository;
import vn.gov.hanoi.dvvl.Service.AuditLogService;
import vn.gov.hanoi.dvvl.Service.BusinessException;
import vn.gov.hanoi.dvvl.Service.TextNormalizer;
import vn.gov.hanoi.dvvl.Service.ViewMapperService;

/**
 * Controller Phân hệ 5 — Tìm kiếm việc làm đa tiêu chí (R18) & Tra cứu ứng viên công khai (R19)
 * Khớp Mục 4.7.3: timKiemViecLam, locTinConHan
 */
@RestController
@RequestMapping("/api/tim-viec")
public class CTimViec {

    @PersistenceContext
    private EntityManager em;

    private final NganhNgheRepository nnRepo;
    private final QuanHuyenRepository qhRepo;
    private final TinTuyenDungRepository tinRepo;
    private final HoSoNguoiTimViecRepository hoSoRepo;
    private final DoanhNghiepRepository dnRepo;
    private final UngTuyenRepository utRepo;
    private final ViewMapperService mapper;
    private final AuditLogService audit;

    public CTimViec(
            NganhNgheRepository nnRepo,
            QuanHuyenRepository qhRepo,
            TinTuyenDungRepository tinRepo,
            HoSoNguoiTimViecRepository hoSoRepo,
            DoanhNghiepRepository dnRepo,
            UngTuyenRepository utRepo,
            ViewMapperService mapper,
            AuditLogService audit
    ) {
        this.nnRepo = nnRepo;
        this.qhRepo = qhRepo;
        this.tinRepo = tinRepo;
        this.hoSoRepo = hoSoRepo;
        this.dnRepo = dnRepo;
        this.utRepo = utRepo;
        this.mapper = mapper;
        this.audit = audit;
    }

    @GetMapping("/danh-muc")
    public ApiResponse<Map<String, Object>> layDanhMucVaTongQuan() {
        long soTinCongKhai = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM dbo.vv_TinTuyenDungCongKhai").getSingleResult()).longValue();
        long soDnXacMinh = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM dbo.tbl_DoanhNghiep WHERE bDaXoa = 0 AND iTrangThaiThamDinh = 1").getSingleResult()).longValue();
        long soHoSoCongKhai = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM dbo.vv_HoSoUngVienCongKhai").getSingleResult()).longValue();
        long soTrungTuyen = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM dbo.tbl_UngTuyen WHERE bDaXoa = 0 AND iTrangThaiUngTuyen = 3").getSingleResult()).longValue();

        Map<String, Object> data = new HashMap<>();
        data.put("nganhNghe", nnRepo.findAllEnabled());
        data.put("quanHuyen", qhRepo.findAllActiveOrdered());
        data.put("tongQuan", Map.of(
                "soTinCongKhai", soTinCongKhai,
                "soDnXacMinh", soDnXacMinh,
                "soHoSoCongKhai", soHoSoCongKhai,
                "soTrungTuyen", soTrungTuyen
        ));
        return ApiResponse.ok("Danh mục ngành nghề, 30 quận/huyện Hà Nội và chỉ số tổng quan.", data);
    }

    // R18: Tìm kiếm & lọc việc làm đa tiêu chí AND (gọi sp_TimKiemViecLamDaTieuChi trên vv_TinTuyenDungCongKhai)
    @GetMapping
    @SuppressWarnings("unchecked")
    public ApiResponse<Map<String, Object>> timKiemViecLam(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) Integer maNganhNghe,
            @RequestParam(required = false) String maQuanHuyen,
            @RequestParam(required = false) Double luongToiThieu,
            @RequestParam(required = false) Double luongToiDa,
            @RequestParam(required = false) String hocVan,
            @RequestParam(required = false) String hinhThuc,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        long t0 = System.currentTimeMillis();
        if (luongToiThieu != null && luongToiDa != null && luongToiThieu > luongToiDa) {
            throw new BusinessException("Khoảng lương không hợp lệ: Mức lương tối thiểu không được lớn hơn tối đa.");
        }

        String candidateId = null;
        if (user != null && user.getFkMaVaiTro() == 1) {
            candidateId = hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                    .map(MHoSoNguoiTimViec::getPkMaUngVien).orElse(null);
        }

        String sql = "EXEC dbo.sp_TimKiemViecLamDaTieuChi @sTuKhoa = :kw, @iMaNganhNghe = :nn, "
                + "@sMaQuanHuyen = :qh, @fLuongToiThieu = :minL, @sYeuCauHocVan = :hv, @sHinhThucLamViec = :ht";
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("kw", tuKhoa != null ? TextNormalizer.normalizeText(tuKhoa) : null)
                .setParameter("nn", maNganhNghe)
                .setParameter("qh", maQuanHuyen != null && !maQuanHuyen.isBlank() ? maQuanHuyen.trim() : null)
                .setParameter("minL", luongToiThieu)
                .setParameter("hv", hocVan != null && !hocVan.isBlank() ? hocVan.trim() : null)
                .setParameter("ht", hinhThuc != null && !hinhThuc.isBlank() ? hinhThuc.trim() : null)
                .getResultList();

        List<VTinTuyenDung.TinTuyenDungView> matched = new java.util.ArrayList<>();
        for (Object[] r : rows) {
            double fMin = r[14] != null ? ((Number) r[14]).doubleValue() : 0.0;
            double fMax = r[15] != null ? ((Number) r[15]).doubleValue() : 0.0;
            if (luongToiDa != null && fMin > luongToiDa) {
                continue;
            }
            VTinTuyenDung.TinTuyenDungView v = new VTinTuyenDung.TinTuyenDungView();
            String pkMaTin = trimStr(r[0]);
            v.setPkMaTin(pkMaTin);
            v.setFkMaDoanhNghiep(trimStr(r[1]));
            v.setSTenDoanhNghiep(trimStr(r[2]));
            v.setSTieuDe(trimStr(r[4]));
            v.setSViTriChucDanh(trimStr(r[5]));
            v.setFkMaNganhNghe(r[6] != null ? ((Number) r[6]).intValue() : null);
            v.setSTenNganhNghe(trimStr(r[7]));
            v.setFkMaQuanLamViec(trimStr(r[8]));
            v.setSTenQuanLamViec(trimStr(r[9]));
            v.setSDiaChiCuThe(trimStr(r[10]));
            v.setISoLuongTuyen(r[11] != null ? ((Number) r[11]).intValue() : 1);
            v.setSCapBac(trimStr(r[12]));
            v.setSHinhThucLamViec(trimStr(r[13]));
            v.setFLuongToiThieu(fMin);
            v.setFLuongToiDa(fMax);
            v.setSYeuCauKinhNghiem(trimStr(r[16]));
            v.setSYeuCauHocVan(trimStr(r[17]));
            v.setSMoTaCongViec(trimStr(r[18]));
            v.setSCheDoDaiNgo(trimStr(r[19]));
            v.setDHanNopHoSo(r[20] instanceof java.sql.Date d ? d.toLocalDate() : null);
            v.setITrangThaiDuyet(1);
            v.setTenTrangThaiDuyet("Đã duyệt");
            v.setBDaDong(false);
            v.setDaNop(candidateId != null && utRepo.existsApplication(pkMaTin, candidateId));
            matched.add(v);
        }

        int safeSize = Math.max(1, Math.min(size, 50));
        int safePage = Math.max(0, page);
        int total = matched.size();
        int fromIdx = Math.min(safePage * safeSize, total);
        int toIdx = Math.min(fromIdx + safeSize, total);
        List<VTinTuyenDung.TinTuyenDungView> paged = matched.subList(fromIdx, toIdx);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / safeSize));

        long elapsed = System.currentTimeMillis() - t0;
        Map<String, Object> body = new HashMap<>();
        body.put("items", paged);
        body.put("totalItems", total);
        body.put("page", safePage);
        body.put("size", safeSize);
        body.put("totalPages", totalPages);
        return ApiResponse.ok("Tìm thấy " + total + " việc làm phù hợp.", body, elapsed);
    }

    @GetMapping("/{maTin}")
    public ApiResponse<VTinTuyenDung.TinTuyenDungView> chiTietViecLam(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maTin
    ) {
        MTinTuyenDung tin = tinRepo.findActiveById(maTin)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tin tuyển dụng " + maTin, HttpStatus.NOT_FOUND));

        boolean isPublic = tin.getITrangThaiDuyet() == 1
                && !Boolean.TRUE.equals(tin.getBDaDong())
                && tin.getDHanNopHoSo() != null
                && !tin.getDHanNopHoSo().isBefore(LocalDate.now());

        if (!isPublic) {
            boolean allowed = false;
            if (user != null) {
                if (user.getFkMaVaiTro() == 3 || user.getFkMaVaiTro() == 4) {
                    allowed = true;
                } else if (user.getFkMaVaiTro() == 2) {
                    allowed = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                            .map(dn -> dn.getPkMaDoanhNghiep().equals(tin.getFkMaDoanhNghiep()))
                            .orElse(false);
                }
            }
            if (!allowed) {
                throw new BusinessException("Tin tuyển dụng này chưa công khai hoặc đã hết hạn nộp hồ sơ.", HttpStatus.NOT_FOUND);
            }
        }

        String candidateId = (user != null && user.getFkMaVaiTro() == 1)
                ? hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan()).map(MHoSoNguoiTimViec::getPkMaUngVien).orElse(null)
                : null;
        return ApiResponse.ok("Chi tiết tin tuyển dụng.", mapper.toTinView(tin, candidateId));
    }

    // R19: Tra cứu hồ sơ ứng viên công khai (chỉ DN đã thẩm định hoặc Cán bộ; ẩn CCCD, SĐT, Email theo NĐ 13/2023)
    @PreAuthorize("hasAnyRole('DN', 'CANBO', 'ADMIN')")
    @GetMapping("/ung-vien-cong-khai")
    public ApiResponse<List<VTimViec.UngVienCongKhaiView>> traCuuUngVienCongKhai(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) String maQuanHuyen,
            @RequestParam(required = false) String hocVan
    ) {
        if (user.getFkMaVaiTro() == 2) {
            MDoanhNghiep dn = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ doanh nghiệp."));
            if (dn.getITrangThaiThamDinh() == null || dn.getITrangThaiThamDinh() != 1) {
                throw new BusinessException("Chỉ doanh nghiệp đã được Cán bộ Trung tâm thẩm định hợp lệ mới được tra cứu ngân hàng hồ sơ ứng viên.", HttpStatus.FORBIDDEN);
            }
        }

        String kw = tuKhoa != null ? tuKhoa.trim().toLowerCase() : "";
        String qh = maQuanHuyen != null ? maQuanHuyen.trim() : "";
        String hv = hocVan != null ? hocVan.trim().toLowerCase() : "";

        List<VTimViec.UngVienCongKhaiView> list = hoSoRepo.findAllPublicActive().stream()
                .filter(hs -> qh.isEmpty() || qh.equals(hs.getFkMaQuanCuTru()))
                .filter(hs -> hv.isEmpty() || (hs.getSTrinhDoChuyenMon() != null && hs.getSTrinhDoChuyenMon().toLowerCase().contains(hv)))
                .filter(hs -> {
                    if (kw.isEmpty()) return true;
                    return contains(hs.getSViTriMongMuon(), kw)
                            || contains(hs.getSTrinhDoChuyenMon(), kw)
                            || contains(hs.getSKyNangTinHoc(), kw)
                            || contains(hs.getSKinhNghiemLamViec(), kw);
                })
                .map(hs -> {
                    VTimViec.UngVienCongKhaiView v = new VTimViec.UngVienCongKhaiView();
                    v.setPkMaUngVien(hs.getPkMaUngVien());
                    v.setSHoTen(hs.getSHoTen());
                    v.setSGioiTinh(hs.getSGioiTinh());
                    v.setIDoTuoi(hs.getDNgaySinh() != null ? LocalDate.now().getYear() - hs.getDNgaySinh().getYear() : null);
                    v.setFkMaQuanCuTru(hs.getFkMaQuanCuTru());
                    v.setSTenQuanCuTru(qhRepo.findActiveById(hs.getFkMaQuanCuTru()).map(MQuanHuyen::getSTenQuanHuyen).orElse(""));
                    v.setSTrinhDoVanHoa(hs.getSTrinhDoVanHoa());
                    v.setSTrinhDoChuyenMon(hs.getSTrinhDoChuyenMon());
                    v.setSKyNangTinHoc(hs.getSKyNangTinHoc());
                    v.setSTrinhDoNgoaiNgu(hs.getSTrinhDoNgoaiNgu());
                    v.setSKinhNghiemLamViec(hs.getSKinhNghiemLamViec());
                    v.setSViTriMongMuon(hs.getSViTriMongMuon());
                    v.setFMucLuongKyVong(hs.getFMucLuongKyVong());
                    v.setSDiaBanMongMuon(hs.getSDiaBanMongMuon());
                    v.setITyLeHoanThien(hs.getITyLeHoanThien());
                    v.setBCoTepCV(hs.getSDuongDanCV() != null && !hs.getSDuongDanCV().isBlank());
                    // Che thông tin nhạy cảm theo Nghị định 13/2023/NĐ-CP
                    v.setSSoCCCDChe(TextNormalizer.maskCccd(hs.getSSoCCCD()));
                    v.setSSDTChe(TextNormalizer.maskPhone(hs.getSSDT()));
                    v.setSEmailChe(TextNormalizer.maskEmail(hs.getSEmailLienHe()));
                    return v;
                })
                .toList();

        return ApiResponse.ok("Ngân hàng hồ sơ ứng viên công khai (đã ẩn thông tin định danh nhạy cảm theo NĐ 13/2023/NĐ-CP).", list);
    }

    // R19: Doanh nghiệp gửi lời mời ứng tuyển tới ứng viên công khai
    @PreAuthorize("hasRole('DN')")
    @PostMapping("/moi-ung-tuyen")
    @Transactional
    public ApiResponse<Void> moiUngTuyen(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody VTimViec.MoiUngTuyenRequest req,
            HttpServletRequest httpReq
    ) {
        MDoanhNghiep dn = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ doanh nghiệp."));
        if (dn.getITrangThaiThamDinh() == null || dn.getITrangThaiThamDinh() != 1) {
            throw new BusinessException("Doanh nghiệp chưa được xác minh pháp lý.", HttpStatus.FORBIDDEN);
        }
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveById(req.getFkMaUngVien())
                .orElseThrow(() -> new BusinessException("Không tìm thấy ứng viên.", HttpStatus.NOT_FOUND));
        if (!Boolean.TRUE.equals(hs.getBDangTimViecCongKhai())) {
            throw new BusinessException("Ứng viên hiện đã tắt chế độ tìm việc công khai.");
        }
        MTinTuyenDung tin = tinRepo.findActiveById(req.getFkMaTin())
                .orElseThrow(() -> new BusinessException("Không tìm thấy tin tuyển dụng.", HttpStatus.NOT_FOUND));
        if (!tin.getFkMaDoanhNghiep().equals(dn.getPkMaDoanhNghiep()) || tin.getITrangThaiDuyet() != 1) {
            throw new BusinessException("Vui lòng chọn tin tuyển dụng đã được duyệt thuộc doanh nghiệp của bạn.");
        }

        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "MOI_UNG_VIEN_UNG_TUYEN", "INFO", null,
                String.format("{\"maDoanhNghiep\":\"%s\",\"maUngVien\":\"%s\",\"maTin\":\"%s\",\"loiNhan\":\"%s\"}",
                        dn.getPkMaDoanhNghiep(), hs.getPkMaUngVien(), tin.getPkMaTin(),
                        TextNormalizer.normalizeText(req.getLoiNhan())),
                httpReq);
        return ApiResponse.ok("Đã gửi lời mời ứng tuyển vị trí \"" + tin.getSViTriChucDanh() + "\" tới ứng viên " + hs.getSHoTen() + ".", null);
    }

    private boolean contains(String src, String kw) {
        return src != null && src.toLowerCase().contains(kw);
    }

    private String trimStr(Object o) {
        return o == null ? "" : o.toString().trim();
    }
}
