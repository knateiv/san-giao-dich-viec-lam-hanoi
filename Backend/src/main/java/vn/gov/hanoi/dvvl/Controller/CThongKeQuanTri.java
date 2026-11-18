package vn.gov.hanoi.dvvl.Controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.gov.hanoi.dvvl.Config.UserPrincipal;
import vn.gov.hanoi.dvvl.Dto.ApiResponse;
import vn.gov.hanoi.dvvl.Dto.VBaoCaoCungCau;
import vn.gov.hanoi.dvvl.Dto.VNhatKyHeThong;
import vn.gov.hanoi.dvvl.Model.MNganhNghe;
import vn.gov.hanoi.dvvl.Model.MQuanHuyen;
import vn.gov.hanoi.dvvl.Model.MTaiKhoan;
import vn.gov.hanoi.dvvl.Repository.NganhNgheRepository;
import vn.gov.hanoi.dvvl.Repository.NhatKyHeThongRepository;
import vn.gov.hanoi.dvvl.Repository.QuanHuyenRepository;
import vn.gov.hanoi.dvvl.Repository.TaiKhoanRepository;
import vn.gov.hanoi.dvvl.Service.AuditLogService;
import vn.gov.hanoi.dvvl.Service.BusinessException;
import vn.gov.hanoi.dvvl.Service.ExcelExportService;
import vn.gov.hanoi.dvvl.Service.PdfExportService;
import vn.gov.hanoi.dvvl.Service.TextNormalizer;
import vn.gov.hanoi.dvvl.Service.ThongKeService;

/**
 * Controller Phân hệ 6 — Thống kê Cung-Cầu (R24, R25), Quản trị Danh mục (R26, R27) & Audit Log (R28)
 */
@RestController
public class CThongKeQuanTri {

    private final ThongKeService thongKeSvc;
    private final PdfExportService pdfSvc;
    private final ExcelExportService excelSvc;
    private final NganhNgheRepository nnRepo;
    private final QuanHuyenRepository qhRepo;
    private final NhatKyHeThongRepository nkRepo;
    private final TaiKhoanRepository tkRepo;
    private final AuditLogService audit;

    public CThongKeQuanTri(
            ThongKeService thongKeSvc,
            PdfExportService pdfSvc,
            ExcelExportService excelSvc,
            NganhNgheRepository nnRepo,
            QuanHuyenRepository qhRepo,
            NhatKyHeThongRepository nkRepo,
            TaiKhoanRepository tkRepo,
            AuditLogService audit
    ) {
        this.thongKeSvc = thongKeSvc;
        this.pdfSvc = pdfSvc;
        this.excelSvc = excelSvc;
        this.nnRepo = nnRepo;
        this.qhRepo = qhRepo;
        this.nkRepo = nkRepo;
        this.tkRepo = tkRepo;
        this.audit = audit;
    }

    // R24: Thống kê số liệu cung - cầu lao động
    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @GetMapping("/api/thong-ke/cung-cau")
    public ApiResponse<VBaoCaoCungCau.TongHopView> thongKeCungCau(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) Integer maNganhNghe,
            @RequestParam(required = false) String maQuanHuyen
    ) {
        return ApiResponse.ok(
                "Số liệu thống kê cung - cầu lao động Hà Nội.",
                thongKeSvc.layBaoCaoCungCau(tuNgay, denNgay, maNganhNghe, maQuanHuyen));
    }

    // R25: Kết xuất Báo cáo định kỳ Sở LĐ-TB&XH (PDF)
    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @GetMapping("/api/thong-ke/xuat-pdf")
    public ResponseEntity<byte[]> xuatBaoCaoPdf(
            @AuthenticationPrincipal UserPrincipal actor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) Integer maNganhNghe,
            @RequestParam(required = false) String maQuanHuyen,
            HttpServletRequest httpReq
    ) {
        var data = thongKeSvc.layBaoCaoCungCau(tuNgay, denNgay, maNganhNghe, maQuanHuyen);
        byte[] pdf = pdfSvc.xuatBaoCaoPdf(
                data,
                tuNgay != null ? tuNgay.toString() : "",
                denNgay != null ? denNgay.toString() : "",
                actor.getSHoTen());
        audit.ghiNhatKy(actor.getPkMaTaiKhoan(), "XUAT_BAO_CAO_PDF", "INFO", null,
                "{\"tuNgay\":\"" + tuNgay + "\",\"denNgay\":\"" + denNgay + "\"}", httpReq);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Bao_Cao_Cung_Cau_HaNoi.pdf\"")
                .body(pdf);
    }

    // R25: Kết xuất Báo cáo định kỳ Sở LĐ-TB&XH (Excel .xlsx)
    @PreAuthorize("hasAnyRole('CANBO', 'ADMIN')")
    @GetMapping("/api/thong-ke/xuat-excel")
    public ResponseEntity<byte[]> xuatBaoCaoExcel(
            @AuthenticationPrincipal UserPrincipal actor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) Integer maNganhNghe,
            @RequestParam(required = false) String maQuanHuyen,
            HttpServletRequest httpReq
    ) {
        var data = thongKeSvc.layBaoCaoCungCau(tuNgay, denNgay, maNganhNghe, maQuanHuyen);
        byte[] xlsx = excelSvc.xuatBaoCaoExcel(
                data,
                tuNgay != null ? tuNgay.toString() : "",
                denNgay != null ? denNgay.toString() : "");
        audit.ghiNhatKy(actor.getPkMaTaiKhoan(), "XUAT_BAO_CAO_EXCEL", "INFO", null,
                "{\"tuNgay\":\"" + tuNgay + "\",\"denNgay\":\"" + denNgay + "\"}", httpReq);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Bao_Cao_Cung_Cau_HaNoi.xlsx\"")
                .body(xlsx);
    }

    // R26: Quản lý danh mục ngành nghề chuẩn (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/quan-tri/nganh-nghe")
    public ApiResponse<List<MNganhNghe>> danhSachNganhNghe() {
        return ApiResponse.ok("Danh mục ngành nghề chuẩn.", nnRepo.findAllActive());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/quan-tri/nganh-nghe")
    @Transactional
    public ApiResponse<MNganhNghe> themNganhNghe(
            @AuthenticationPrincipal UserPrincipal admin,
            @RequestBody MNganhNghe body,
            HttpServletRequest httpReq
    ) {
        String ten = TextNormalizer.normalizeText(body.getSTenNganhNghe());
        if (ten == null || ten.isBlank()) {
            throw new BusinessException("Vui lòng nhập Tên ngành nghề.");
        }
        MNganhNghe item = new MNganhNghe();
        item.setPkMaNganhNghe(nnRepo.findMaxId() + 1);
        item.setSTenNganhNghe(ten);
        item.setSNhomNganh(TextNormalizer.normalizeText(body.getSNhomNganh()));
        item.setBTrangThaiHoatDong(body.getBTrangThaiHoatDong() == null || body.getBTrangThaiHoatDong());
        item.setBDaXoa(false);
        nnRepo.save(item);
        audit.ghiNhatKy(admin.getPkMaTaiKhoan(), "THEM_NGANH_NGHE", "INFO", null,
                "{\"PK_MaNganhNghe\":" + item.getPkMaNganhNghe() + ",\"sTen\":\"" + ten + "\"}", httpReq);
        return ApiResponse.ok("Đã thêm ngành nghề mới.", item);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/quan-tri/nganh-nghe/{id}")
    @Transactional
    public ApiResponse<MNganhNghe> suaNganhNghe(
            @AuthenticationPrincipal UserPrincipal admin,
            @PathVariable Integer id,
            @RequestBody MNganhNghe body,
            HttpServletRequest httpReq
    ) {
        MNganhNghe item = nnRepo.findActiveById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy ngành nghề.", HttpStatus.NOT_FOUND));
        if (body.getSTenNganhNghe() != null && !body.getSTenNganhNghe().isBlank()) {
            item.setSTenNganhNghe(TextNormalizer.normalizeText(body.getSTenNganhNghe()));
        }
        if (body.getSNhomNganh() != null) {
            item.setSNhomNganh(TextNormalizer.normalizeText(body.getSNhomNganh()));
        }
        if (body.getBTrangThaiHoatDong() != null) {
            item.setBTrangThaiHoatDong(body.getBTrangThaiHoatDong());
        }
        nnRepo.save(item);
        audit.ghiNhatKy(admin.getPkMaTaiKhoan(), "CAP_NHAT_NGANH_NGHE", "INFO", null,
                "{\"PK_MaNganhNghe\":" + id + "}", httpReq);
        return ApiResponse.ok("Cập nhật ngành nghề thành công.", item);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/quan-tri/nganh-nghe/{id}")
    @Transactional
    public ApiResponse<Void> xoaMemNganhNghe(
            @AuthenticationPrincipal UserPrincipal admin,
            @PathVariable Integer id,
            HttpServletRequest httpReq
    ) {
        MNganhNghe item = nnRepo.findActiveById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy ngành nghề.", HttpStatus.NOT_FOUND));
        long refs = nnRepo.countJobReferences(id);
        if (refs > 0) {
            item.setBTrangThaiHoatDong(false);
            nnRepo.save(item);
            audit.ghiNhatKy(admin.getPkMaTaiKhoan(), "TAM_NGUNG_NGANH_NGHE", "INFO", null,
                    "{\"PK_MaNganhNghe\":" + id + ",\"refs\":" + refs + "}", httpReq);
            return ApiResponse.ok("Ngành nghề đang gắn với " + refs + " tin tuyển dụng nên đã chuyển sang trạng thái Ngưng áp dụng.", null);
        }
        item.setBDaXoa(true);
        nnRepo.save(item);
        audit.ghiNhatKy(admin.getPkMaTaiKhoan(), "XOA_MEM_NGANH_NGHE", "INFO", null, "{\"PK_MaNganhNghe\":" + id + "}", httpReq);
        return ApiResponse.ok("Đã xóa mềm ngành nghề thành công.", null);
    }

    // R27: Quản lý danh mục 30 Quận / Huyện Hà Nội (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/quan-tri/quan-huyen")
    public ApiResponse<List<MQuanHuyen>> danhSachQuanHuyen() {
        return ApiResponse.ok("Danh mục địa bàn quận/huyện TP. Hà Nội.", qhRepo.findAllActiveOrdered());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/quan-tri/quan-huyen")
    @Transactional
    public ApiResponse<MQuanHuyen> themQuanHuyen(
            @AuthenticationPrincipal UserPrincipal admin,
            @RequestBody MQuanHuyen body,
            HttpServletRequest httpReq
    ) {
        String ma = TextNormalizer.normalizeText(body.getPkMaQuanHuyen());
        if (ma == null || !ma.matches("^QH\\d{3}$")) {
            throw new BusinessException("Mã quận/huyện phải đúng định dạng 5 ký tự (ví dụ: QH031).");
        }
        if (qhRepo.findById(ma).isPresent()) {
            throw new BusinessException("Mã quận/huyện " + ma + " đã tồn tại.");
        }
        MQuanHuyen q = new MQuanHuyen();
        q.setPkMaQuanHuyen(ma);
        q.setSTenQuanHuyen(TextNormalizer.normalizeText(body.getSTenQuanHuyen()));
        q.setSKhuVuc(body.getSKhuVuc() != null ? body.getSKhuVuc().trim() : "Ngoại thành");
        q.setIThuTuHienThi(body.getIThuTuHienThi() != null ? body.getIThuTuHienThi() : 31);
        q.setBDaXoa(false);
        qhRepo.save(q);
        audit.ghiNhatKy(admin.getPkMaTaiKhoan(), "THEM_QUAN_HUYEN", "INFO", null, "{\"PK_MaQuanHuyen\":\"" + ma + "\"}", httpReq);
        return ApiResponse.ok("Đã thêm đơn vị hành chính mới.", q);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/quan-tri/quan-huyen/{ma}")
    @Transactional
    public ApiResponse<MQuanHuyen> suaQuanHuyen(
            @AuthenticationPrincipal UserPrincipal admin,
            @PathVariable String ma,
            @RequestBody MQuanHuyen body,
            HttpServletRequest httpReq
    ) {
        MQuanHuyen q = qhRepo.findActiveById(ma.trim())
                .orElseThrow(() -> new BusinessException("Không tìm thấy quận/huyện " + ma, HttpStatus.NOT_FOUND));
        if (body.getSTenQuanHuyen() != null && !body.getSTenQuanHuyen().isBlank()) {
            q.setSTenQuanHuyen(TextNormalizer.normalizeText(body.getSTenQuanHuyen()));
        }
        if (body.getSKhuVuc() != null && (body.getSKhuVuc().equals("Nội thành") || body.getSKhuVuc().equals("Ngoại thành"))) {
            q.setSKhuVuc(body.getSKhuVuc());
        }
        if (body.getIThuTuHienThi() != null) {
            q.setIThuTuHienThi(body.getIThuTuHienThi());
        }
        qhRepo.save(q);
        audit.ghiNhatKy(admin.getPkMaTaiKhoan(), "CAP_NHAT_QUAN_HUYEN", "INFO", null, "{\"PK_MaQuanHuyen\":\"" + ma + "\"}", httpReq);
        return ApiResponse.ok("Cập nhật địa bàn quận/huyện thành công.", q);
    }

    // R27 & Mục 4.6.6: Chặn xóa cứng khi đang có tham chiếu hoặc chuyển sang ẩn mềm bDaXoa = 1
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/quan-tri/quan-huyen/{ma}")
    @Transactional
    public ApiResponse<Void> xoaQuanHuyen(
            @AuthenticationPrincipal UserPrincipal admin,
            @PathVariable String ma,
            HttpServletRequest httpReq
    ) {
        String id = ma.trim();
        MQuanHuyen q = qhRepo.findActiveById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy quận/huyện " + id, HttpStatus.NOT_FOUND));
        long refs = qhRepo.countCandidateReferences(id) + qhRepo.countJobReferences(id) + qhRepo.countEmployerReferences(id);
        if (refs > 0) {
            throw new BusinessException("Quy tắc toàn vẹn R27: Không được phép xóa địa bàn \"" + q.getSTenQuanHuyen()
                    + "\" vì đang gắn với " + refs + " hồ sơ/doanh nghiệp/tin tuyển dụng.");
        }
        q.setBDaXoa(true);
        qhRepo.save(q);
        audit.ghiNhatKy(admin.getPkMaTaiKhoan(), "XOA_MEM_QUAN_HUYEN", "INFO", null, "{\"PK_MaQuanHuyen\":\"" + id + "\"}", httpReq);
        return ApiResponse.ok("Đã ẩn (xóa mềm) địa bàn " + q.getSTenQuanHuyen(), null);
    }

    // R28: Giám sát & tra cứu Nhật ký hệ thống Append-Only (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/quan-tri/nhat-ky")
    public ApiResponse<List<VNhatKyHeThong>> traCuuNhatKy(
            @RequestParam(required = false) String mucDo,
            @RequestParam(required = false) String tuKhoa
    ) {
        return ApiResponse.ok("Danh sách nhật ký hệ thống (Audit Log Append-Only).", locNhatKy(mucDo, tuKhoa));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/quan-tri/nhat-ky/xuat-excel")
    public ResponseEntity<byte[]> xuatNhatKyExcel(
            @RequestParam(required = false) String mucDo,
            @RequestParam(required = false) String tuKhoa
    ) {
        byte[] xlsx = excelSvc.xuatNhatKyExcel(locNhatKy(mucDo, tuKhoa));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Audit_Log_DVVL_HaNoi.xlsx\"")
                .body(xlsx);
    }

    private List<VNhatKyHeThong> locNhatKy(String mucDo, String tuKhoa) {
        Map<String, MTaiKhoan> userMap = tkRepo.findAll().stream()
                .collect(Collectors.toMap(MTaiKhoan::getPkMaTaiKhoan, t -> t, (a, b) -> a));
        String md = mucDo != null ? mucDo.trim().toUpperCase() : "";
        String kw = tuKhoa != null ? tuKhoa.trim().toLowerCase() : "";

        return nkRepo.findAllOrderedDesc().stream()
                .filter(n -> md.isEmpty() || md.equals(n.getSMucDo()))
                .map(n -> {
                    VNhatKyHeThong v = new VNhatKyHeThong();
                    v.setPkMaNhatKy(n.getPkMaNhatKy());
                    v.setFkMaTaiKhoan(n.getFkMaTaiKhoan());
                    MTaiKhoan actor = n.getFkMaTaiKhoan() != null ? userMap.get(n.getFkMaTaiKhoan()) : null;
                    v.setTenNguoiThucHien(actor != null ? actor.getSHoTen() : "Khách / Hệ thống");
                    v.setSEmail(actor != null ? actor.getSEmail() : "");
                    v.setSLoaiHanhDong(n.getSLoaiHanhDong());
                    v.setSMucDo(n.getSMucDo());
                    v.setSDiaChiIP(n.getSDiaChiIP());
                    v.setSTrinhDuyet(n.getSTrinhDuyet());
                    v.setDThoiGianGhi(n.getDThoiGianGhi());
                    v.setSDuLieuCu(n.getSDuLieuCu());
                    v.setSDuLieuMoi(n.getSDuLieuMoi());
                    return v;
                })
                .filter(v -> kw.isEmpty()
                        || (v.getSLoaiHanhDong() != null && v.getSLoaiHanhDong().toLowerCase().contains(kw))
                        || (v.getTenNguoiThucHien() != null && v.getTenNguoiThucHien().toLowerCase().contains(kw))
                        || (v.getSEmail() != null && v.getSEmail().toLowerCase().contains(kw))
                        || (v.getSDiaChiIP() != null && v.getSDiaChiIP().toLowerCase().contains(kw)))
                .toList();
    }
}
