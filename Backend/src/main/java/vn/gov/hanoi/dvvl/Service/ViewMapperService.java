package vn.gov.hanoi.dvvl.Service;

import java.util.List;
import org.springframework.stereotype.Service;
import vn.gov.hanoi.dvvl.Dto.VTinTuyenDung;
import vn.gov.hanoi.dvvl.Dto.VUngTuyen;
import vn.gov.hanoi.dvvl.Model.MDoanhNghiep;
import vn.gov.hanoi.dvvl.Model.MHoSoNguoiTimViec;
import vn.gov.hanoi.dvvl.Model.MNganhNghe;
import vn.gov.hanoi.dvvl.Model.MQuanHuyen;
import vn.gov.hanoi.dvvl.Model.MTinTuyenDung;
import vn.gov.hanoi.dvvl.Model.MUngTuyen;
import vn.gov.hanoi.dvvl.Repository.DoanhNghiepRepository;
import vn.gov.hanoi.dvvl.Repository.HoSoNguoiTimViecRepository;
import vn.gov.hanoi.dvvl.Repository.LichSuUngTuyenRepository;
import vn.gov.hanoi.dvvl.Repository.NganhNgheRepository;
import vn.gov.hanoi.dvvl.Repository.QuanHuyenRepository;
import vn.gov.hanoi.dvvl.Repository.TinTuyenDungRepository;
import vn.gov.hanoi.dvvl.Repository.UngTuyenRepository;

@Service
public class ViewMapperService {

    private final DoanhNghiepRepository dnRepo;
    private final NganhNgheRepository nnRepo;
    private final QuanHuyenRepository qhRepo;
    private final TinTuyenDungRepository tinRepo;
    private final HoSoNguoiTimViecRepository hoSoRepo;
    private final UngTuyenRepository ungTuyenRepo;
    private final LichSuUngTuyenRepository lichSuRepo;

    public ViewMapperService(
            DoanhNghiepRepository dnRepo,
            NganhNgheRepository nnRepo,
            QuanHuyenRepository qhRepo,
            TinTuyenDungRepository tinRepo,
            HoSoNguoiTimViecRepository hoSoRepo,
            UngTuyenRepository ungTuyenRepo,
            LichSuUngTuyenRepository lichSuRepo
    ) {
        this.dnRepo = dnRepo;
        this.nnRepo = nnRepo;
        this.qhRepo = qhRepo;
        this.tinRepo = tinRepo;
        this.hoSoRepo = hoSoRepo;
        this.ungTuyenRepo = ungTuyenRepo;
        this.lichSuRepo = lichSuRepo;
    }

    public static String tenTrangThaiDuyet(int code, boolean daDong) {
        if (daDong) return "Đã đóng tin";
        return switch (code) {
            case 1 -> "Đã duyệt";
            case 2 -> "Từ chối";
            case 3 -> "Bị gỡ khẩn cấp";
            default -> "Chờ duyệt";
        };
    }

    public static String tenTrangThaiUngTuyen(int code) {
        return switch (code) {
            case 0 -> "Chờ duyệt";
            case 1 -> "Đã tiếp nhận / Đã xem";
            case 2 -> "Hẹn phỏng vấn";
            case 3 -> "Trúng tuyển";
            case 4 -> "Từ chối";
            case 5 -> "Đã hủy";
            default -> "Không xác định";
        };
    }

    public VTinTuyenDung.TinTuyenDungView toTinView(MTinTuyenDung t, String currentCandidateId) {
        VTinTuyenDung.TinTuyenDungView v = new VTinTuyenDung.TinTuyenDungView();
        v.setPkMaTin(t.getPkMaTin());
        v.setFkMaDoanhNghiep(t.getFkMaDoanhNghiep());
        v.setSTenDoanhNghiep(dnRepo.findActiveById(t.getFkMaDoanhNghiep())
                .map(MDoanhNghiep::getSTenDoanhNghiep).orElse(""));
        v.setSTieuDe(t.getSTieuDe());
        v.setSViTriChucDanh(t.getSViTriChucDanh());
        v.setFkMaNganhNghe(t.getFkMaNganhNghe());
        v.setSTenNganhNghe(nnRepo.findActiveById(t.getFkMaNganhNghe())
                .map(MNganhNghe::getSTenNganhNghe).orElse(""));
        v.setFkMaQuanLamViec(t.getFkMaQuanLamViec());
        v.setSTenQuanLamViec(qhRepo.findActiveById(t.getFkMaQuanLamViec())
                .map(MQuanHuyen::getSTenQuanHuyen).orElse(""));
        v.setSDiaChiCuThe(t.getSDiaChiCuThe());
        v.setISoLuongTuyen(t.getISoLuongTuyen());
        v.setSCapBac(t.getSCapBac());
        v.setSHinhThucLamViec(t.getSHinhThucLamViec());
        v.setFLuongToiThieu(t.getFLuongToiThieu());
        v.setFLuongToiDa(t.getFLuongToiDa());
        v.setSYeuCauKinhNghiem(t.getSYeuCauKinhNghiem());
        v.setSYeuCauHocVan(t.getSYeuCauHocVan());
        v.setSMoTaCongViec(t.getSMoTaCongViec());
        v.setSCheDoDaiNgo(t.getSCheDoDaiNgo());
        v.setDHanNopHoSo(t.getDHanNopHoSo());
        v.setITrangThaiDuyet(t.getITrangThaiDuyet());
        v.setTenTrangThaiDuyet(tenTrangThaiDuyet(t.getITrangThaiDuyet(), Boolean.TRUE.equals(t.getBDaDong())));
        v.setFkMaCanBoDuyet(t.getFkMaCanBoDuyet());
        v.setDNgayDuyet(t.getDNgayDuyet());
        v.setSLyDoTuChoi(t.getSLyDoTuChoi());
        v.setBDaDong(t.getBDaDong());
        v.setSoHoSoUngTuyen((long) ungTuyenRepo.findByJob(t.getPkMaTin()).size());
        if (currentCandidateId != null) {
            v.setDaNop(ungTuyenRepo.existsApplication(t.getPkMaTin(), currentCandidateId));
        } else {
            v.setDaNop(false);
        }
        return v;
    }

    public VUngTuyen.UngTuyenView toUngTuyenView(MUngTuyen u, boolean includeHistory) {
        VUngTuyen.UngTuyenView v = new VUngTuyen.UngTuyenView();
        v.setPkMaUngTuyen(u.getPkMaUngTuyen());
        v.setFkMaTin(u.getFkMaTin());
        MTinTuyenDung tin = tinRepo.findById(u.getFkMaTin()).orElse(null);
        if (tin != null) {
            v.setSTieuDeTin(tin.getSTieuDe());
            v.setSViTriChucDanh(tin.getSViTriChucDanh());
            v.setSTenDoanhNghiep(dnRepo.findActiveById(tin.getFkMaDoanhNghiep())
                    .map(MDoanhNghiep::getSTenDoanhNghiep).orElse(""));
        }
        v.setFkMaUngVien(u.getFkMaUngVien());
        MHoSoNguoiTimViec hs = hoSoRepo.findActiveById(u.getFkMaUngVien()).orElse(null);
        if (hs != null) {
            v.setSHoTenUngVien(hs.getSHoTen());
            v.setSSDTUngVien(hs.getSSDT());
            v.setSEmailUngVien(hs.getSEmailLienHe());
            v.setSTrinhDoChuyenMon(hs.getSTrinhDoChuyenMon());
            v.setSKyNangTinHoc(hs.getSKyNangTinHoc());
            v.setSKinhNghiemLamViec(hs.getSKinhNghiemLamViec());
            v.setSDuongDanCV(hs.getSDuongDanCV());
        }
        v.setDNgayNop(u.getDNgayNop());
        v.setITrangThaiUngTuyen(u.getITrangThaiUngTuyen());
        v.setTenTrangThaiUngTuyen(tenTrangThaiUngTuyen(u.getITrangThaiUngTuyen()));
        v.setDThoiGianPhongVan(u.getDThoiGianPhongVan());
        v.setSDiaDiemPhongVan(u.getSDiaDiemPhongVan());
        v.setSHinhThucPhongVan(u.getSHinhThucPhongVan());
        v.setSGhiChuPhongVan(u.getSGhiChuPhongVan());

        if (includeHistory) {
            List<VUngTuyen.LichSuView> ls = lichSuRepo.findByUngTuyenOrdered(u.getPkMaUngTuyen()).stream().map(item -> {
                VUngTuyen.LichSuView lv = new VUngTuyen.LichSuView();
                lv.setPkMaLichSu(item.getPkMaLichSu());
                lv.setITrangThaiCu(item.getITrangThaiCu());
                lv.setITrangThaiMoi(item.getITrangThaiMoi());
                lv.setTenTrangThaiMoi(tenTrangThaiUngTuyen(item.getITrangThaiMoi()));
                lv.setFkMaNguoiThucHien(item.getFkMaNguoiThucHien());
                lv.setSGhiChu(item.getSGhiChu());
                lv.setDThoiGianThayDoi(item.getDThoiGianThayDoi());
                return lv;
            }).toList();
            v.setLichSu(ls);
        }
        return v;
    }
}
