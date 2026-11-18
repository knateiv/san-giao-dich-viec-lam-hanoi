package vn.gov.hanoi.dvvl.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.gov.hanoi.dvvl.Config.UserPrincipal;
import vn.gov.hanoi.dvvl.Dto.ApiResponse;
import vn.gov.hanoi.dvvl.Dto.VNguoiTimViec;
import vn.gov.hanoi.dvvl.Model.MHoSoNguoiTimViec;
import vn.gov.hanoi.dvvl.Model.MQuanHuyen;
import vn.gov.hanoi.dvvl.Repository.HoSoNguoiTimViecRepository;
import vn.gov.hanoi.dvvl.Repository.QuanHuyenRepository;
import vn.gov.hanoi.dvvl.Service.AuditLogService;
import vn.gov.hanoi.dvvl.Service.BusinessException;
import vn.gov.hanoi.dvvl.Service.FileStorageService;
import vn.gov.hanoi.dvvl.Service.IdGeneratorService;
import vn.gov.hanoi.dvvl.Service.PdfExportService;
import vn.gov.hanoi.dvvl.Service.TextNormalizer;

/**
 * Controller Phân hệ 2 — Quản lý Hồ sơ Người tìm việc (R06, R07, R08, R09)
 * Khớp phương thức thiết kế Mục 4.7.1: kiemTraHoSo, kiemTraCccdTonTai, themNguoiTimViec, taoPdfPhieuTimViec
 */
@RestController
@RequestMapping("/api/nguoi-tim-viec")
public class CNguoiTimViec {

    private static final Pattern CCCD_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");

    private final HoSoNguoiTimViecRepository hoSoRepo;
    private final QuanHuyenRepository quanHuyenRepo;
    private final IdGeneratorService idGenerator;
    private final AuditLogService auditLog;
    private final FileStorageService fileStorage;
    private final PdfExportService pdfService;

    public CNguoiTimViec(
            HoSoNguoiTimViecRepository hoSoRepo,
            QuanHuyenRepository quanHuyenRepo,
            IdGeneratorService idGenerator,
            AuditLogService auditLog,
            FileStorageService fileStorage,
            PdfExportService pdfService
    ) {
        this.hoSoRepo = hoSoRepo;
        this.quanHuyenRepo = quanHuyenRepo;
        this.idGenerator = idGenerator;
        this.auditLog = auditLog;
        this.fileStorage = fileStorage;
        this.pdfService = pdfService;
    }

    @PreAuthorize("hasRole('NTV')")
    @GetMapping("/ho-so")
    public ApiResponse<VNguoiTimViec.HoSoView> layHoSoCuaToi(@AuthenticationPrincipal UserPrincipal user) {
        return hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .map(hs -> ApiResponse.ok("Thông tin hồ sơ người tìm việc.", toView(hs)))
                .orElseGet(() -> ApiResponse.ok("Chưa khởi tạo hồ sơ ứng viên.", null));
    }

    // R06: Tạo mới hoặc cập nhật & chuẩn hóa hồ sơ người tìm việc
    @PreAuthorize("hasRole('NTV')")
    @PutMapping("/ho-so")
    @Transactional
    public ApiResponse<VNguoiTimViec.HoSoView> themHoacCapNhatHoSo(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody VNguoiTimViec.HoSoRequest req,
            HttpServletRequest httpReq
    ) {
        kiemTraHoSo(req);
        MHoSoNguoiTimViec existing = hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan()).orElse(null);
        kiemTraCccdTonTai(req.getSSoCCCD().trim(), existing != null ? existing.getPkMaUngVien() : null);

        MHoSoNguoiTimViec hs = existing != null ? existing : new MHoSoNguoiTimViec();
        boolean isNew = (existing == null);
        if (isNew) {
            hs.setPkMaUngVien(idGenerator.nextCode("tbl_HoSoNguoiTimViec", "PK_MaUngVien", "SK"));
            hs.setFkMaTaiKhoan(user.getPkMaTaiKhoan());
            hs.setBDaXoa(false);
        }
        themNguoiTimViec(hs, req, user.getSEmail());
        hoSoRepo.save(hs);

        auditLog.ghiNhatKy(user.getPkMaTaiKhoan(), isNew ? "TAO_HO_SO_NTV" : "CAP_NHAT_HO_SO_NTV", "INFO",
                null, "{\"PK_MaUngVien\":\"" + hs.getPkMaUngVien() + "\",\"iTyLeHoanThien\":" + hs.getITyLeHoanThien() + "}", httpReq);

        return ApiResponse.ok("Lưu và chuẩn hóa hồ sơ ứng viên thành công.", toView(hs));
    }

    // R07: Bật / Tắt trạng thái tìm việc công khai
    @PreAuthorize("hasRole('NTV')")
    @PatchMapping("/trang-thai-tim-viec")
    @Transactional
    public ApiResponse<VNguoiTimViec.HoSoView> doiTrangThaiTimViec(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody Map<String, Boolean> body,
            HttpServletRequest httpReq
    ) {
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Vui lòng tạo và hoàn thiện hồ sơ trước khi bật trạng thái công khai."));
        boolean congKhai = Boolean.TRUE.equals(body.get("bDangTimViecCongKhai"));
        if (congKhai && hs.getITyLeHoanThien() < 60) {
            throw new BusinessException("Vui lòng hoàn thiện đầy đủ thông tin bắt buộc trước khi bật tìm việc công khai.");
        }
        boolean cu = Boolean.TRUE.equals(hs.getBDangTimViecCongKhai());
        hs.setBDangTimViecCongKhai(congKhai);
        hoSoRepo.save(hs);

        auditLog.ghiNhatKy(user.getPkMaTaiKhoan(), "DOI_TRANG_THAI_TIM_VIEC", "INFO",
                "{\"bDangTimViecCongKhai\":" + cu + "}",
                "{\"bDangTimViecCongKhai\":" + congKhai + "}", httpReq);
        return ApiResponse.ok(
                congKhai ? "Đã BẬT trạng thái tìm việc công khai." : "Đã TẮT trạng thái tìm việc công khai.",
                toView(hs));
    }

    // R08: Tải lên tệp CV mềm PDF (<= 5MB, Magic Bytes %PDF-)
    @PreAuthorize("hasRole('NTV')")
    @PostMapping("/upload-cv")
    @Transactional
    public ApiResponse<VNguoiTimViec.HoSoView> uploadCv(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpReq
    ) {
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Vui lòng lưu hồ sơ thông tin cá nhân trước khi đính kèm CV PDF."));
        String relPath = fileStorage.savePdfCv(hs.getPkMaUngVien(), file);
        hs.setSDuongDanCV(relPath);
        hs.setITyLeHoanThien(tinhTyLeHoanThien(hs));
        hoSoRepo.save(hs);

        auditLog.ghiNhatKy(user.getPkMaTaiKhoan(), "TAI_LEN_CV_PDF", "INFO", null, "{\"sDuongDanCV\":\"" + relPath + "\"}", httpReq);
        return ApiResponse.ok("Tải lên tệp CV mềm (PDF) thành công.", toView(hs));
    }

    @PreAuthorize("hasAnyRole('NTV', 'DN', 'CANBO', 'ADMIN')")
    @GetMapping("/cv-file/{maUngVien}")
    public ResponseEntity<Resource> taiCvFile(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maUngVien
    ) {
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveById(maUngVien)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ ứng viên.", HttpStatus.NOT_FOUND));
        if (user.getFkMaVaiTro() == 1 && !hs.getFkMaTaiKhoan().equals(user.getPkMaTaiKhoan())) {
            throw new BusinessException("Bạn không có quyền tải tệp CV của ứng viên khác.", HttpStatus.FORBIDDEN);
        }
        Resource res = fileStorage.loadAsResource(hs.getSDuongDanCV());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"CV_" + maUngVien + ".pdf\"")
                .body(res);
    }

    // R09: Kết xuất Phiếu đăng ký tìm việc làm (PDF A4 kèm mã QR)
    @PreAuthorize("hasAnyRole('NTV', 'CANBO', 'ADMIN')")
    @GetMapping("/phieu-dang-ky-pdf/{maUngVien}")
    public ResponseEntity<byte[]> taoPdfPhieuTimViec(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maUngVien
    ) {
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveById(maUngVien)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ ứng viên.", HttpStatus.NOT_FOUND));
        if (user.getFkMaVaiTro() == 1 && !hs.getFkMaTaiKhoan().equals(user.getPkMaTaiKhoan())) {
            throw new BusinessException("Bạn chỉ được phép xuất Phiếu đăng ký của chính mình.", HttpStatus.FORBIDDEN);
        }
        String tenQuan = quanHuyenRepo.findActiveById(hs.getFkMaQuanCuTru())
                .map(MQuanHuyen::getSTenQuanHuyen).orElse("");
        byte[] pdfBytes = pdfService.taoPdfPhieuTimViec(hs, tenQuan);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Phieu_Dang_Ky_Tim_Viec_" + maUngVien + ".pdf\"")
                .body(pdfBytes);
    }

    // Phương thức chuẩn Mục 4.7.1: KiemTraHoSo()
    private void kiemTraHoSo(VNguoiTimViec.HoSoRequest req) {
        String cccd = TextNormalizer.normalizeText(req.getSSoCCCD());
        if (!CCCD_PATTERN.matcher(cccd).matches()) {
            throw new BusinessException("Số CCCD bắt buộc phải gồm đúng 12 chữ số.");
        }
        String sdt = TextNormalizer.normalizeText(req.getSSDT());
        if (!PHONE_PATTERN.matcher(sdt).matches()) {
            throw new BusinessException("Số điện thoại bắt buộc phải gồm 10 chữ số, bắt đầu bằng số 0.");
        }
        int tuoi = Period.between(req.getDNgaySinh(), LocalDate.now()).getYears();
        if (tuoi < 15 || tuoi > 80) {
            throw new BusinessException("Theo Bộ luật Lao động, người tìm việc phải đủ từ 15 tuổi trở lên.");
        }
        if (quanHuyenRepo.findActiveById(req.getFkMaQuanCuTru().trim()).isEmpty()) {
            throw new BusinessException("Quận/Huyện cư trú tại Hà Nội không hợp lệ.");
        }
        if (req.getFMucLuongKyVong() != null && req.getFMucLuongKyVong() < 0) {
            throw new BusinessException("Mức lương kỳ vọng không được nhỏ hơn 0.");
        }
    }

    // Phương thức chuẩn Mục 4.7.1: KiemTraCCCDTonTai()
    private void kiemTraCccdTonTai(String cccd, String excludeId) {
        if (hoSoRepo.existsActiveByCccd(cccd, excludeId)) {
            throw new BusinessException("Số CCCD này đã tồn tại trên một hồ sơ ứng viên khác.");
        }
    }

    // Phương thức chuẩn Mục 4.7.1: ThemNguoiTimViec() + Thuật toán chuẩn hóa chuỗi chống dính chữ (ISS-04)
    private void themNguoiTimViec(MHoSoNguoiTimViec hs, VNguoiTimViec.HoSoRequest req, String defaultEmail) {
        hs.setSHoTen(TextNormalizer.normalizeProperName(req.getSHoTen()));
        hs.setSSoCCCD(TextNormalizer.normalizeText(req.getSSoCCCD()));
        hs.setDNgaySinh(req.getDNgaySinh());
        hs.setSGioiTinh(TextNormalizer.normalizeText(req.getSGioiTinh()));
        hs.setSSDT(TextNormalizer.normalizeText(req.getSSDT()));
        hs.setSEmailLienHe(req.getSEmailLienHe() != null && !req.getSEmailLienHe().isBlank()
                ? TextNormalizer.normalizeText(req.getSEmailLienHe()).toLowerCase()
                : defaultEmail);
        hs.setFkMaQuanCuTru(req.getFkMaQuanCuTru().trim());
        hs.setSTrinhDoVanHoa(TextNormalizer.normalizeText(req.getSTrinhDoVanHoa()));
        hs.setSTrinhDoChuyenMon(TextNormalizer.normalizeText(req.getSTrinhDoChuyenMon()));
        hs.setSKyNangTinHoc(TextNormalizer.normalizeText(req.getSKyNangTinHoc()));
        hs.setSTrinhDoNgoaiNgu(TextNormalizer.normalizeText(req.getSTrinhDoNgoaiNgu()));
        hs.setSKinhNghiemLamViec(TextNormalizer.normalizeMultiline(req.getSKinhNghiemLamViec()));
        hs.setSViTriMongMuon(TextNormalizer.normalizeText(req.getSViTriMongMuon()));
        hs.setFMucLuongKyVong(req.getFMucLuongKyVong());
        hs.setSDiaBanMongMuon(TextNormalizer.normalizeText(req.getSDiaBanMongMuon()));
        if (req.getBDangTimViecCongKhai() != null) {
            hs.setBDangTimViecCongKhai(req.getBDangTimViecCongKhai());
        }
        hs.setITyLeHoanThien(tinhTyLeHoanThien(hs));
    }

    private int tinhTyLeHoanThien(MHoSoNguoiTimViec hs) {
        int score = 40; // Đã đủ thông tin định danh bắt buộc
        if (notBlank(hs.getSTrinhDoVanHoa())) score += 10;
        if (notBlank(hs.getSTrinhDoChuyenMon())) score += 10;
        if (notBlank(hs.getSKyNangTinHoc())) score += 10;
        if (notBlank(hs.getSTrinhDoNgoaiNgu())) score += 5;
        if (notBlank(hs.getSKinhNghiemLamViec())) score += 10;
        if (notBlank(hs.getSViTriMongMuon())) score += 10;
        if (notBlank(hs.getSDiaBanMongMuon()) || notBlank(hs.getSDuongDanCV())) score += 5;
        return Math.min(100, score);
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private VNguoiTimViec.HoSoView toView(MHoSoNguoiTimViec hs) {
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
        v.setSTenQuanCuTru(quanHuyenRepo.findActiveById(hs.getFkMaQuanCuTru()).map(MQuanHuyen::getSTenQuanHuyen).orElse(""));
        v.setSTrinhDoVanHoa(hs.getSTrinhDoVanHoa());
        v.setSTrinhDoChuyenMon(hs.getSTrinhDoChuyenMon());
        v.setSKyNangTinHoc(hs.getSKyNangTinHoc());
        v.setSTrinhDoNgoaiNgu(hs.getSTrinhDoNgoaiNgu());
        v.setSKinhNghiemLamViec(hs.getSKinhNghiemLamViec());
        v.setSViTriMongMuon(hs.getSViTriMongMuon());
        v.setFMucLuongKyVong(hs.getFMucLuongKyVong());
        v.setSDiaBanMongMuon(hs.getSDiaBanMongMuon());
        v.setSDuongDanCV(hs.getSDuongDanCV());
        v.setITyLeHoanThien(hs.getITyLeHoanThien());
        v.setBDangTimViecCongKhai(hs.getBDangTimViecCongKhai());
        return v;
    }
}
