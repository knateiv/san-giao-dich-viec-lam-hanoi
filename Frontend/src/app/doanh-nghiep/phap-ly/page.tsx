"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { ArrowLeft, Download, Save, ShieldCheck, Upload } from "lucide-react";
import { apiFetch, apiUpload, downloadBinary } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { DanhMucVaTongQuan, DoanhNghiepView, PhapLyRequest, QuanHuyen } from "@/types/models";

const EMPTY_PHAPLY: PhapLyRequest = {
  sTenDoanhNghiep: "",
  sTenVietTat: "",
  sMaSoThue: "",
  sDiaChiTruSo: "",
  fkMaQuanTruSo: "QH001",
  sLinhVucHoatDong: "",
  sNguoiDaiDien: "",
  sTenNguoiLienHe: "",
  sSDTNguoiLienHe: "",
  sEmailNguoiLienHe: "",
};

function PhapLyContent() {
  const [dn, setDn] = useState<DoanhNghiepView | null>(null);
  const [form, setForm] = useState<PhapLyRequest>(EMPTY_PHAPLY);
  const [quanList, setQuanList] = useState<QuanHuyen[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const [dmRes, dnRes] = await Promise.all([
          apiFetch<DanhMucVaTongQuan>("/api/tim-viec/danh-muc"),
          apiFetch<DoanhNghiepView>("/api/doanh-nghiep/ho-so"),
        ]);
        if (!active) return;
        setQuanList(dmRes.duLieu.quanHuyen || []);
        const d = dnRes.duLieu;
        setDn(d);
        if (d) {
          setForm({
            sTenDoanhNghiep: d.sTenDoanhNghiep || "",
            sTenVietTat: d.sTenVietTat || "",
            sMaSoThue: d.sMaSoThue || "",
            sDiaChiTruSo: d.sDiaChiTruSo || "",
            fkMaQuanTruSo: d.fkMaQuanTruSo || "QH001",
            sLinhVucHoatDong: d.sLinhVucHoatDong || "",
            sNguoiDaiDien: d.sNguoiDaiDien || "",
            sTenNguoiLienHe: d.sTenNguoiLienHe || "",
            sSDTNguoiLienHe: d.sSDTNguoiLienHe || "",
            sEmailNguoiLienHe: d.sEmailNguoiLienHe || "",
          });
        }
      } catch (err) {
        if (!active) return;
        setError(err instanceof Error ? err.message : "Không tải được hồ sơ pháp lý.");
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => {
      active = false;
    };
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSaving(true);
    try {
      const res = await apiFetch<DoanhNghiepView>("/api/doanh-nghiep/ho-so", {
        method: "PUT",
        body: JSON.stringify(form),
      });
      setDn(res.duLieu);
      setSuccess(res.thongBao);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Lưu thông tin pháp lý thất bại.");
    } finally {
      setSaving(false);
    }
  };

  const handleUploadGpkd = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setError(null);
    setSuccess(null);
    setUploading(true);
    try {
      const fd = new FormData();
      fd.append("file", file);
      const res = await apiUpload<DoanhNghiepView>("/api/doanh-nghiep/upload-giay-phep", fd, "POST");
      setDn(res.duLieu);
      setSuccess(res.thongBao);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Tải lên Giấy phép ĐKKD thất bại.");
    } finally {
      setUploading(false);
      e.target.value = "";
    }
  };

  if (loading) {
    return <div className="civic-card p-6 text-sm text-[#5F6C7B]">Đang tải hồ sơ pháp lý doanh nghiệp...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <Link href="/doanh-nghiep" className="inline-flex items-center gap-1.5 text-sm font-medium text-[#215DA8] hover:underline mb-1">
            <ArrowLeft className="w-4 h-4" />
            Quay lại tổng quan doanh nghiệp
          </Link>
          <h1 className="text-2xl font-bold text-[#192536]">Hồ sơ Pháp lý Doanh nghiệp &amp; GPKD</h1>
        </div>
        {dn && (
          <StatusBadge
            variant={
              dn.iTrangThaiThamDinh === 1
                ? "success"
                : dn.iTrangThaiThamDinh === 2
                ? "danger"
                : "warning"
            }
          >
            {dn.iTrangThaiThamDinh === 1
              ? "Đã xác minh pháp lý"
              : dn.iTrangThaiThamDinh === 2
              ? "Yêu cầu bổ sung"
              : "Chờ thẩm định"}
          </StatusBadge>
        )}
      </div>

      {dn?.sGhiChuThamDinh && (
        <AlertBanner
          type={dn.iTrangThaiThamDinh === 1 ? "success" : dn.iTrangThaiThamDinh === 2 ? "warning" : "info"}
          message={`Phản hồi từ Cán bộ Trung tâm DVVL: ${dn.sGhiChuThamDinh}`}
        />
      )}

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <section className="civic-card p-5 flex flex-wrap items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="text-sm font-semibold text-[#192536] flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-[#215DA8]" />
            Giấy chứng nhận Đăng ký doanh nghiệp (PDF / PNG / JPG &lt;= 5MB)
          </div>
          <p className="text-xs text-[#5F6C7B]">
            Thay đổi tên doanh nghiệp, mã số thuế hoặc tải GPKD mới sẽ chuyển trạng thái về &ldquo;Chờ thẩm định&rdquo;.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          {dn?.sDuongDanGiayPhep && (
            <button
              type="button"
              onClick={() =>
                void downloadBinary(
                  `/api/doanh-nghiep/giay-phep/${encodeURIComponent(dn.pkMaDoanhNghiep)}`,
                  `GPKD_${dn.pkMaDoanhNghiep}`
                )
              }
              className="civic-btn-secondary text-xs"
            >
              <Download className="w-3.5 h-3.5" />
              Xem GPKD đã nộp
            </button>
          )}
          <label className="civic-btn-secondary text-xs cursor-pointer">
            <Upload className="w-3.5 h-3.5" />
            {uploading ? "Đang tải..." : "Tải lên GPKD"}
            <input
              id="fileUploadGpkd"
              type="file"
              accept="application/pdf,image/png,image/jpeg"
              className="hidden"
              onChange={handleUploadGpkd}
              disabled={uploading || !dn}
            />
          </label>
        </div>
      </section>

      <form onSubmit={handleSave} className="civic-card p-6 space-y-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="sm:col-span-2">
            <label htmlFor="tbTenDn" className="block text-sm font-medium text-[#192536] mb-1">
              Tên đầy đủ theo ĐKKD *
            </label>
            <input
              id="tbTenDn"
              type="text"
              className="civic-input"
              value={form.sTenDoanhNghiep}
              onChange={(e) => setForm({ ...form, sTenDoanhNghiep: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbTenVietTat" className="block text-sm font-medium text-[#192536] mb-1">
              Tên viết tắt
            </label>
            <input
              id="tbTenVietTat"
              type="text"
              className="civic-input"
              value={form.sTenVietTat}
              onChange={(e) => setForm({ ...form, sTenVietTat: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbMstDn" className="block text-sm font-medium text-[#192536] mb-1">
              Mã số thuế (10 hoặc 13 chữ số) *
            </label>
            <input
              id="tbMstDn"
              type="text"
              className="civic-input font-mono tabular-nums"
              value={form.sMaSoThue}
              onChange={(e) => setForm({ ...form, sMaSoThue: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="cbbQuanTruSo" className="block text-sm font-medium text-[#192536] mb-1">
              Quận / Huyện trụ sở tại Hà Nội *
            </label>
            <select
              id="cbbQuanTruSo"
              className="civic-input"
              value={form.fkMaQuanTruSo}
              onChange={(e) => setForm({ ...form, fkMaQuanTruSo: e.target.value })}
            >
              {quanList.map((qh) => (
                <option key={qh.pkMaQuanHuyen} value={qh.pkMaQuanHuyen}>
                  {qh.sTenQuanHuyen}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="tbDiaChiTruSo" className="block text-sm font-medium text-[#192536] mb-1">
              Địa chỉ trụ sở chi tiết *
            </label>
            <input
              id="tbDiaChiTruSo"
              type="text"
              className="civic-input"
              value={form.sDiaChiTruSo}
              onChange={(e) => setForm({ ...form, sDiaChiTruSo: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbLinhVuc" className="block text-sm font-medium text-[#192536] mb-1">
              Lĩnh vực hoạt động chính *
            </label>
            <input
              id="tbLinhVuc"
              type="text"
              className="civic-input"
              value={form.sLinhVucHoatDong}
              onChange={(e) => setForm({ ...form, sLinhVucHoatDong: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbNguoiDaiDien" className="block text-sm font-medium text-[#192536] mb-1">
              Người đại diện pháp luật *
            </label>
            <input
              id="tbNguoiDaiDien"
              type="text"
              className="civic-input"
              value={form.sNguoiDaiDien}
              onChange={(e) => setForm({ ...form, sNguoiDaiDien: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbNguoiLienHe" className="block text-sm font-medium text-[#192536] mb-1">
              Cán bộ phụ trách tuyển dụng *
            </label>
            <input
              id="tbNguoiLienHe"
              type="text"
              className="civic-input"
              value={form.sTenNguoiLienHe}
              onChange={(e) => setForm({ ...form, sTenNguoiLienHe: e.target.value })}
            />
          </div>
          <div>
            <label htmlFor="tbSdtLienHe" className="block text-sm font-medium text-[#192536] mb-1">
              SĐT liên hệ (10 số) *
            </label>
            <input
              id="tbSdtLienHe"
              type="tel"
              className="civic-input tabular-nums"
              value={form.sSDTNguoiLienHe}
              onChange={(e) => setForm({ ...form, sSDTNguoiLienHe: e.target.value })}
            />
          </div>
          <div className="sm:col-span-2">
            <label htmlFor="tbEmailLienHeDn" className="block text-sm font-medium text-[#192536] mb-1">
              Email tuyển dụng *
            </label>
            <input
              id="tbEmailLienHeDn"
              type="email"
              className="civic-input"
              value={form.sEmailNguoiLienHe}
              onChange={(e) => setForm({ ...form, sEmailNguoiLienHe: e.target.value })}
            />
          </div>
        </div>

        <button id="btnLuuPhapLy" type="submit" disabled={saving} className="civic-btn-primary">
          <Save className="w-4 h-4" />
          {saving ? "Đang gửi hồ sơ..." : "Lưu & Gửi thẩm định pháp lý"}
        </button>
      </form>
    </div>
  );
}

export default function PhapLyPage() {
  return (
    <RoleGate allowedRoles={[2]}>
      <PhapLyContent />
    </RoleGate>
  );
}
