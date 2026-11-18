package vn.gov.hanoi.dvvl.Controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.gov.hanoi.dvvl.Config.UserPrincipal;
import vn.gov.hanoi.dvvl.Dto.ApiResponse;
import vn.gov.hanoi.dvvl.Dto.VDoanhNghiep;
import vn.gov.hanoi.dvvl.Dto.VNguoiTimViec;
import vn.gov.hanoi.dvvl.Dto.VPheDuyet;
import vn.gov.hanoi.dvvl.Dto.VTinTuyenDung;
import vn.gov.hanoi.dvvl.Model.MDoanhNghiep;
import vn.gov.hanoi.dvvl.Model.MHoSoNguoiTimViec;
import vn.gov.hanoi.dvvl.Model.MQuanHuyen;
import vn.gov.hanoi.dvvl.Model.MTinTuyenDung;
import vn.gov.hanoi.dvvl.Repository.DoanhNghiepRepository;
import vn.gov.hanoi.dvvl.Repository.HoSoNguoiTimViecRepository;
import vn.gov.hanoi.dvvl.Repository.QuanHuyenRepository;
import vn.gov.hanoi.dvvl.Repository.TinTuyenDungRepository;
import vn.gov.hanoi.dvvl.Service.AuditLogService;
import vn.gov.hanoi.dvvl.Service.BusinessException;
import vn.gov.hanoi.dvvl.Service.PdfExportService;
import vn.gov.hanoi.dvvl.Service.TextNormalizer;
import vn.gov.hanoi.dvvl.Service.ViewMapperService;

/**
 * Controller Phân hệ 4 — Kiểm duyệt Nghiệp vụ Cán bộ Trung tâm (R14, R15, R16, R17)
 * Khớp Mục 4.7.2: xuLyPheDuyet
 */
@RestController
@RequestMapping("/api/kiem-duyet")
public class CPheDuyet {

    private static final AtomicInteger REFERRAL_SEQ = new AtomicInteger(1001);

    private final DoanhNghiepRepository dnRepo;
    private final TinTuyenDungRepository tinRepo;
    private final HoSoNguoiTimViecRepository hoSoRepo;
    private final QuanHuyenRepository qhRepo;
    private final CDoanhNghiep doanhNghiepMapper;
    private final ViewMapperService mapper;
    private final PdfExportService pdfService;
    private final AuditLogService audit;

    public CPheDuyet(
            DoanhNghiepRepository dnRepo,
            TinTuyenDungRepository tinRepo,
            HoSoNguoiTimViecRepository hoSoRepo,
            QuanHuyenRepository qhRepo,
            CDoanhNghiep doanhNghiepMapper,
            ViewMapperService mapper,
            PdfExportService pdfService,
            AuditLogService audit
    ) {
        this.dnRepo = dnRepo;
        this.tinRepo = tinRepo;
        this.hoSoRepo = hoSoRepo;
        this.qhRepo = qhRepo;
        this.doanhNghiepMapper = doanhNghiepMapper;
        this.mapper = mapper;
        this.pdfService = pdfService;
        this.audit = audit;
    }

    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @GetMapping("/doanh-nghiep")
    public ApiResponse<List<VDoanhNghiep.DoanhNghiepView>> danhSachDoanhNghiep(
            @RequestParam(required = false) Integer trangThai
    ) {
        List<VDoanhNghiep.DoanhNghiepView> list = dnRepo.findAllActiveForModeration().stream()
                .filter(d -> trangThai == null || trangThai.equals(d.getITrangThaiThamDinh()))
                .map(doanhNghiepMapper::toView)
                .toList();
        return ApiResponse.ok("Danh sách hồ sơ pháp lý doanh nghiệp.", list);
    }

    // R14: Thẩm định hồ sơ pháp lý doanh nghiệp
    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @PutMapping("/doanh-nghiep/{maDn}")
    @Transactional
    public ApiResponse<VDoanhNghiep.DoanhNghiepView> thamDinhDoanhNghiep(
            @AuthenticationPrincipal UserPrincipal canBo,
            @PathVariable String maDn,
            @RequestBody VPheDuyet.ThamDinhDnRequest req,
            HttpServletRequest httpReq
    ) {
        MDoanhNghiep dn = dnRepo.findActiveById(maDn)
                .orElseThrow(() -> new BusinessException("Không tìm thấy doanh nghiệp " + maDn, HttpStatus.NOT_FOUND));
        int status = req.getITrangThaiThamDinh() != null ? req.getITrangThaiThamDinh() : -1;
        if (status != 1 && status != 2) {
            throw new BusinessException("Trạng thái thẩm định phải là 1 (Đã xác minh) hoặc 2 (Từ chối).");
        }
        String ghiChu = TextNormalizer.normalizeText(req.getSGhiChuThamDinh());
        if (status == 2 && (ghiChu == null || ghiChu.isBlank())) {
            throw new BusinessException("Vui lòng nhập rõ lý do từ chối hồ sơ pháp lý doanh nghiệp.");
        }
        int cu = dn.getITrangThaiThamDinh();
        dn.setITrangThaiThamDinh(status);
        dn.setSGhiChuThamDinh(status == 1 && (ghiChu == null || ghiChu.isBlank())
                ? "Hồ sơ pháp lý doanh nghiệp hợp lệ, đã cấp quyền đăng tuyển công khai."
                : ghiChu);
        dnRepo.save(dn);

        audit.ghiNhatKy(canBo.getPkMaTaiKhoan(), status == 1 ? "XAC_MINH_DOANH_NGHIEP" : "TU_CHOI_DOANH_NGHIEP",
                "INFO", "{\"iTrangThaiThamDinh\":" + cu + "}",
                "{\"PK_MaDoanhNghiep\":\"" + maDn + "\",\"iTrangThaiThamDinh\":" + status + "}", httpReq);

        return ApiResponse.ok(
                status == 1 ? "Đã xác minh hợp lệ doanh nghiệp " + dn.getSTenDoanhNghiep() : "Đã từ chối hồ sơ doanh nghiệp.",
                doanhNghiepMapper.toView(dn));
    }

    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @GetMapping("/tin-tuyen-dung")
    public ApiResponse<List<VTinTuyenDung.TinTuyenDungView>> danhSachTinKiemDuyet(
            @RequestParam(required = false) Integer trangThai
    ) {
        List<VTinTuyenDung.TinTuyenDungView> list = tinRepo.findAllActiveForModeration().stream()
                .filter(t -> trangThai == null || trangThai.equals(t.getITrangThaiDuyet()))
                .map(t -> mapper.toTinView(t, null))
                .toList();
        return ApiResponse.ok("Danh sách tin tuyển dụng phục vụ kiểm duyệt.", list);
    }

    // R15: Phê duyệt / Từ chối tin tuyển dụng mới (Phương thức XuLyPheDuyet trong Mục 4.7.2)
    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @PutMapping("/tin-tuyen-dung/{maTin}")
    @Transactional
    public ApiResponse<VTinTuyenDung.TinTuyenDungView> xuLyPheDuyet(
            @AuthenticationPrincipal UserPrincipal canBo,
            @PathVariable String maTin,
            @RequestBody VPheDuyet.DuyetTinRequest req,
            HttpServletRequest httpReq
    ) {
        MTinTuyenDung tin = tinRepo.findActiveById(maTin)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tin tuyển dụng " + maTin, HttpStatus.NOT_FOUND));
        int status = req.getITrangThaiDuyet() != null ? req.getITrangThaiDuyet() : -1;
        if (status != 1 && status != 2) {
            throw new BusinessException("Trạng thái duyệt tin phải là 1 (Phê duyệt) hoặc 2 (Từ chối).");
        }
        String lyDo = TextNormalizer.normalizeText(req.getSLyDoTuChoi());
        if (status == 2 && (lyDo == null || lyDo.isBlank())) {
            throw new BusinessException("Quy định R15: Vui lòng nhập rõ lý do từ chối tin tuyển dụng.");
        }

        int cu = tin.getITrangThaiDuyet();
        tin.setITrangThaiDuyet(status);
        tin.setFkMaCanBoDuyet(canBo.getPkMaTaiKhoan());
        tin.setDNgayDuyet(LocalDateTime.now());
        tin.setSLyDoTuChoi(status == 2 ? lyDo : null);
        tinRepo.save(tin);

        audit.ghiNhatKy(canBo.getPkMaTaiKhoan(), status == 1 ? "PHE_DUYET_TIN_TUYEN_DUNG" : "TU_CHOI_TIN_TUYEN_DUNG",
                "INFO", "{\"iTrangThaiDuyet\":" + cu + "}",
                "{\"PK_MaTin\":\"" + maTin + "\",\"iTrangThaiDuyet\":" + status + "}", httpReq);

        return ApiResponse.ok(
                status == 1 ? "Đã phê duyệt công khai tin tuyển dụng " + maTin : "Đã từ chối tin tuyển dụng " + maTin,
                mapper.toTinView(tin, null));
    }

    // R16: Gỡ bỏ khẩn cấp tin tuyển dụng vi phạm (Cán bộ Trung tâm hoặc Admin)
    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @PutMapping("/tin-tuyen-dung/{maTin}/go-khan-cap")
    @Transactional
    public ApiResponse<Void> goKhanCapTin(
            @AuthenticationPrincipal UserPrincipal actor,
            @PathVariable String maTin,
            @RequestBody VPheDuyet.GoKhanCapRequest req,
            HttpServletRequest httpReq
    ) {
        MTinTuyenDung tin = tinRepo.findActiveById(maTin)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tin tuyển dụng " + maTin, HttpStatus.NOT_FOUND));
        String lyDo = TextNormalizer.normalizeText(req.getSLyDo());
        if (lyDo == null || lyDo.isBlank()) {
            throw new BusinessException("Bắt buộc nhập căn cứ / lý do gỡ bỏ khẩn cấp tin tuyển dụng vi phạm.");
        }
        int cu = tin.getITrangThaiDuyet();
        tin.setITrangThaiDuyet(3);
        tin.setBDaXoa(true);
        tin.setSLyDoTuChoi(lyDo);
        tin.setFkMaCanBoDuyet(actor.getPkMaTaiKhoan());
        tin.setDNgayDuyet(LocalDateTime.now());
        tinRepo.save(tin);

        audit.ghiNhatKy(actor.getPkMaTaiKhoan(), "GO_KHAN_CAP_TIN_VI_PHAM", "SECURITY_ACTION",
                "{\"PK_MaTin\":\"" + maTin + "\",\"iTrangThaiDuyet\":" + cu + "}",
                "{\"PK_MaTin\":\"" + maTin + "\",\"iTrangThaiDuyet\":3,\"bDaXoa\":1,\"lyDo\":\"" + lyDo + "\"}", httpReq);

        return ApiResponse.ok("Đã gỡ bỏ khẩn cấp tin tuyển dụng " + maTin + " khỏi toàn bộ hệ thống.", null);
    }

    // Phục vụ Cán bộ chọn ứng viên xuất Phiếu đăng ký (R09) hoặc cấp Giấy giới thiệu (R17)
    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @GetMapping("/ung-vien")
    public ApiResponse<List<VNguoiTimViec.HoSoView>> danhSachUngVienChoCanBo() {
        List<VNguoiTimViec.HoSoView> list = hoSoRepo.findAllActive().stream().map(hs -> {
            VNguoiTimViec.HoSoView v = new VNguoiTimViec.HoSoView();
            v.setPkMaUngVien(hs.getPkMaUngVien());
            v.setFkMaTaiKhoan(hs.getFkMaTaiKhoan());
            v.setSHoTen(hs.getSHoTen());
            v.setSSoCCCD(hs.getSSoCCCD());
            v.setDNgaySinh(hs.getDNgaySinh());
            v.setSGioiTinh(hs.getSGioiTinh());
            v.setSSDT(hs.getSSDT());
            v.setSEmailLienHe(hs.getSEmailLienHe());
            v.setFkMaQuanCuTru(hs.getFkMaQuanCuTru());
            v.setSTenQuanCuTru(qhRepo.findActiveById(hs.getFkMaQuanCuTru()).map(MQuanHuyen::getSTenQuanHuyen).orElse(""));
            v.setSTrinhDoChuyenMon(hs.getSTrinhDoChuyenMon());
            v.setSViTriMongMuon(hs.getSViTriMongMuon());
            v.setITyLeHoanThien(hs.getITyLeHoanThien());
            return v;
        }).toList();
        return ApiResponse.ok("Danh sách hồ sơ người tìm việc.", list);
    }

    // R17: Cấp và xuất Giấy giới thiệu việc làm (PDF A4 kèm số công văn & mã QR)
    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @GetMapping("/giay-gioi-thieu-pdf")
    @Transactional
    public ResponseEntity<byte[]> capGiayGioiThieuPdf(
            @AuthenticationPrincipal UserPrincipal canBo,
            @RequestParam String maUngVien,
            @RequestParam String maTin,
            @RequestParam(required = false) String ngayHen,
            @RequestParam(required = false) String ghiChu,
            HttpServletRequest httpReq
    ) {
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveById(maUngVien.trim())
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ ứng viên " + maUngVien, HttpStatus.NOT_FOUND));
        MTinTuyenDung tin = tinRepo.findActiveById(maTin.trim())
                .orElseThrow(() -> new BusinessException("Không tìm thấy tin tuyển dụng " + maTin, HttpStatus.NOT_FOUND));
        if (tin.getITrangThaiDuyet() != 1) {
            throw new BusinessException("Chỉ được cấp Giấy giới thiệu cho tin tuyển dụng đã được phê duyệt.");
        }
        MDoanhNghiep dn = dnRepo.findActiveById(tin.getFkMaDoanhNghiep())
                .orElseThrow(() -> new BusinessException("Không tìm thấy thông tin doanh nghiệp tiếp nhận."));

        String tenQuanUv = qhRepo.findActiveById(hs.getFkMaQuanCuTru()).map(MQuanHuyen::getSTenQuanHuyen).orElse("");
        String tenQuanDn = dn.getFkMaQuanTruSo() != null
                ? qhRepo.findActiveById(dn.getFkMaQuanTruSo()).map(MQuanHuyen::getSTenQuanHuyen).orElse("")
                : "";

        String soCongVan = String.format("GGT-2026/%04d-DVVLHN", REFERRAL_SEQ.getAndIncrement());
        byte[] pdfBytes = pdfService.taoPdfGiayGioiThieu(
                hs, dn, tin, tenQuanUv, tenQuanDn, soCongVan,
                TextNormalizer.normalizeText(ngayHen),
                TextNormalizer.normalizeText(ghiChu),
                canBo.getSHoTen()
        );

        audit.ghiNhatKy(canBo.getPkMaTaiKhoan(), "CAP_GIAY_GIOI_THIEU_VIEC_LAM", "INFO", null,
                String.format("{\"soCongVan\":\"%s\",\"maUngVien\":\"%s\",\"maTin\":\"%s\"}", soCongVan, maUngVien, maTin),
                httpReq);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Giay_Gioi_Thieu_" + maUngVien + "_" + maTin + ".pdf\"")
                .body(pdfBytes);
    }
}
