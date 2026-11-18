"use client";

import React, { useEffect, useState } from "react";
import { Download, Eye, EyeOff, FileText, Save, Upload } from "lucide-react";
import { apiFetch, apiUpload, downloadBinary } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { DanhMucVaTongQuan, HoSoRequest, HoSoView, QuanHuyen } from "@/types/models";

const EMPTY_FORM: HoSoRequest = {
  sHoTen: "",
  sSoCCCD: "",
  dNgaySinh: "",
  sGioiTinh: "Nam",
  sSDT: "",
  sEmailLienHe: "",
  fkMaQuanCuTru: "QH001",
  sTrinhDoVanHoa: "12/12",
  sTrinhDoChuyenMon: "",
  sKyNangTinHoc: "",
  sTrinhDoNgoaiNgu: "",
  sKinhNghiemLamViec: "",
  sViTriMongMuon: "",
  fMucLuongKyVong: null,
  sDiaBanMongMuon: "",
  bDangTimViecCongKhai: true,
};

function HoSoUngVienContent() {
  const [hoSo, setHoSo] = useState<HoSoView | null>(null);
  const [form, setForm] = useState<HoSoRequest>(EMPTY_FORM);
  const [quanList, setQuanList] = useState<QuanHuyen[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [uploadingCv, setUploadingCv] = useState(false);
  const [exportingPdf, setExportingPdf] = useState(false);
  const [togglingPublic, setTogglingPublic] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [dmRes, hsRes] = await Promise.all([
        apiFetch<DanhMucVaTongQuan>("/api/tim-viec/danh-muc"),
        apiFetch<HoSoView | null>("/api/nguoi-tim-viec/ho-so"),
      ]);
      setQuanList(dmRes.duLieu.quanHuyen || []);
      const v = hsRes.duLieu;
      setHoSo(v);
      if (v) {
        setForm({
          sHoTen: v.sHoTen || "",
          sSoCCCD: v.sSoCCCD || "",
          dNgaySinh: v.dNgaySinh || "",
          sGioiTinh: v.sGioiTinh || "Nam",
          sSDT: v.sSDT || "",
          sEmailLienHe: v.sEmailLienHe || "",
          fkMaQuanCuTru: v.fkMaQuanCuTru || "QH001",
          sTrinhDoVanHoa: v.sTrinhDoVanHoa || "12/12",
          sTrinhDoChuyenMon: v.sTrinhDoChuyenMon || "",
          sKyNangTinHoc: v.sKyNangTinHoc || "",
          sTrinhDoNgoaiNgu: v.sTrinhDoNgoaiNgu || "",
          sKinhNghiemLamViec: v.sKinhNghiemLamViec || "",
          sViTriMongMuon: v.sViTriMongMuon || "",
          fMucLuongKyVong: v.fMucLuongKyVong ?? null,
          sDiaBanMongMuon: v.sDiaBanMongMuon || "",
          bDangTimViecCongKhai: v.bDangTimViecCongKhai ?? true,
        });
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không thể tải hồ sơ người tìm việc.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadData();
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSaving(true);
    try {
      const res = await apiFetch<HoSoView>("/api/nguoi-tim-viec/ho-so", {
        method: "PUT",
        body: JSON.stringify(form),
      });
      setHoSo(res.duLieu);
      setSuccess(res.thongBao);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Lưu hồ sơ thất bại.");
    } finally {
      setSaving(false);
    }
  };

  const handleTogglePublic = async () => {
    if (!hoSo) return;
    setError(null);
    setSuccess(null);
    setTogglingPublic(true);
    try {
      const nextVal = !hoSo.bDangTimViecCongKhai;
      const res = await apiFetch<HoSoView>("/api/nguoi-tim-viec/trang-thai-tim-viec", {
        method: "PATCH",
        body: JSON.stringify({ bDangTimViecCongKhai: nextVal }),
      });
      setHoSo(res.duLieu);
      setForm((prev) => ({ ...prev, bDangTimViecCongKhai: res.duLieu.bDangTimViecCongKhai }));
      setSuccess(res.thongBao);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Đổi trạng thái công khai thất bại.");
    } finally {
      setTogglingPublic(false);
    }
  };

  const handleUploadCv = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setError(null);
    setSuccess(null);
    setUploadingCv(true);
    try {
      const fd = new FormData();
      fd.append("file", file);
      const res = await apiUpload<HoSoView>("/api/nguoi-tim-viec/upload-cv", fd, "POST");
      setHoSo(res.duLieu);
      setSuccess(res.thongBao);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Tải tệp CV thất bại.");
    } finally {
      setUploadingCv(false);
      e.target.value = "";
    }
  };

  const handleExportPdf = async () => {
    if (!hoSo) return;
    setError(null);
    setExportingPdf(true);
    try {
      await downloadBinary(
        `/api/nguoi-tim-viec/phieu-dang-ky-pdf/${encodeURIComponent(hoSo.pkMaUngVien)}`,
        `Phieu_Dang_Ky_Tim_Viec_${hoSo.pkMaUngVien}.pdf`
      );
      setSuccess("Đã xuất tệp PDF Phiếu đăng ký tìm việc làm chuẩn mẫu A4 kèm mã QR.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Xuất PDF thất bại.");
    } finally {
      setExportingPdf(false);
    }
  };

  if (loading) {
    return <div className="civic-card p-6 text-sm text-[#5F6C7B]">Đang tải hồ sơ người tìm việc...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[#192536]">Hồ sơ Người tìm việc &amp; CV PDF</h1>
          <p className="text-sm text-[#5F6C7B] mt-1">
            Quản lý sơ yếu lý lịch, trình độ, mức độ hoàn thiện hồ sơ và kết xuất Phiếu đăng ký tìm việc mẫu A4 có mã QR.
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          {hoSo && (
            <>
              <button
                type="button"
                id="btnCongKhaiHoSo"
                disabled={togglingPublic}
                onClick={handleTogglePublic}
                className="civic-btn-secondary"
              >
                {hoSo.bDangTimViecCongKhai ? <Eye className="w-4 h-4 text-[#15803D]" /> : <EyeOff className="w-4 h-4" />}
                {hoSo.bDangTimViecCongKhai ? "Đang bật: Công khai tìm việc" : "Đang tắt: Ẩn khỏi NTD"}
              </button>
              <button
                type="button"
                id="btnXuatPhieuPdf"
                disabled={exportingPdf}
                onClick={handleExportPdf}
                className="civic-btn-primary"
              >
                <Download className="w-4 h-4" />
                {exportingPdf ? "Đang tạo PDF..." : "Xuất Phiếu ĐK (PDF + QR)"}
              </button>
            </>
          )}
        </div>
      </div>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <section className="civic-card p-5 grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
        <div>
          <div className="text-xs font-semibold text-[#5F6C7B]">Mã ứng viên định danh</div>
          <div className="font-mono font-bold text-[#192536] mt-0.5">
            {hoSo ? hoSo.pkMaUngVien : "Chưa khởi tạo (Lưu hồ sơ để cấp mã)"}
          </div>
        </div>
        <div>
          <div className="flex items-center justify-between text-xs font-semibold text-[#5F6C7B] mb-1">
            <span>Tỷ lệ hoàn thiện hồ sơ</span>
            <span className="tabular-nums text-[#215DA8]">{hoSo?.iTyLeHoanThien ?? 0}%</span>
          </div>
          <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden border border-[#DCE2E9]">
            <div
              className="h-full bg-[#215DA8]"
              style={{ width: `${hoSo?.iTyLeHoanThien ?? 0}%` }}
            />
          </div>
        </div>
        <div className="flex flex-wrap items-center justify-start md:justify-end gap-2">
          {hoSo?.sDuongDanCV ? (
            <button
              type="button"
              onClick={() =>
                void downloadBinary(
                  `/api/nguoi-tim-viec/cv-file/${encodeURIComponent(hoSo.pkMaUngVien)}`,
                  `CV_${hoSo.pkMaUngVien}.pdf`
                )
              }
              className="civic-btn-secondary py-1.5 px-3 text-xs"
            >
              <FileText className="w-3.5 h-3.5 text-[#215DA8]" />
              Tải CV đã lưu
            </button>
          ) : (
            <StatusBadge variant="warning">Chưa tải tệp CV PDF</StatusBadge>
          )}
          <label className="civic-btn-secondary py-1.5 px-3 text-xs cursor-pointer">
            <Upload className="w-3.5 h-3.5" />
            {uploadingCv ? "Đang tải CV..." : "Tải lên CV (PDF <= 5MB)"}
            <input
              id="fileUploadCV"
              type="file"
              accept="application/pdf"
              className="hidden"
              onChange={handleUploadCv}
              disabled={uploadingCv || !hoSo}
            />
          </label>
        </div>
      </section>

      <form onSubmit={handleSave} className="civic-card p-6 space-y-5">
        <h2 className="text-lg font-semibold text-[#192536]">Thông tin chi tiết hồ sơ lao động</h2>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label htmlFor="tbHoTenUv" className="block text-sm font-medium text-[#192536] mb-1">
              Họ và tên *
            </label>
            <input
              id="tbHoTenUv"
              type="text"
              className="civic-input"
              value={form.sHoTen}
              onChange={(e) => setForm({ ...form, sHoTen: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbCCCD" className="block text-sm font-medium text-[#192536] mb-1">
              Số CCCD (12 chữ số) *
            </label>
            <input
              id="tbCCCD"
              type="text"
              className="civic-input font-mono tabular-nums"
              maxLength={12}
              value={form.sSoCCCD}
              onChange={(e) => setForm({ ...form, sSoCCCD: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbNgaySinh" className="block text-sm font-medium text-[#192536] mb-1">
              Ngày sinh (đủ từ 15 tuổi) *
            </label>
            <input
              id="tbNgaySinh"
              type="date"
              className="civic-input tabular-nums"
              value={form.dNgaySinh}
              onChange={(e) => setForm({ ...form, dNgaySinh: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="cbbGioiTinh" className="block text-sm font-medium text-[#192536] mb-1">
              Giới tính *
            </label>
            <select
              id="cbbGioiTinh"
              className="civic-input"
              value={form.sGioiTinh}
              onChange={(e) => setForm({ ...form, sGioiTinh: e.target.value })}
            >
              <option value="Nam">Nam</option>
              <option value="Nữ">Nữ</option>
              <option value="Khác">Khác</option>
            </select>
          </div>
          <div>
            <label htmlFor="tbSdtUv" className="block text-sm font-medium text-[#192536] mb-1">
              Số điện thoại (10 số) *
            </label>
            <input
              id="tbSdtUv"
              type="tel"
              className="civic-input tabular-nums"
              value={form.sSDT}
              onChange={(e) => setForm({ ...form, sSDT: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbEmailUv" className="block text-sm font-medium text-[#192536] mb-1">
              Email liên hệ
            </label>
            <input
              id="tbEmailUv"
              type="email"
              className="civic-input"
              value={form.sEmailLienHe}
              onChange={(e) => setForm({ ...form, sEmailLienHe: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="cbbQuanCuTru" className="block text-sm font-medium text-[#192536] mb-1">
              Quận / Huyện cư trú tại Hà Nội *
            </label>
            <select
              id="cbbQuanCuTru"
              className="civic-input"
              value={form.fkMaQuanCuTru}
              onChange={(e) => setForm({ ...form, fkMaQuanCuTru: e.target.value })}
            >
              {quanList.map((qh) => (
                <option key={qh.pkMaQuanHuyen} value={qh.pkMaQuanHuyen}>
                  {qh.sTenQuanHuyen}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="tbTrinhDoVanHoa" className="block text-sm font-medium text-[#192536] mb-1">
              Trình độ văn hóa
            </label>
            <input
              id="tbTrinhDoVanHoa"
              type="text"
              className="civic-input"
              value={form.sTrinhDoVanHoa}
              onChange={(e) => setForm({ ...form, sTrinhDoVanHoa: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbChuyenMon" className="block text-sm font-medium text-[#192536] mb-1">
              Trình độ chuyên môn / Ngành học
            </label>
            <input
              id="tbChuyenMon"
              type="text"
              className="civic-input"
              placeholder="Ví dụ: Cử nhân Kế toán, Kỹ sư CNTT..."
              value={form.sTrinhDoChuyenMon}
              onChange={(e) => setForm({ ...form, sTrinhDoChuyenMon: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbTinHoc" className="block text-sm font-medium text-[#192536] mb-1">
              Kỹ năng tin học
            </label>
            <input
              id="tbTinHoc"
              type="text"
              className="civic-input"
              placeholder="Ví dụ: MOS Excel, MISA, SQL, Java..."
              value={form.sKyNangTinHoc}
              onChange={(e) => setForm({ ...form, sKyNangTinHoc: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbNgoaiNgu" className="block text-sm font-medium text-[#192536] mb-1">
              Trình độ ngoại ngữ
            </label>
            <input
              id="tbNgoaiNgu"
              type="text"
              className="civic-input"
              placeholder="Ví dụ: Tiếng Anh IELTS 6.0 / TOEIC 650"
              value={form.sTrinhDoNgoaiNgu}
              onChange={(e) => setForm({ ...form, sTrinhDoNgoaiNgu: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbViTriMongMuon" className="block text-sm font-medium text-[#192536] mb-1">
              Vị trí công việc mong muốn
            </label>
            <input
              id="tbViTriMongMuon"
              type="text"
              className="civic-input"
              value={form.sViTriMongMuon}
              onChange={(e) => setForm({ ...form, sViTriMongMuon: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbMucLuongKyVong" className="block text-sm font-medium text-[#192536] mb-1">
              Mức lương kỳ vọng (VNĐ)
            </label>
            <input
              id="tbMucLuongKyVong"
              type="number"
              className="civic-input tabular-nums"
              value={form.fMucLuongKyVong ?? ""}
              onChange={(e) =>
                setForm({ ...form, fMucLuongKyVong: e.target.value ? Number(e.target.value) : null })
              }
            />
          </div>
          <div className="sm:col-span-2">
            <label htmlFor="tbDiaBanMongMuon" className="block text-sm font-medium text-[#192536] mb-1">
              Địa bàn mong muốn làm việc tại Hà Nội
            </label>
            <input
              id="tbDiaBanMongMuon"
              type="text"
              className="civic-input"
              placeholder="Ví dụ: Cầu Giấy, Đống Đa, Nam Từ Liêm"
              value={form.sDiaBanMongMuon}
              onChange={(e) => setForm({ ...form, sDiaBanMongMuon: e.target.value })}
            />
          </div>
        </div>

        <div>
          <label htmlFor="tbKinhNghiemUv" className="block text-sm font-medium text-[#192536] mb-1">
            Tóm tắt kinh nghiệm làm việc
          </label>
          <textarea
            id="tbKinhNghiemUv"
            rows={4}
            className="civic-input"
            value={form.sKinhNghiemLamViec}
            onChange={(e) => setForm({ ...form, sKinhNghiemLamViec: e.target.value })}
          />
        </div>

        <div className="flex flex-wrap items-center justify-between gap-4 pt-2 border-t border-[#DCE2E9]">
          <label className="inline-flex items-center gap-2 text-sm text-[#192536] cursor-pointer">
            <input
              id="cbCongKhaiTimViec"
              type="checkbox"
              checked={form.bDangTimViecCongKhai}
              onChange={(e) => setForm({ ...form, bDangTimViecCongKhai: e.target.checked })}
              className="w-4 h-4 accent-[#215DA8]"
            />
            Cho phép doanh nghiệp đã xác minh tìm kiếm hồ sơ này (thông tin CCCD/SĐT/Email tự động được che)
          </label>

          <button id="btnLuuHoSoUv" type="submit" disabled={saving} className="civic-btn-primary">
            <Save className="w-4 h-4" />
            {saving ? "Đang lưu hồ sơ..." : "Lưu hồ sơ người tìm việc"}
          </button>
        </div>
      </form>
    </div>
  );
}

export default function HoSoUngVienPage() {
  return (
    <RoleGate allowedRoles={[1]}>
      <HoSoUngVienContent />
    </RoleGate>
  );
}
