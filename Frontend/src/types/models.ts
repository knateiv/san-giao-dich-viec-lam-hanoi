export interface ApiResponse<T> {
  thanhCong: boolean;
  thongBao: string;
  duLieu: T;
  thoiGianXuLyMs?: number;
}

export interface QuanHuyen {
  pkMaQuanHuyen: string;
  sTenQuanHuyen: string;
  sKhuVuc: string | null;
  iThuTuHienThi: number | null;
  bDaXoa?: boolean;
}

export interface NganhNghe {
  pkMaNganhNghe: number;
  sTenNganhNghe: string;
  sNhomNganh: string | null;
  bTrangThaiHoatDong: boolean;
  bDaXoa?: boolean;
}

export interface TongQuanHeThong {
  soTinCongKhai: number;
  soDnXacMinh: number;
  soHoSoCongKhai: number;
  soTrungTuyen: number;
}

export interface DanhMucVaTongQuan {
  nganhNghe: NganhNghe[];
  quanHuyen: QuanHuyen[];
  tongQuan: TongQuanHeThong;
}

export interface TaiKhoanView {
  pkMaTaiKhoan: string;
  sEmail: string;
  fkMaVaiTro: number; // 1: NTV, 2: DN, 3: CAN_BO, 4: ADMIN
  sTenVaiTro: string;
  sHoTen: string | null;
  sSDT: string | null;
  sDuongDanAnhDaiDien: string | null;
  bDaKichHoat: boolean;
  bBiKhoa: boolean;
  iSoLanDangNhapSai: number;
  dKhoaDenLuc: string | null;
  sLyDoKhoa: string | null;
  dNgayTao: string;
  duongDanDich?: string;
  token?: string;
}

export interface HoSoRequest {
  sHoTen: string;
  sSoCCCD: string;
  dNgaySinh: string;
  sGioiTinh: string;
  sSDT: string;
  sEmailLienHe: string;
  fkMaQuanCuTru: string;
  sTrinhDoVanHoa: string;
  sTrinhDoChuyenMon: string;
  sKyNangTinHoc: string;
  sTrinhDoNgoaiNgu: string;
  sKinhNghiemLamViec: string;
  sViTriMongMuon: string;
  fMucLuongKyVong: number | null;
  sDiaBanMongMuon: string;
  bDangTimViecCongKhai: boolean;
}

export interface HoSoView extends HoSoRequest {
  pkMaUngVien: string;
  fkMaTaiKhoan: string;
  sTenQuanCuTru: string;
  sDuongDanCV: string | null;
  iTyLeHoanThien: number;
}

export interface PhapLyRequest {
  sTenDoanhNghiep: string;
  sTenVietTat: string;
  sMaSoThue: string;
  sDiaChiTruSo: string;
  fkMaQuanTruSo: string;
  sLinhVucHoatDong: string;
  sNguoiDaiDien: string;
  sTenNguoiLienHe: string;
  sSDTNguoiLienHe: string;
  sEmailNguoiLienHe: string;
}

export interface DoanhNghiepView extends PhapLyRequest {
  pkMaDoanhNghiep: string;
  fkMaTaiKhoan: string;
  sDuongDanGiayPhep: string | null;
  sTenQuanTruSo: string;
  iTrangThaiThamDinh: number; // 0: Chờ thẩm định, 1: Đã xác minh, 2: Yêu cầu bổ sung
  sGhiChuThamDinh: string | null;
}

export interface TinRequest {
  sTieuDe: string;
  sViTriChucDanh: string;
  fkMaNganhNghe: number;
  fkMaQuanLamViec: string;
  sDiaChiCuThe: string;
  iSoLuongTuyen: number;
  sCapBac: string;
  sHinhThucLamViec: string;
  fLuongToiThieu: number;
  fLuongToiDa: number;
  sYeuCauKinhNghiem: string;
  sYeuCauHocVan: string;
  sMoTaCongViec: string;
  sCheDoDaiNgo: string;
  dHanNopHoSo: string;
}

export interface TinTuyenDungView extends TinRequest {
  pkMaTin: string;
  fkMaDoanhNghiep: string;
  sTenDoanhNghiep: string;
  sTenNganhNghe: string;
  sTenQuanLamViec: string;
  iTrangThaiDuyet: number; // 0: Chờ duyệt, 1: Đã duyệt, 2: Từ chối, 3: Đã gỡ vi phạm
  tenTrangThaiDuyet: string;
  fkMaCanBoDuyet: string | null;
  dNgayDuyet: string | null;
  sLyDoTuChoi: string | null;
  bDaDong: boolean;
  soHoSoUngTuyen: number;
  daNop?: boolean;
}

export interface TimViecPage {
  items: TinTuyenDungView[];
  totalItems: number;
  page: number;
  size: number;
  totalPages: number;
}

export interface UngVienCongKhaiView {
  pkMaUngVien: string;
  sHoTen: string;
  sGioiTinh: string;
  iDoTuoi: number;
  fkMaQuanCuTru: string;
  sTenQuanCuTru: string;
  sTrinhDoVanHoa: string | null;
  sTrinhDoChuyenMon: string | null;
  sKyNangTinHoc: string | null;
  sTrinhDoNgoaiNgu: string | null;
  sKinhNghiemLamViec: string | null;
  sViTriMongMuon: string | null;
  fMucLuongKyVong: number | null;
  sDiaBanMongMuon: string | null;
  iTyLeHoanThien: number;
  bCoTepCV: boolean;
  sSoCCCDChe: string;
  sSDTChe: string;
  sEmailChe: string;
}

export interface LichSuUngTuyenView {
  pkMaLichSu: number;
  iTrangThaiCu: number | null;
  iTrangThaiMoi: number;
  tenTrangThaiMoi: string;
  fkMaNguoiThucHien: string;
  sGhiChu: string | null;
  dThoiGianThayDoi: string;
}

export interface UngTuyenView {
  pkMaUngTuyen: string;
  fkMaTin: string;
  sTieuDeTin: string;
  sViTriChucDanh: string;
  sTenDoanhNghiep: string;
  fkMaUngVien: string;
  sHoTenUngVien: string;
  sSDTUngVien: string;
  sEmailUngVien: string;
  sTrinhDoChuyenMon: string | null;
  sKyNangTinHoc: string | null;
  sKinhNghiemLamViec: string | null;
  sDuongDanCV: string | null;
  dNgayNop: string;
  iTrangThaiUngTuyen: number; // 0..5
  tenTrangThaiUngTuyen: string;
  dThoiGianPhongVan: string | null;
  sDiaDiemPhongVan: string | null;
  sHinhThucPhongVan: string | null;
  sGhiChuPhongVan: string | null;
  lichSu?: LichSuUngTuyenView[];
}

export interface DongTheoQuan {
  pkMaQuanHuyen: string;
  sTenQuanHuyen: string;
  sKhuVuc: string;
  iTongCung: number;
  iTongCau: number;
  iChenhLech: number;
  iSoHoSoNop: number;
  iSoTrungTuyen: number;
}

export interface DongTheoNganh {
  pkMaNganhNghe: number;
  sTenNganhNghe: string;
  sNhomNganh: string;
  iSoTinTuyenDung: number;
  iTongCauChiTieu: number;
  iTongCungHoSo: number;
  iTongLuotUngTuyen: number;
  iSoTrungTuyen: number;
}

export interface TongHopBaoCaoView {
  tongCung: number;
  tongCau: number;
  tongHoSoUngTuyen: number;
  tongTrungTuyen: number;
  tongDoanhNghiepDaXacMinh: number;
  tongTinCongKhai: number;
  theoQuanHuyen: DongTheoQuan[];
  theoNganhNghe: DongTheoNganh[];
}

export interface NhatKyHeThongView {
  pkMaNhatKy: number;
  fkMaTaiKhoan: string | null;
  tenNguoiThucHien: string;
  sEmail: string;
  sLoaiHanhDong: string;
  sMucDo: string;
  sDiaChiIP: string | null;
  sTrinhDuyet: string | null;
  dThoiGianGhi: string;
  sDuLieuCu: string | null;
  sDuLieuMoi: string | null;
}
