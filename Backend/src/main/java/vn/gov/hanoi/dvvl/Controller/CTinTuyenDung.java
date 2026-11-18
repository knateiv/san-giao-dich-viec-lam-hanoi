package vn.gov.hanoi.dvvl.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import vn.gov.hanoi.dvvl.Dto.VTinTuyenDung;
import vn.gov.hanoi.dvvl.Dto.VUngTuyen;
import vn.gov.hanoi.dvvl.Model.MDoanhNghiep;
import vn.gov.hanoi.dvvl.Model.MTinTuyenDung;
import vn.gov.hanoi.dvvl.Repository.DoanhNghiepRepository;
import vn.gov.hanoi.dvvl.Repository.NganhNgheRepository;
import vn.gov.hanoi.dvvl.Repository.QuanHuyenRepository;
import vn.gov.hanoi.dvvl.Repository.TinTuyenDungRepository;
import vn.gov.hanoi.dvvl.Repository.UngTuyenRepository;
import vn.gov.hanoi.dvvl.Service.AuditLogService;
import vn.gov.hanoi.dvvl.Service.BusinessException;
import vn.gov.hanoi.dvvl.Service.IdGeneratorService;
import vn.gov.hanoi.dvvl.Service.TextNormalizer;
import vn.gov.hanoi.dvvl.Service.ViewMapperService;

/**
 * Controller Phân hệ 3 — Quản lý Tuyển dụng & Doanh nghiệp (R11, R12, R13)
 * Khớp Mục 4.7.2: kiemTraDuLieuTin, themTinTuyenDung
 */
@RestController
@RequestMapping("/api/tin-tuyen-dung")
public class CTinTuyenDung {

    private final TinTuyenDungRepository tinRepo;
    private final DoanhNghiepRepository dnRepo;
    private final NganhNgheRepository nnRepo;
    private final QuanHuyenRepository qhRepo;
    private final UngTuyenRepository utRepo;
    private final IdGeneratorService idGen;
    private final AuditLogService audit;
    private final ViewMapperService mapper;

    public CTinTuyenDung(
            TinTuyenDungRepository tinRepo,
            DoanhNghiepRepository dnRepo,
            NganhNgheRepository nnRepo,
            QuanHuyenRepository qhRepo,
            UngTuyenRepository utRepo,
            IdGeneratorService idGen,
            AuditLogService audit,
            ViewMapperService mapper
    ) {
        this.tinRepo = tinRepo;
        this.dnRepo = dnRepo;
        this.nnRepo = nnRepo;
        this.qhRepo = qhRepo;
        this.utRepo = utRepo;
        this.idGen = idGen;
        this.audit = audit;
        this.mapper = mapper;
    }

    @PreAuthorize("hasRole('DN')")
    @GetMapping("/cua-toi")
    public ApiResponse<List<VTinTuyenDung.TinTuyenDungView>> danhSachTinCuaToi(@AuthenticationPrincipal UserPrincipal user) {
        MDoanhNghiep dn = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Doanh nghiệp chưa cập nhật hồ sơ pháp lý.", HttpStatus.NOT_FOUND));
        List<VTinTuyenDung.TinTuyenDungView> ds = tinRepo.findByEmployer(dn.getPkMaDoanhNghiep()).stream()
                .map(t -> mapper.toTinView(t, null))
                .toList();
        return ApiResponse.ok("Danh sách tin tuyển dụng của doanh nghiệp.", ds);
    }

    // R11: Soạn thảo & đăng tin tuyển dụng mới (yêu cầu DN đã thẩm định iTrangThaiThamDinh = 1)
    @PreAuthorize("hasRole('DN')")
    @PostMapping
    @Transactional
    public ApiResponse<VTinTuyenDung.TinTuyenDungView> dangTinMoi(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody VTinTuyenDung.TinRequest req,
            HttpServletRequest httpReq
    ) {
        MDoanhNghiep dn = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Vui lòng cập nhật hồ sơ pháp lý doanh nghiệp trước khi đăng tin."));
        if (dn.getITrangThaiThamDinh() == null || dn.getITrangThaiThamDinh() != 1) {
            throw new BusinessException("Doanh nghiệp chưa được Cán bộ Trung tâm xác minh pháp lý nên chưa thể đăng tin tuyển dụng.", HttpStatus.FORBIDDEN);
        }

        kiemTraDuLieuTin(req, true);
        MTinTuyenDung tin = new MTinTuyenDung();
        tin.setPkMaTin(idGen.nextCode("tbl_TinTuyenDung", "PK_MaTin", "JB"));
        tin.setFkMaDoanhNghiep(dn.getPkMaDoanhNghiep());
        themTinTuyenDung(tin, req);
        tin.setITrangThaiDuyet(0);
        tin.setBDaDong(false);
        tin.setBDaXoa(false);
        tinRepo.save(tin);

        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "DANG_TIN_TUYEN_DUNG", "INFO", null,
                "{\"PK_MaTin\":\"" + tin.getPkMaTin() + "\",\"iTrangThaiDuyet\":0}", httpReq);
        return ApiResponse.ok("Đã gửi tin tuyển dụng mới thành công (Trạng thái: Chờ Cán bộ Trung tâm duyệt).", mapper.toTinView(tin, null));
    }

    // R12: Chỉnh sửa tin tuyển dụng (sửa nội dung trọng yếu tự động đưa về Chờ duyệt = 0)
    @PreAuthorize("hasRole('DN')")
    @PutMapping("/{maTin}")
    @Transactional
    public ApiResponse<VTinTuyenDung.TinTuyenDungView> suaTin(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maTin,
            @Valid @RequestBody VTinTuyenDung.TinRequest req,
            HttpServletRequest httpReq
    ) {
        MTinTuyenDung tin = getOwnedJob(user, maTin);
        kiemTraDuLieuTin(req, false);

        String tieuDeMoi = TextNormalizer.normalizeText(req.getSTieuDe());
        String viTriMoi = TextNormalizer.normalizeText(req.getSViTriChucDanh());
        String moTaMoi = TextNormalizer.normalizeMultiline(req.getSMoTaCongViec());
        boolean coreChanged = !Objects.equals(tin.getSTieuDe(), tieuDeMoi)
                || !Objects.equals(tin.getSViTriChucDanh(), viTriMoi)
                || !Objects.equals(tin.getFkMaNganhNghe(), req.getFkMaNganhNghe())
                || !Objects.equals(tin.getFkMaQuanLamViec(), req.getFkMaQuanLamViec().trim())
                || !Objects.equals(tin.getFLuongToiThieu(), req.getFLuongToiThieu())
                || !Objects.equals(tin.getFLuongToiDa(), req.getFLuongToiDa())
                || !Objects.equals(tin.getSMoTaCongViec(), moTaMoi);

        themTinTuyenDung(tin, req);
        if (coreChanged) {
            tin.setITrangThaiDuyet(0);
            tin.setFkMaCanBoDuyet(null);
            tin.setDNgayDuyet(null);
            tin.setSLyDoTuChoi(null);
        }
        tinRepo.save(tin);

        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "SUA_TIN_TUYEN_DUNG", "INFO", null,
                "{\"PK_MaTin\":\"" + maTin + "\",\"resetChoDuyet\":" + coreChanged + "}", httpReq);
        return ApiResponse.ok(
                coreChanged
                        ? "Đã cập nhật tin tuyển dụng. Do thay đổi nội dung trọng yếu, tin đã chuyển về trạng thái Chờ duyệt lại."
                        : "Đã cập nhật thông tin tin tuyển dụng.",
                mapper.toTinView(tin, null));
    }

    // R12: Đóng / Mở lại tin tuyển dụng khi đã tuyển đủ số lượng
    @PreAuthorize("hasRole('DN')")
    @PatchMapping("/{maTin}/dong-tin")
    @Transactional
    public ApiResponse<VTinTuyenDung.TinTuyenDungView> dongMoTin(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maTin,
            @RequestBody(required = false) Map<String, Boolean> body,
            HttpServletRequest httpReq
    ) {
        MTinTuyenDung tin = getOwnedJob(user, maTin);
        boolean daDong = body == null || !body.containsKey("bDaDong") || Boolean.TRUE.equals(body.get("bDaDong"));
        tin.setBDaDong(daDong);
        tinRepo.save(tin);

        audit.ghiNhatKy(user.getPkMaTaiKhoan(), daDong ? "DONG_TIN_TUYEN_DUNG" : "MO_LAI_TIN_TUYEN_DUNG",
                "INFO", null, "{\"PK_MaTin\":\"" + maTin + "\",\"bDaDong\":" + daDong + "}", httpReq);
        return ApiResponse.ok(daDong ? "Đã đóng tin tuyển dụng." : "Đã mở lại tin tuyển dụng.", mapper.toTinView(tin, null));
    }

    // R12: Gia hạn thời hạn nộp hồ sơ
    @PreAuthorize("hasRole('DN')")
    @PatchMapping("/{maTin}/gia-han")
    @Transactional
    public ApiResponse<VTinTuyenDung.TinTuyenDungView> giaHanTin(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maTin,
            @Valid @RequestBody VTinTuyenDung.GiaHanRequest req,
            HttpServletRequest httpReq
    ) {
        MTinTuyenDung tin = getOwnedJob(user, maTin);
        LocalDate hanCu = tin.getDHanNopHoSo();
        LocalDate hanMoi = req.getDHanNopMoi();
        if (!hanMoi.isAfter(LocalDate.now())) {
            throw new BusinessException("Hạn nộp hồ sơ gia hạn phải sau ngày hiện tại.");
        }
        if (hanCu != null && !hanMoi.isAfter(hanCu)) {
            throw new BusinessException("Hạn nộp hồ sơ mới (" + hanMoi + ") phải sau hạn nộp cũ (" + hanCu + ").");
        }
        tin.setDHanNopHoSo(hanMoi);
        tin.setBDaDong(false);
        tinRepo.save(tin);

        audit.ghiNhatKy(user.getPkMaTaiKhoan(), "GIA_HAN_TIN_TUYEN_DUNG", "INFO",
                "{\"dHanNopHoSo\":\"" + hanCu + "\"}",
                "{\"PK_MaTin\":\"" + maTin + "\",\"dHanNopHoSo\":\"" + hanMoi + "\"}", httpReq);
        return ApiResponse.ok("Gia hạn tin tuyển dụng đến ngày " + hanMoi + " thành công.", mapper.toTinView(tin, null));
    }

    // R13: Xem danh sách hồ sơ ứng viên nộp vào từng tin tuyển dụng
    @PreAuthorize("hasRole('DN')")
    @GetMapping("/{maTin}/ung-vien")
    public ApiResponse<List<VUngTuyen.UngTuyenView>> danhSachUngVienNopVaoTin(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String maTin
    ) {
        getOwnedJob(user, maTin);
        List<VUngTuyen.UngTuyenView> list = utRepo.findByJob(maTin).stream()
                .map(u -> mapper.toUngTuyenView(u, true))
                .toList();
        return ApiResponse.ok("Danh sách hồ sơ ứng tuyển vào tin " + maTin, list);
    }

    private MTinTuyenDung getOwnedJob(UserPrincipal user, String maTin) {
        MDoanhNghiep dn = dnRepo.findActiveByTaiKhoan(user.getPkMaTaiKhoan())
                .orElseThrow(() -> new BusinessException("Không tìm thấy hồ sơ doanh nghiệp.", HttpStatus.NOT_FOUND));
        MTinTuyenDung tin = tinRepo.findActiveById(maTin)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tin tuyển dụng " + maTin, HttpStatus.NOT_FOUND));
        if (!tin.getFkMaDoanhNghiep().equals(dn.getPkMaDoanhNghiep())) {
            throw new BusinessException("Bạn không có quyền thao tác trên tin tuyển dụng của doanh nghiệp khác.", HttpStatus.FORBIDDEN);
        }
        return tin;
    }

    // Phương thức chuẩn Mục 4.7.2: KiemTraDuLieuTin()
    private void kiemTraDuLieuTin(VTinTuyenDung.TinRequest req, boolean isNew) {
        if (nnRepo.findActiveById(req.getFkMaNganhNghe()).isEmpty()) {
            throw new BusinessException("Ngành nghề tuyển dụng không hợp lệ.");
        }
        if (qhRepo.findActiveById(req.getFkMaQuanLamViec().trim()).isEmpty()) {
            throw new BusinessException("Quận/Huyện làm việc tại Hà Nội không hợp lệ.");
        }
        if (req.getFLuongToiThieu() < 0 || req.getFLuongToiDa() < req.getFLuongToiThieu()) {
            throw new BusinessException("Mức lương tối đa phải lớn hơn hoặc bằng mức lương tối thiểu.");
        }
        if (isNew) {
            if (req.getDHanNopHoSo().isBefore(LocalDate.now().plusDays(3))) {
                throw new BusinessException("Quy định R11: Hạn nộp hồ sơ khi đăng tin mới phải sau ngày hiện tại tối thiểu 03 ngày.");
            }
        } else if (!req.getDHanNopHoSo().isAfter(LocalDate.now())) {
            throw new BusinessException("Hạn nộp hồ sơ không được nằm trong quá khứ.");
        }
    }

    // Phương thức chuẩn Mục 4.7.2: ThemTinTuyenDung()
    private void themTinTuyenDung(MTinTuyenDung tin, VTinTuyenDung.TinRequest req) {
        tin.setSTieuDe(TextNormalizer.normalizeText(req.getSTieuDe()));
        tin.setSViTriChucDanh(TextNormalizer.normalizeText(req.getSViTriChucDanh()));
        tin.setFkMaNganhNghe(req.getFkMaNganhNghe());
        tin.setFkMaQuanLamViec(req.getFkMaQuanLamViec().trim());
        tin.setSDiaChiCuThe(TextNormalizer.normalizeText(req.getSDiaChiCuThe()));
        tin.setISoLuongTuyen(req.getISoLuongTuyen());
        tin.setSCapBac(TextNormalizer.normalizeText(req.getSCapBac()));
        tin.setSHinhThucLamViec(TextNormalizer.normalizeText(req.getSHinhThucLamViec()));
        tin.setFLuongToiThieu(req.getFLuongToiThieu());
        tin.setFLuongToiDa(req.getFLuongToiDa());
        tin.setSYeuCauKinhNghiem(TextNormalizer.normalizeText(req.getSYeuCauKinhNghiem()));
        tin.setSYeuCauHocVan(TextNormalizer.normalizeText(req.getSYeuCauHocVan()));
        tin.setSMoTaCongViec(TextNormalizer.normalizeMultiline(req.getSMoTaCongViec()));
        tin.setSCheDoDaiNgo(TextNormalizer.normalizeMultiline(req.getSCheDoDaiNgo()));
        tin.setDHanNopHoSo(req.getDHanNopHoSo());
    }
}
