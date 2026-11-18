package vn.gov.hanoi.dvvl.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import vn.gov.hanoi.dvvl.Dto.VDoanhNghiep;
import vn.gov.hanoi.dvvl.Model.MDoanhNghiep;
import vn.gov.hanoi.dvvl.Model.MQuanHuyen;
import vn.gov.hanoi.dvvl.Repository.DoanhNghiepRepository;
import vn.gov.hanoi.dvvl.Repository.QuanHuyenRepository;
import vn.gov.hanoi.dvvl.Service.AuditLogService;
import vn.gov.hanoi.dvvl.Service.BusinessException;
import vn.gov.hanoi.dvvl.Service.FileStorageService;
import vn.gov.hanoi.dvvl.Service.IdGeneratorService;
import vn.gov.hanoi.dvvl.Service.TextNormalizer;

@RestController
@RequestMapping("/api/doanh-nghiep")
public class CDoanhNghiep {

    private static final Pattern MST_REGEX = Pattern.compile("^\\d{10}(\\d{3})?$");
    private static final Pattern SDT_REGEX = Pattern.compile("^0\\d{9}$");

    private final DoanhNghiepRepository dnRepo;
    private final QuanHuyenRepository qhRepo;
    private final IdGeneratorService idGen;
    private final AuditLogService audit;
    private final FileStorageService storage;

    public CDoanhNghiep(
            DoanhNghiepRepository dnRepo,
            QuanHuyenRepository qhRepo,
            IdGeneratorService idGen,
            AuditLogService audit,
            FileStorageService storage
    ) {
        this.dnRepo = dnRepo;
        this.qhRepo = qhRepo;
        this.idGen = idGen;
        this.audit = audit;
        this.storage = storage;
    }

    @PreAuthorize("hasRole('DN')")
    @GetMapping("/ho-so")
    public ApiResponse<VDoanhNghiep.DoanhNghiepView> layHoSoCuaToi(@AuthenticationPrincipal UserPrincipal user) {
        return dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .map(dn -> ApiResponse.ok("Thông tin pháp lý doanh nghiệp.", toView(dn)))
                .orElseGet(() -> ApiResponse.ok("Chưa khởi tạo hồ sơ pháp lý.", null));
    }

    // R10: Cập nhật thông tin pháp lý doanh nghiệp
    @PreAuthorize("hasRole('DN')")
    @PutMapping("/ho-so")
    @Transactional
    public ApiResponse<VDoanhNghiep.DoanhNghiepView> capNhatPhapLy(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody VDoanhNghiep.PhapLyRequest req,
            HttpServletRequest httpReq
    ) {
        String mst = TextNormalizer.normalizeText(req.getSMaSoThue());
        if (!MST_REGEX.matcher(mst).matches()) {
            throw new BusinessException("Mã số thuế phải gồm đúng 10 hoặc 13 chữ số.");
        }
        String sdt = TextNormalizer.normalizeText(req.getSSDTNguoiLienHe());
        if (!SDT_REGEX.matcher(sdt).matches()) {
            throw new BusinessException("Số điện thoại liên hệ phải có 10 chữ số, bắt đầu bằng 0.");
        }
        if (qhRepo.findActiveById(req.getFkMaQuanTruSo().trim()).isEmpty()) {
            throw new BusinessException("Quận/Huyện trụ sở không hợp lệ.");
        }

        MDoanhNghiep cu = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan()).orElse(null);
        if (dnRepo.existsActiveByTaxCode(mst, cu != null ? cu.getPkMaDoanhNghiep() : null)) {
            throw new BusinessException("Mã số thuế đã được đăng ký bởi đơn vị khác.");
        }

        MDoanhNghiep dn = cu != null ? cu : new MDoanhNghiep();
        if (cu == null) {
            dn.setPkMaDoanhNghiep(idGen.nextCode("tbl_DoanhNghiep", "PK_MaDoanhNghiep", "DN"));
            dn.setFkMaTaiKhoan(user.getPkMaTaiKhoan());
            dn.setBDaXoa(false);
        }

        String tenMoi = TextNormalizer.normalizeText(req.getSTenDoanhNghiep());
        boolean resetVerify = cu == null || !tenMoi.equals(cu.getSTenDoanhNghiep()) || !mst.equals(cu.getSMaSoThue());

        dn.setSTenDoanhNghiep(tenMoi);
        dn.setSTenVietTat(TextNormalizer.normalizeText(req.getSTenVietTat()));
        dn.setSMaSoThue(mst);
        dn.setSDiaChiTruSo(TextNormalizer.normalizeText(req.getSDiaChiTruSo()));
        dn.setFkMaQuanTruSo(req.getFkMaQuanTruSo().trim());
        dn.setSLinhVucHoatDong(TextNormalizer.normalizeText(req.getSLinhVucHoatDong()));
        dn.setSNguoiDaiDien(TextNormalizer.normalizeProperName(req.getSNguoiDaiDien()));
        dn.setSTenNguoiLienHe(TextNormalizer.normalizeProperName(req.getSTenNguoiLienHe()));
        dn.setSSDTNguoiLienHe(sdt);
        dn.setSEmailNguoiLienHe(TextNormalizer.normalizeText(req.getSEmailNguoiLienHe()).toLowerCase());

        if (resetVerify) {
            dn.setITrangThaiThamDinh(0);
            dn.setSGhiChuThamDinh("Đã gửi thông tin pháp lý, đang chờ Cán bộ Trung tâm thẩm định.");
        }
        dnRepo.save(dn);

        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "CAP_NHAT_PHAP_LY", "INFO", null,
                "{\"PK_MaDoanhNghiep\":\"" + dn.getPkMaDoanhNghiep() + "\"}", httpReq);
        return ApiResponse.ok("Đã gửi thông tin pháp lý, đang chờ Cán bộ thẩm định.", toView(dn));
    }

    // R10: Upload bản scan GPKD
    @PreAuthorize("hasRole('DN')")
    @PostMapping("/upload-giay-phep")
    @Transactional
    public ApiResponse<VDoanhNghiep.DoanhNghiepView> uploadGiayPhep(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpReq
    ) {
        MDoanhNghiep dn = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Vui lòng lưu thông tin doanh nghiệp trước."));
        String path = storage.saveGiayPhep(dn.getPkMaDoanhNghiep(), file);
        dn.setSDuongDanGiayPhep(path);
        dn.setITrangThaiThamDinh(0);
        dn.setSGhiChuThamDinh("Đã bổ sung bản scan Giấy phép ĐKKD mới, chờ Cán bộ thẩm định.");
        dnRepo.save(dn);
        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "TAI_LEN_GPKD", "INFO", null, "{\"path\":\"" + path + "\"}", httpReq);
        return ApiResponse.ok("Tải lên Giấy phép ĐKKD thành công.", toView(dn));
    }

    @PreAuthorize("hasAnyRole('DN', 'CANBO', 'ADMIN')")
    @GetMapping("/giay-phep/{maDn}")
    public ResponseEntity<Resource> xemGiayPhep(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maDn
    ) {
        MDoanhNghiep dn = dnRepo.findActiveById(maDn)
                .orElseThrow(() -> new BusinessException("Không tìm thấy doanh nghiệp.", HttpStatus.NOT_FOUND));
        if (user.getFkMaVaiTro() == 2 && !dn.getFkMaTaiKhoan().equals(user.getPkMaTaiKhoan())) {
            throw new BusinessException("Không có quyền xem hồ sơ của đơn vị khác.", HttpStatus.FORBIDDEN);
        }
        Resource res = storage.loadAsResource(dn.getSDuongDanGiayPhep());
        String p = dn.getSDuongDanGiayPhep().toLowerCase();
        MediaType mt = p.endsWith(".pdf") ? MediaType.APPLICATION_PDF : (p.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG);
        return ResponseEntity.ok().contentType(mt).header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"GPKD_" + maDn + "\"").body(res);
    }

    public VDoanhNghiep.DoanhNghiepView toView(MDoanhNghiep dn) {
        VDoanhNghiep.DoanhNghiepView v = new VDoanhNghiep.DoanhNghiepView();
        v.setPkMaDoanhNghiep(dn.getPkMaDoanhNghiep());
        v.setFkMaTaiKhoan(dn.getFkMaTaiKhoan());
        v.setSTenDoanhNghiep(dn.getSTenDoanhNghiep());
        v.setSTenVietTat(dn.getSTenVietTat());
        v.setSMaSoThue(dn.getSMaSoThue());
        v.setSDuongDanGiayPhep(dn.getSDuongDanGiayPhep());
        v.setSDiaChiTruSo(dn.getSDiaChiTruSo());
        v.setFkMaQuanTruSo(dn.getFkMaQuanTruSo());
        if (dn.getFkMaQuanTruSo() != null) {
            v.setSTenQuanTruSo(qhRepo.findActiveById(dn.getFkMaQuanTruSo()).map(MQuanHuyen::getSTenQuanHuyen).orElse(""));
        }
        v.setSLinhVucHoatDong(dn.getSLinhVucHoatDong());
        v.setSNguoiDaiDien(dn.getSNguoiDaiDien());
        v.setSTenNguoiLienHe(dn.getSTenNguoiLienHe());
        v.setSSDTNguoiLienHe(dn.getSSDTNguoiLienHe());
        v.setSEmailNguoiLienHe(dn.getSEmailNguoiLienHe());
        v.setITrangThaiThamDinh(dn.getITrangThaiThamDinh());
        v.setTenTrangThaiThamDinh(switch (dn.getITrangThaiThamDinh()) {
            case 1 -> "Đã xác minh";
            case 2 -> "Bị từ chối";
            default -> "Chờ thẩm định";
        });
        v.setSGhiChuThamDinh(dn.getSGhiChuThamDinh());
        return v;
    }
}
