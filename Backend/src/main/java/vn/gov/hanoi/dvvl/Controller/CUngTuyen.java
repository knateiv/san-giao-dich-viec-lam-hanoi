package vn.gov.hanoi.dvvl.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.gov.hanoi.dvvl.Config.UserPrincipal;
import vn.gov.hanoi.dvvl.Dto.ApiResponse;
import vn.gov.hanoi.dvvl.Dto.VUngTuyen;
import vn.gov.hanoi.dvvl.Model.MDoanhNghiep;
import vn.gov.hanoi.dvvl.Model.MHoSoNguoiTimViec;
import vn.gov.hanoi.dvvl.Model.MLichSuUngTuyen;
import vn.gov.hanoi.dvvl.Model.MTinTuyenDung;
import vn.gov.hanoi.dvvl.Model.MUngTuyen;
import vn.gov.hanoi.dvvl.Repository.DoanhNghiepRepository;
import vn.gov.hanoi.dvvl.Repository.HoSoNguoiTimViecRepository;
import vn.gov.hanoi.dvvl.Repository.LichSuUngTuyenRepository;
import vn.gov.hanoi.dvvl.Repository.TinTuyenDungRepository;
import vn.gov.hanoi.dvvl.Repository.UngTuyenRepository;
import vn.gov.hanoi.dvvl.Service.AuditLogService;
import vn.gov.hanoi.dvvl.Service.BusinessException;
import vn.gov.hanoi.dvvl.Service.IdGeneratorService;
import vn.gov.hanoi.dvvl.Service.TextNormalizer;
import vn.gov.hanoi.dvvl.Service.ViewMapperService;

/**
 * Controller Phân hệ 5 — Giao dịch Ứng tuyển & Theo dõi tiến trình (R20, R21, R22, R23)
 * Khớp Mục 4.7.3: nopHoSo, themUngTuyen, capNhatTienTrinh
 */
@RestController
@RequestMapping("/api/ung-tuyen")
public class CUngTuyen {

    private final UngTuyenRepository utRepo;
    private final LichSuUngTuyenRepository lichSuRepo;
    private final TinTuyenDungRepository tinRepo;
    private final HoSoNguoiTimViecRepository hoSoRepo;
    private final DoanhNghiepRepository dnRepo;
    private final IdGeneratorService idGen;
    private final AuditLogService audit;
    private final ViewMapperService mapper;

    public CUngTuyen(
            UngTuyenRepository utRepo,
            LichSuUngTuyenRepository lichSuRepo,
            TinTuyenDungRepository tinRepo,
            HoSoNguoiTimViecRepository hoSoRepo,
            DoanhNghiepRepository dnRepo,
            IdGeneratorService idGen,
            AuditLogService audit,
            ViewMapperService mapper
    ) {
        this.utRepo = utRepo;
        this.lichSuRepo = lichSuRepo;
        this.tinRepo = tinRepo;
        this.hoSoRepo = hoSoRepo;
        this.dnRepo = dnRepo;
        this.idGen = idGen;
        this.audit = audit;
        this.mapper = mapper;
    }

    // R20: Nộp hồ sơ ứng tuyển trực tuyến (NopHoSo + ThemUngTuyen)
    @PreAuthorize("hasRole('NTV')")
    @PostMapping
    @Transactional
    public ApiResponse<VUngTuyen.UngTuyenView> nopHoSo(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody VUngTuyen.NopHoSoRequest req,
            HttpServletRequest httpReq
    ) {
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Vui lòng tạo và chuẩn hóa hồ sơ cá nhân trước khi nộp ứng tuyển."));
        if (hs.getITyLeHoanThien() < 60) {
            throw new BusinessException("Hồ sơ ứng viên cần hoàn thiện tối thiểu 60% trước khi nộp ứng tuyển.");
        }
        String maTin = req.getFkMaTin().trim();
        MTinTuyenDung tin = tinRepo.findActiveById(maTin)
                .orElseThrow(() -> new BusinessException("Tin tuyển dụng không tồn tại.", HttpStatus.NOT_FOUND));
        if (tin.getITrangThaiDuyet() != 1 || Boolean.TRUE.equals(tin.getBDaDong())
                || (tin.getDHanNopHoSo() != null && tin.getDHanNopHoSo().isBefore(LocalDate.now()))) {
            throw new BusinessException("Tin tuyển dụng đã đóng, hết hạn hoặc chưa được phê duyệt công khai.");
        }
        if (utRepo.existsApplication(maTin, hs.getPkMaUngVien())) {
            throw new BusinessException("Bạn đã nộp hồ sơ ứng tuyển vào vị trí này trước đó.");
        }

        MUngTuyen ut = themUngTuyen(maTin, hs.getPkMaUngVien(), req.getSGhiChu());
        luuLichSu(ut.getPkMaUngTuyen(), null, 0, user.getPkMaTaiKhoan(),
                req.getSGhiChu() != null && !req.getSGhiChu().isBlank()
                        ? TextNormalizer.normalizeText(req.getSGhiChu())
                        : "Ứng viên nộp hồ sơ ứng tuyển trực tuyến.");
        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "NOP_HO_SO_UNG_TUYEN", "INFO", null,
                String.format("{\"PK_MaUngTuyen\":\"%s\",\"FK_MaTin\":\"%s\"}", ut.getPkMaUngTuyen(), maTin), httpReq);

        return ApiResponse.ok("Nộp hồ sơ ứng tuyển thành công!", mapper.toUngTuyenView(ut, true));
    }

    // R21: Khi Doanh nghiệp mở xem chi tiết lần đầu -> tự động chuyển 0 -> 1 (Đã tiếp nhận / Đã xem)
    @PreAuthorize("hasAnyRole('NTV', 'DN', 'CANBO', 'ADMIN')")
    @GetMapping("/chi-tiet/{maUngTuyen}")
    @Transactional
    public ApiResponse<VUngTuyen.UngTuyenView> xemChiTietHoSoUngTuyen(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maUngTuyen,
            HttpServletRequest httpReq
    ) {
        MUngTuyen ut = utRepo.findActiveById(maUngTuyen)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ ứng tuyển.", HttpStatus.NOT_FOUND));
        MTinTuyenDung tin = tinRepo.findById(ut.getFkMaTin())
                .orElseThrow(() -> new BusinessException("Không tìm thấy tin tuyển dụng."));

        if (user.getFkMaVaiTro() == 1) {
            MHoSoNguoiTimViec hs = hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                    .orElseThrow(() -> new BusinessException("Không có quyền truy cập.", HttpStatus.FORBIDDEN));
            if (!hs.getPkMaUngVien().equals(ut.getFkMaUngVien())) {
                throw new BusinessException("Không có quyền xem hồ sơ ứng tuyển của người khác.", HttpStatus.FORBIDDEN);
            }
        } else if (user.getFkMaVaiTro() == 2) {
            MDoanhNghiep dn = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                    .orElseThrow(() -> new BusinessException("Không có quyền truy cập.", HttpStatus.FORBIDDEN));
            if (!dn.getPkMaDoanhNghiep().equals(tin.getFkMaDoanhNghiep())) {
                throw new BusinessException("Hồ sơ ứng tuyển này không thuộc tin tuyển dụng của doanh nghiệp bạn.", HttpStatus.FORBIDDEN);
            }
            // R21: Tự động chuyển từ Chờ duyệt (0) sang Đã tiếp nhận / Đã xem (1) khi DN mở lần đầu
            if (ut.getITrangThaiUngTuyen() == 0) {
                ut.setITrangThaiUngTuyen(1);
                ut.setSGhiChuPhongVan("Doanh nghiệp đã tiếp nhận và mở xem chi tiết hồ sơ.");
                utRepo.save(ut);
                luuLichSu(ut.getPkMaUngTuyen(), 0, 1, user.getPkMaTaiKhoan(), "Doanh nghiệp mở xem chi tiết hồ sơ lần đầu.");
                audit.ghiNhatKy(user.getPkMaTaiKhoan(), "TIEP_NHAN_HO_SO_UNG_TUYEN", "INFO",
                        "{\"iTrangThaiUngTuyen\":0}", "{\"PK_MaUngTuyen\":\"" + maUngTuyen + "\",\"iTrangThaiUngTuyen\":1}", httpReq);
            }
        }

        return ApiResponse.ok("Chi tiết hồ sơ ứng tuyển.", mapper.toUngTuyenView(ut, true));
    }

    // R21: Doanh nghiệp cập nhật trạng thái xét duyệt & thiết lập lịch phỏng vấn
    @PreAuthorize("hasRole('DN')")
    @PutMapping("/{maUngTuyen}/tien-trinh")
    @Transactional
    public ApiResponse<VUngTuyen.UngTuyenView> capNhatTienTrinh(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maUngTuyen,
            @Valid @RequestBody VUngTuyen.CapNhatTienTrinhRequest req,
            HttpServletRequest httpReq
    ) {
        MDoanhNghiep dn = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ doanh nghiệp.", HttpStatus.NOT_FOUND));
        MUngTuyen ut = utRepo.findActiveById(maUngTuyen)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn ứng tuyển.", HttpStatus.NOT_FOUND));
        MTinTuyenDung tin = tinRepo.findActiveById(ut.getFkMaTin())
                .orElseThrow(() -> new BusinessException("Không tìm thấy tin tuyển dụng."));
        if (!tin.getFkMaDoanhNghiep().equals(dn.getPkMaDoanhNghiep())) {
            throw new BusinessException("Bạn không có quyền xử lý hồ sơ không thuộc doanh nghiệp mình.", HttpStatus.FORBIDDEN);
        }

        int cu = ut.getITrangThaiUngTuyen();
        if (cu == 5) {
            throw new BusinessException("Ứng viên đã hủy yêu cầu ứng tuyển trước đó, không thể cập nhật thêm.");
        }

        int moi = req.getITrangThaiUngTuyen();
        if (moi < 1 || moi > 4) {
            throw new BusinessException("Trạng thái xét duyệt hợp lệ gồm: 1 (Đã xem), 2 (Hẹn phỏng vấn), 3 (Trúng tuyển), 4 (Từ chối).");
        }
        if (moi < cu) {
            throw new BusinessException("Không được chuyển ngược tiến trình xét duyệt từ trạng thái đã qua.");
        }
        if (moi == 2) {
            if (req.getDThoiGianPhongVan() == null || !req.getDThoiGianPhongVan().isAfter(LocalDateTime.now())) {
                throw new BusinessException("Khi chọn Hẹn phỏng vấn, thời gian hẹn phải ở trong tương lai.");
            }
            if (req.getSDiaDiemPhongVan() == null || req.getSDiaDiemPhongVan().isBlank()) {
                throw new BusinessException("Vui lòng nhập địa điểm hoặc đường dẫn phòng họp trực tuyến.");
            }
            ut.setDThoiGianPhongVan(req.getDThoiGianPhongVan());
            ut.setSDiaDiemPhongVan(TextNormalizer.normalizeText(req.getSDiaDiemPhongVan()));
            ut.setSHinhThucPhongVan(req.getSHinhThucPhongVan() != null && !req.getSHinhThucPhongVan().isBlank()
                    ? TextNormalizer.normalizeText(req.getSHinhThucPhongVan()) : "Trực tiếp");
        }

        String ghiChu = TextNormalizer.normalizeText(req.getSGhiChuPhongVan());
        if (ghiChu == null || ghiChu.isBlank()) {
            ghiChu = "Chuyển trạng thái sang: " + ViewMapperService.tenTrangThaiUngTuyen(moi);
        }
        ut.setITrangThaiUngTuyen(moi);
        ut.setSGhiChuPhongVan(ghiChu);
        utRepo.save(ut);

        luuLichSu(maUngTuyen, cu, moi, user.getPkMaTaiKhoan(), ghiChu);
        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "CAP_NHAT_TIEN_TRINH_UNG_TUYEN", "INFO",
                "{\"iTrangThaiUngTuyen\":" + cu + "}",
                "{\"PK_MaUngTuyen\":\"" + maUngTuyen + "\",\"iTrangThaiUngTuyen\":" + moi + "}", httpReq);

        return ApiResponse.ok("Cập nhật tiến trình xét duyệt thành công.", mapper.toUngTuyenView(ut, true));
    }

    // R22: Người tìm việc theo dõi tiến trình & lịch sử ứng tuyển tức thời
    @PreAuthorize("hasRole('NTV')")
    @GetMapping("/lich-su-cua-toi")
    public ApiResponse<List<VUngTuyen.UngTuyenView>> lichSuCuaToi(@AuthenticationPrincipal UserPrincipal user) {
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan()).orElse(null);
        if (hs == null) {
            return ApiResponse.ok("Chưa có hồ sơ ứng tuyển.", List.of());
        }
        List<VUngTuyen.UngTuyenView> list = utRepo.findByCandidate(hs.getPkMaUngVien()).stream()
                .map(u -> mapper.toUngTuyenView(u, true))
                .toList();
        return ApiResponse.ok("Danh sách tiến trình ứng tuyển.", list);
    }

    // R22: Người tìm việc xác nhận đã nhận lịch phỏng vấn
    @PreAuthorize("hasRole('NTV')")
    @PatchMapping("/{maUngTuyen}/xac-nhan-pv")
    @Transactional
    public ApiResponse<VUngTuyen.UngTuyenView> xacNhanPhongVan(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maUngTuyen,
            HttpServletRequest httpReq
    ) {
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ ứng viên.", HttpStatus.NOT_FOUND));
        MUngTuyen ut = utRepo.findActiveById(maUngTuyen)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đơn ứng tuyển.", HttpStatus.NOT_FOUND));
        if (!ut.getFkMaUngVien().equals(hs.getPkMaUngVien())) {
            throw new BusinessException("Không có quyền thao tác trên hồ sơ của người khác.", HttpStatus.FORBIDDEN);
        }
        if (ut.getITrangThaiUngTuyen() != 2) {
            throw new BusinessException("Đơn ứng tuyển hiện không ở trạng thái Hẹn phỏng vấn.");
        }
        luuLichSu(maUngTuyen, 2, 2, user.getPkMaTaiKhoan(), "Ứng viên xác nhận đã nhận thông tin lịch hẹn phỏng vấn.");
        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "XAC_NHAN_LICH_PHONG_VAN", "INFO", null, "{\"PK_MaUngTuyen\":\"" + maUngTuyen + "\"}", httpReq);
        return ApiResponse.ok("Đã xác nhận tham gia phỏng vấn thành công.", mapper.toUngTuyenView(ut, true));
    }

    // R23: Người tìm việc chủ động hủy yêu cầu ứng tuyển (chỉ cho phép khi trạng thái còn là 0 - Chờ duyệt)
    @PreAuthorize("hasRole('NTV')")
    @PutMapping("/{maUngTuyen}/huy")
    @Transactional
    public ApiResponse<VUngTuyen.UngTuyenView> huyUngTuyen(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maUngTuyen,
            @RequestBody(required = false) VUngTuyen.HuyUngTuyenRequest req,
            HttpServletRequest httpReq
    ) {
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ ứng viên.", HttpStatus.NOT_FOUND));
        MUngTuyen ut = utRepo.findActiveById(maUngTuyen)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ ứng tuyển.", HttpStatus.NOT_FOUND));
        if (!ut.getFkMaUngVien().equals(hs.getPkMaUngVien())) {
            throw new BusinessException("Bạn không có quyền hủy hồ sơ của người khác.", HttpStatus.FORBIDDEN);
        }
        if (ut.getITrangThaiUngTuyen() != 0) {
            throw new BusinessException("Hồ sơ đã được doanh nghiệp tiếp nhận xử lý, vui lòng liên hệ trực tiếp nhà tuyển dụng nếu muốn rút hồ sơ.");
        }
        String lyDo = (req != null && req.getSLyDo() != null && !req.getSLyDo().isBlank())
                ? TextNormalizer.normalizeText(req.getSLyDo())
                : "Người tìm việc chủ động hủy yêu cầu ứng tuyển.";
        ut.setITrangThaiUngTuyen(5);
        ut.setSGhiChuPhongVan(lyDo);
        utRepo.save(ut);

        luuLichSu(maUngTuyen, 0, 5, user.getPkMaTaiKhoan(), lyDo);
        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "HUY_YEU_CAU_UNG_TUYEN", "INFO",
                "{\"iTrangThaiUngTuyen\":0}", "{\"PK_MaUngTuyen\":\"" + maUngTuyen + "\",\"iTrangThaiUngTuyen\":5}", httpReq);

        return ApiResponse.ok("Hủy yêu cầu ứng tuyển thành công.", mapper.toUngTuyenView(ut, true));
    }

    private MUngTuyen themUngTuyen(String maTin, String maUngVien, String ghiChu) {
        MUngTuyen ut = new MUngTuyen();
        ut.setPkMaUngTuyen(idGen.nextCode("tbl_UngTuyen", "PK_MaUngTuyen", "AP"));
        ut.setFkMaTin(maTin);
        ut.setFkMaUngVien(maUngVien);
        ut.setDNgayNop(LocalDateTime.now());
        ut.setITrangThaiUngTuyen(0);
        ut.setSGhiChuPhongVan(TextNormalizer.normalizeText(ghiChu));
        ut.setBDaXoa(false);
        return utRepo.save(ut);
    }

    private void luuLichSu(String maUngTuyen, Integer cu, int moi, String nguoiThucHien, String ghiChu) {
        MLichSuUngTuyen ls = new MLichSuUngTuyen();
        ls.setFkMaUngTuyen(maUngTuyen);
        ls.setITrangThaiCu(cu);
        ls.setITrangThaiMoi(moi);
        ls.setFkMaNguoiThucHien(nguoiThucHien);
        ls.setSGhiChu(ghiChu);
        ls.setDThoiGianThayDoi(LocalDateTime.now());
        lichSuRepo.save(ls);
    }
}
