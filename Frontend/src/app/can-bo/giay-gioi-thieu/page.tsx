"use client";

import React, { useEffect, useState } from "react";
import { Download, FileCheck2, QrCode } from "lucide-react";
import { apiFetch, downloadBinary } from "@/lib/apiClient";
import { AlertBanner, RoleGate } from "@/components/AppShell";
import type { HoSoView, TinTuyenDungView } from "@/types/models";

function GiayGioiThieuContent() {
  const [ungViens, setUngViens] = useState<HoSoView[]>([]);
  const [tins, setTins] = useState<TinTuyenDungView[]>([]);
  const [maUngVien, setMaUngVien] = useState("");
  const [maTin, setMaTin] = useState("");
  const [ngayHen, setNgayHen] = useState("2026-12-01 09:00");
  const [ghiChu, setGhiChu] = useState("Đề nghị doanh nghiệp tiếp nhận phỏng vấn trực tiếp theo giấy giới thiệu của Trung tâm DVVL Hà Nội.");

  const [loading, setLoading] = useState(true);
  const [exporting, setExporting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const [uvRes, tinRes] = await Promise.all([
          apiFetch<HoSoView[]>("/api/kiem-duyet/ung-vien"),
          apiFetch<TinTuyenDungView[]>("/api/kiem-duyet/tin-tuyen-dung?trangThai=1"),
        ]);
        if (!active) return;
        const uvs = uvRes.duLieu || [];
        const approvedTins = tinRes.duLieu || [];
        setUngViens(uvs);
        setTins(approvedTins);
        setMaUngVien(uvs[0]?.pkMaUngVien || "");
        setMaTin(approvedTins[0]?.pkMaTin || "");
      } catch (err) {
        if (active) setError(err instanceof Error ? err.message : "Không tải được danh sách ứng viên / tin đã duyệt.");
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => {
      active = false;
    };
  }, []);

  const handleExportReferral = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!maUngVien || !maTin) {
      setError("Vui lòng chọn đầy đủ Ứng viên và Tin tuyển dụng đã duyệt.");
      return;
    }
    setError(null);
    setSuccess(null);
    setExporting(true);
    try {
      const qs = new URLSearchParams({
        maUngVien,
        maTin,
        ngayHen: ngayHen.trim(),
        ghiChu: ghiChu.trim(),
      });
      await downloadBinary(
        `/api/kiem-duyet/giay-gioi-thieu-pdf?${qs.toString()}`,
        `Giay_Gioi_Thieu_${maUngVien}_${maTin}.pdf`
      );
      setSuccess("Đã cấp và xuất tệp PDF Giấy giới thiệu việc làm có số công văn và mã QR xác thực.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Cấp Giấy giới thiệu PDF thất bại.");
    } finally {
      setExporting(false);
    }
  };

  const handleExportCandidateForm = async () => {
    if (!maUngVien) return;
    setError(null);
    setSuccess(null);
    setExporting(true);
    try {
      await downloadBinary(
        `/api/nguoi-tim-viec/phieu-dang-ky-pdf/${encodeURIComponent(maUngVien)}`,
        `Phieu_Dang_Ky_Tim_Viec_${maUngVien}.pdf`
      );
      setSuccess("Đã xuất tệp PDF Phiếu đăng ký tìm việc làm chuẩn mẫu A4.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Xuất Phiếu đăng ký PDF thất bại.");
    } finally {
      setExporting(false);
    }
  };

  if (loading) {
    return <div className="civic-card p-6 text-sm text-[#5F6C7B]">Đang tải biểu mẫu cấp giấy giới thiệu...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[#192536]">Cấp Giấy giới thiệu Việc làm &amp; Phiếu hành chính PDF</h1>
        <p className="text-sm text-[#5F6C7B] mt-1">
          Kết xuất biểu mẫu chuẩn A4 UTF-8 Times New Roman đính kèm số công văn và mã QR Code xác thực tại doanh nghiệp tiếp nhận.
        </p>
      </div>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <form onSubmit={handleExportReferral} className="civic-card p-6 space-y-5">
        <div className="flex items-center gap-2 text-base font-semibold text-[#192536]">
          <QrCode className="w-5 h-5 text-[#215DA8]" />
          Thông tin cấp Giấy giới thiệu việc làm (R17)
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label htmlFor="cbbUngVienGgt" className="block text-sm font-medium text-[#192536] mb-1">
              Ứng viên được giới thiệu *
            </label>
            <select
              id="cbbUngVienGgt"
              className="civic-input"
              value={maUngVien}
              onChange={(e) => setMaUngVien(e.target.value)}
            >
              {ungViens.map((uv) => (
                <option key={uv.pkMaUngVien} value={uv.pkMaUngVien}>
                  {uv.pkMaUngVien} — {uv.sHoTen} ({uv.sTenQuanCuTru})
                </option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="cbbTinGgt" className="block text-sm font-medium text-[#192536] mb-1">
              Vị trí &amp; Doanh nghiệp tiếp nhận (Đã duyệt) *
            </label>
            <select
              id="cbbTinGgt"
              className="civic-input"
              value={maTin}
              onChange={(e) => setMaTin(e.target.value)}
            >
              {tins.map((t) => (
                <option key={t.pkMaTin} value={t.pkMaTin}>
                  {t.pkMaTin} — {t.sTieuDe} ({t.sTenDoanhNghiep})
                </option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="tbNgayHenGgt" className="block text-sm font-medium text-[#192536] mb-1">
              Thời gian hẹn tiếp nhận dự kiến
            </label>
            <input
              id="tbNgayHenGgt"
              type="text"
              className="civic-input tabular-nums"
              value={ngayHen}
              onChange={(e) => setNgayHen(e.target.value)}
            />
          </div>
          <div>
            <label htmlFor="tbGhiChuGgt" className="block text-sm font-medium text-[#192536] mb-1">
              Ghi chú công văn giới thiệu
            </label>
            <input
              id="tbGhiChuGgt"
              type="text"
              className="civic-input"
              value={ghiChu}
              onChange={(e) => setGhiChu(e.target.value)}
            />
          </div>
        </div>

        <div className="flex flex-wrap gap-3 pt-2 border-t border-[#DCE2E9]">
          <button id="btnCapGiayGioiThieuPdf" type="submit" disabled={exporting} className="civic-btn-primary">
            <FileCheck2 className="w-4 h-4" />
            {exporting ? "Đang tạo PDF..." : "Cấp & Tải Giấy giới thiệu (PDF + QR)"}
          </button>
          <button
            id="btnXuatPhieuUvCanBo"
            type="button"
            disabled={exporting || !maUngVien}
            onClick={() => void handleExportCandidateForm()}
            className="civic-btn-secondary"
          >
            <Download className="w-4 h-4" />
            Xuất Phiếu ĐK của ứng viên (PDF)
          </button>
        </div>
      </form>
    </div>
  );
}

export default function GiayGioiThieuPage() {
  return (
    <RoleGate allowedRoles={[3, 4]}>
      <GiayGioiThieuContent />
    </RoleGate>
  );
}
