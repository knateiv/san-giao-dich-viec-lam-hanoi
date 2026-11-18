"use client";

import React, { Suspense, useEffect, useState } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { ArrowLeft, ArrowRight, CheckCircle2, FilePlus2 } from "lucide-react";
import { apiFetch } from "@/lib/apiClient";
import { AlertBanner, RoleGate } from "@/components/AppShell";
import type { DanhMucVaTongQuan, NganhNghe, QuanHuyen, TinRequest, TinTuyenDungView } from "@/types/models";

function defaultDeadline(): string {
  const d = new Date();
  d.setDate(d.getDate() + 30);
  return d.toISOString().slice(0, 10);
}

function DangTinContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const editId = searchParams.get("edit");

  const [step, setStep] = useState<1 | 2>(1);
  const [nganhList, setNganhList] = useState<NganhNghe[]>([]);
  const [quanList, setQuanList] = useState<QuanHuyen[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const [form, setForm] = useState<TinRequest>({
    sTieuDe: "",
    sViTriChucDanh: "",
    fkMaNganhNghe: 1,
    fkMaQuanLamViec: "QH001",
    sDiaChiCuThe: "",
    iSoLuongTuyen: 2,
    sCapBac: "Nhân viên",
    sHinhThucLamViec: "Toàn thời gian",
    fLuongToiThieu: 10000000,
    fLuongToiDa: 18000000,
    sYeuCauKinhNghiem: "Từ 1 năm kinh nghiệm",
    sYeuCauHocVan: "Đại học",
    sMoTaCongViec: "",
    sCheDoDaiNgo: "Đóng BHXH đầy đủ, nghỉ phép năm và thưởng theo hiệu quả công việc.",
    dHanNopHoSo: defaultDeadline(),
  });

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const dmRes = await apiFetch<DanhMucVaTongQuan>("/api/tim-viec/danh-muc");
        if (!active) return;
        setNganhList(dmRes.duLieu.nganhNghe || []);
        setQuanList(dmRes.duLieu.quanHuyen || []);

        if (editId) {
          const listRes = await apiFetch<TinTuyenDungView[]>("/api/tin-tuyen-dung/cua-toi");
          const target = (listRes.duLieu || []).find((t) => t.pkMaTin === editId);
          if (target && active) {
            setForm({
              sTieuDe: target.sTieuDe,
              sViTriChucDanh: target.sViTriChucDanh,
              fkMaNganhNghe: target.fkMaNganhNghe,
              fkMaQuanLamViec: target.fkMaQuanLamViec,
              sDiaChiCuThe: target.sDiaChiCuThe || "",
              iSoLuongTuyen: target.iSoLuongTuyen,
              sCapBac: target.sCapBac || "Nhân viên",
              sHinhThucLamViec: target.sHinhThucLamViec || "Toàn thời gian",
              fLuongToiThieu: target.fLuongToiThieu,
              fLuongToiDa: target.fLuongToiDa,
              sYeuCauKinhNghiem: target.sYeuCauKinhNghiem || "",
              sYeuCauHocVan: target.sYeuCauHocVan || "Đại học",
              sMoTaCongViec: target.sMoTaCongViec || "",
              sCheDoDaiNgo: target.sCheDoDaiNgo || "",
              dHanNopHoSo: target.dHanNopHoSo || defaultDeadline(),
            });
          }
        }
      } catch (err) {
        if (active) setError(err instanceof Error ? err.message : "Không tải được danh mục biểu mẫu.");
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => {
      active = false;
    };
  }, [editId]);

  const validateStep1 = () => {
    if (!form.sTieuDe.trim() || !form.sViTriChucDanh.trim()) {
      setError("Vui lòng nhập Tiêu đề tin tuyển dụng và Vị trí / chức danh.");
      return false;
    }
    if (form.fLuongToiDa < form.fLuongToiThieu) {
      setError("Mức lương tối đa phải lớn hơn hoặc bằng mức lương tối thiểu.");
      return false;
    }
    if (form.iSoLuongTuyen < 1) {
      setError("Số lượng tuyển tối thiểu là 1.");
      return false;
    }
    setError(null);
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateStep1()) {
      setStep(1);
      return;
    }
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const path = editId ? `/api/tin-tuyen-dung/${encodeURIComponent(editId)}` : "/api/tin-tuyen-dung";
      const method = editId ? "PUT" : "POST";
      const res = await apiFetch<TinTuyenDungView>(path, {
        method,
        body: JSON.stringify(form),
      });
      setSuccess(res.thongBao);
      window.setTimeout(() => router.push("/doanh-nghiep"), 900);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Lưu tin tuyển dụng thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="civic-card p-6 text-sm text-[#5F6C7B]">Đang tải biểu mẫu tin tuyển dụng...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div>
        <Link href="/doanh-nghiep" className="inline-flex items-center gap-1.5 text-sm font-medium text-[#215DA8] hover:underline mb-1">
          <ArrowLeft className="w-4 h-4" />
          Danh sách tin của doanh nghiệp
        </Link>
        <h1 className="text-2xl font-bold text-[#192536]">
          {editId ? `Chỉnh sửa tin tuyển dụng (${editId})` : "Đăng tin tuyển dụng mới"}
        </h1>
        <p className="text-sm text-[#5F6C7B] mt-1">
          Doanh nghiệp phải được xác minh pháp lý trước khi gửi duyệt. Hạn nộp hồ sơ mới phải cách hiện tại tối thiểu 3 ngày.
        </p>
      </div>

      <div className="grid grid-cols-2 gap-3" role="tablist">
        <button
          type="button"
          onClick={() => setStep(1)}
          className={`p-3 rounded-lg border text-left transition-colors duration-150 ${
            step === 1
              ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
              : "border-[#DCE2E9] bg-white text-[#5F6C7B]"
          }`}
        >
          <div className="text-xs font-semibold">Bước 1</div>
          <div className="text-sm font-bold">Vị trí, Địa bàn &amp; Lương</div>
        </button>
        <button
          type="button"
          onClick={() => {
            if (validateStep1()) setStep(2);
          }}
          className={`p-3 rounded-lg border text-left transition-colors duration-150 ${
            step === 2
              ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
              : "border-[#DCE2E9] bg-white text-[#5F6C7B]"
          }`}
        >
          <div className="text-xs font-semibold">Bước 2</div>
          <div className="text-sm font-bold">Yêu cầu, Mô tả &amp; Gửi duyệt</div>
        </button>
      </div>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <form onSubmit={handleSubmit} className="civic-card p-6 space-y-5">
        {step === 1 && (
          <div className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                <label htmlFor="tbTieuDeTin" className="block text-sm font-medium text-[#192536] mb-1">
                  Tiêu đề tin tuyển dụng *
                </label>
                <input
                  id="tbTieuDeTin"
                  type="text"
                  className="civic-input"
                  placeholder="Ví dụ: Tuyển Kế toán tổng hợp làm việc tại Cầu Giấy"
                  value={form.sTieuDe}
                  onChange={(e) => setForm({ ...form, sTieuDe: e.target.value })}
                />
              </div>
              <div>
                <label htmlFor="tbViTriTin" className="block text-sm font-medium text-[#192536] mb-1">
                  Vị trí / Chức danh *
                </label>
                <input
                  id="tbViTriTin"
                  type="text"
                  className="civic-input"
                  value={form.sViTriChucDanh}
                  onChange={(e) => setForm({ ...form, sViTriChucDanh: e.target.value })}
                />
              </div>
              <div>
                <label htmlFor="cbbNganhTin" className="block text-sm font-medium text-[#192536] mb-1">
                  Ngành nghề chuẩn *
                </label>
                <select
                  id="cbbNganhTin"
                  className="civic-input"
                  value={form.fkMaNganhNghe}
                  onChange={(e) => setForm({ ...form, fkMaNganhNghe: Number(e.target.value) })}
                >
                  {nganhList.map((nn) => (
                    <option key={nn.pkMaNganhNghe} value={nn.pkMaNganhNghe}>
                      {nn.sTenNganhNghe}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label htmlFor="cbbQuanTin" className="block text-sm font-medium text-[#192536] mb-1">
                  Quận / Huyện làm việc *
                </label>
                <select
                  id="cbbQuanTin"
                  className="civic-input"
                  value={form.fkMaQuanLamViec}
                  onChange={(e) => setForm({ ...form, fkMaQuanLamViec: e.target.value })}
                >
                  {quanList.map((qh) => (
                    <option key={qh.pkMaQuanHuyen} value={qh.pkMaQuanHuyen}>
                      {qh.sTenQuanHuyen}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label htmlFor="tbDiaChiTin" className="block text-sm font-medium text-[#192536] mb-1">
                  Địa chỉ làm việc cụ thể
                </label>
                <input
                  id="tbDiaChiTin"
                  type="text"
                  className="civic-input"
                  value={form.sDiaChiCuThe}
                  onChange={(e) => setForm({ ...form, sDiaChiCuThe: e.target.value })}
                />
              </div>
              <div>
                <label htmlFor="nudSoLuong" className="block text-sm font-medium text-[#192536] mb-1">
                  Số lượng tuyển *
                </label>
                <input
                  id="nudSoLuong"
                  type="number"
                  min={1}
                  className="civic-input tabular-nums"
                  value={form.iSoLuongTuyen}
                  onChange={(e) => setForm({ ...form, iSoLuongTuyen: Number(e.target.value) || 1 })}
                />
              </div>
              <div>
                <label htmlFor="tbHanNop" className="block text-sm font-medium text-[#192536] mb-1">
                  Hạn nộp hồ sơ *
                </label>
                <input
                  id="tbHanNop"
                  type="date"
                  className="civic-input tabular-nums"
                  value={form.dHanNopHoSo}
                  onChange={(e) => setForm({ ...form, dHanNopHoSo: e.target.value })}
                />
              </div>
              <div>
                <label htmlFor="tbLuongMin" className="block text-sm font-medium text-[#192536] mb-1">
                  Lương tối thiểu (VNĐ) *
                </label>
                <input
                  id="tbLuongMin"
                  type="number"
                  min={0}
                  step={500000}
                  className="civic-input tabular-nums"
                  value={form.fLuongToiThieu}
                  onChange={(e) => setForm({ ...form, fLuongToiThieu: Number(e.target.value) || 0 })}
                />
              </div>
              <div>
                <label htmlFor="tbLuongMax" className="block text-sm font-medium text-[#192536] mb-1">
                  Lương tối đa (VNĐ) *
                </label>
                <input
                  id="tbLuongMax"
                  type="number"
                  min={0}
                  step={500000}
                  className="civic-input tabular-nums"
                  value={form.fLuongToiDa}
                  onChange={(e) => setForm({ ...form, fLuongToiDa: Number(e.target.value) || 0 })}
                />
              </div>
            </div>

            <div className="flex justify-end">
              <button
                type="button"
                onClick={() => {
                  if (validateStep1()) setStep(2);
                }}
                className="civic-btn-primary"
              >
                Tiếp tục Bước 2
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}

        {step === 2 && (
          <div className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div>
                <label htmlFor="cbbCapBacTin" className="block text-sm font-medium text-[#192536] mb-1">
                  Cấp bậc
                </label>
                <input
                  id="cbbCapBacTin"
                  type="text"
                  className="civic-input"
                  value={form.sCapBac}
                  onChange={(e) => setForm({ ...form, sCapBac: e.target.value })}
                />
              </div>
              <div>
                <label htmlFor="cbbHinhThucTin" className="block text-sm font-medium text-[#192536] mb-1">
                  Hình thức làm việc
                </label>
                <select
                  id="cbbHinhThucTin"
                  className="civic-input"
                  value={form.sHinhThucLamViec}
                  onChange={(e) => setForm({ ...form, sHinhThucLamViec: e.target.value })}
                >
                  <option value="Toàn thời gian">Toàn thời gian</option>
                  <option value="Bán thời gian">Bán thời gian</option>
                  <option value="Theo ca">Theo ca</option>
                  <option value="Thực tập">Thực tập</option>
                </select>
              </div>
              <div>
                <label htmlFor="cbbHocVanTin" className="block text-sm font-medium text-[#192536] mb-1">
                  Yêu cầu học vấn
                </label>
                <select
                  id="cbbHocVanTin"
                  className="civic-input"
                  value={form.sYeuCauHocVan}
                  onChange={(e) => setForm({ ...form, sYeuCauHocVan: e.target.value })}
                >
                  <option value="THPT">THPT</option>
                  <option value="Trung cấp">Trung cấp</option>
                  <option value="Cao đẳng">Cao đẳng</option>
                  <option value="Đại học">Đại học</option>
                  <option value="Sau đại học">Sau đại học</option>
                </select>
              </div>
            </div>

            <div>
              <label htmlFor="tbKinhNghiemTin" className="block text-sm font-medium text-[#192536] mb-1">
                Yêu cầu kinh nghiệm
              </label>
              <input
                id="tbKinhNghiemTin"
                type="text"
                className="civic-input"
                value={form.sYeuCauKinhNghiem}
                onChange={(e) => setForm({ ...form, sYeuCauKinhNghiem: e.target.value })}
              />
            </div>

            <div>
              <label htmlFor="tbMoTaTin" className="block text-sm font-medium text-[#192536] mb-1">
                Mô tả công việc chi tiết
              </label>
              <textarea
                id="tbMoTaTin"
                rows={4}
                className="civic-input"
                value={form.sMoTaCongViec}
                onChange={(e) => setForm({ ...form, sMoTaCongViec: e.target.value })}
              />
            </div>

            <div>
              <label htmlFor="tbDaiNgoTin" className="block text-sm font-medium text-[#192536] mb-1">
                Chế độ đãi ngộ
              </label>
              <textarea
                id="tbDaiNgoTin"
                rows={3}
                className="civic-input"
                value={form.sCheDoDaiNgo}
                onChange={(e) => setForm({ ...form, sCheDoDaiNgo: e.target.value })}
              />
            </div>

            <div className="flex flex-wrap items-center justify-between gap-3 pt-2 border-t border-[#DCE2E9]">
              <button type="button" onClick={() => setStep(1)} className="civic-btn-secondary">
                <ArrowLeft className="w-4 h-4" />
                Quay lại Bước 1
              </button>
              <button id="btnGuiDuyetTin" type="submit" disabled={submitting} className="civic-btn-primary">
                {editId ? <CheckCircle2 className="w-4 h-4" /> : <FilePlus2 className="w-4 h-4" />}
                {submitting ? "Đang gửi..." : editId ? "Cập nhật tin tuyển dụng" : "Lưu & Gửi Cán bộ DVVL duyệt"}
              </button>
            </div>
          </div>
        )}
      </form>
    </div>
  );
}

export default function DangTinPage() {
  return (
    <RoleGate allowedRoles={[2]}>
      <Suspense fallback={<div className="civic-card p-6 text-sm text-[#5F6C7B]">Đang tải...</div>}>
        <DangTinContent />
      </Suspense>
    </RoleGate>
  );
}
