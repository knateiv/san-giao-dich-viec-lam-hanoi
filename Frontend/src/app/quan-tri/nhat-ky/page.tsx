"use client";

import React, { useCallback, useEffect, useState } from "react";
import { FileSpreadsheet, ScrollText, Search, ShieldCheck } from "lucide-react";
import { apiFetch, downloadBinary, formatDateTimeVN } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { NhatKyHeThongView } from "@/types/models";

function severityBadge(level: string) {
  switch (level) {
    case "SECURITY_ACTION":
      return <StatusBadge variant="danger">{level}</StatusBadge>;
    case "WARNING":
      return <StatusBadge variant="warning">{level}</StatusBadge>;
    case "ERROR":
      return <StatusBadge variant="danger">{level}</StatusBadge>;
    default:
      return <StatusBadge variant="info">{level}</StatusBadge>;
  }
}

function NhatKyHeThongContent() {
  const [logs, setLogs] = useState<NhatKyHeThongView[]>([]);
  const [mucDo, setMucDo] = useState("");
  const [tuKhoa, setTuKhoa] = useState("");
  const [loading, setLoading] = useState(true);
  const [exporting, setExporting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const buildQuery = useCallback(() => {
    const qs = new URLSearchParams();
    if (mucDo) qs.set("mucDo", mucDo);
    if (tuKhoa.trim()) qs.set("tuKhoa", tuKhoa.trim());
    return qs.toString() ? `?${qs.toString()}` : "";
  }, [mucDo, tuKhoa]);

  const loadLogs = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiFetch<NhatKyHeThongView[]>(`/api/quan-tri/nhat-ky${buildQuery()}`);
      setLogs(res.duLieu || []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không tải được nhật ký hệ thống.");
    } finally {
      setLoading(false);
    }
  }, [buildQuery]);

  useEffect(() => {
    void loadLogs();
  }, [loadLogs]);

  const handleExportExcel = async () => {
    setError(null);
    setSuccess(null);
    setExporting(true);
    try {
      await downloadBinary(
        `/api/quan-tri/nhat-ky/xuat-excel${buildQuery()}`,
        "Audit_Log_DVVL_HaNoi.xlsx"
      );
      setSuccess("Đã kết xuất Nhật ký hệ thống (Audit Log) ra tệp Excel (.xlsx).");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Kết xuất Excel nhật ký thất bại.");
    } finally {
      setExporting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[#192536]">Nhật ký Kiểm toán Hệ thống (Audit Log Append-Only)</h1>
          <p className="text-sm text-[#5F6C7B] mt-1">
            Toàn bộ thao tác nhạy cảm và lịch sử biến động được SQL Server bảo vệ chống sửa/xóa (Append-Only).
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <StatusBadge variant="info">
            <ShieldCheck className="w-3.5 h-3.5 mr-1" />
            Chỉ thêm mới · Không chỉnh sửa
          </StatusBadge>
          <button
            id="btnXuatExcelNhatKy"
            type="button"
            disabled={exporting}
            onClick={() => void handleExportExcel()}
            className="civic-btn-primary text-xs"
          >
            <FileSpreadsheet className="w-4 h-4" />
            {exporting ? "Đang xuất..." : "Kết xuất Excel (.xlsx)"}
          </button>
        </div>
      </div>

      <form
        onSubmit={(e) => {
          e.preventDefault();
          void loadLogs();
        }}
        className="civic-card p-5 grid grid-cols-1 sm:grid-cols-3 gap-3"
      >
        <div>
          <label htmlFor="cbbMucDoLog" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Mức độ sự kiện
          </label>
          <select
            id="cbbMucDoLog"
            className="civic-input"
            value={mucDo}
            onChange={(e) => setMucDo(e.target.value)}
          >
            <option value="">Tất cả mức độ</option>
            <option value="INFO">INFO</option>
            <option value="WARNING">WARNING</option>
            <option value="SECURITY_ACTION">SECURITY_ACTION</option>
            <option value="ERROR">ERROR</option>
          </select>
        </div>
        <div>
          <label htmlFor="tbTuKhoaLog" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Từ khóa (hành động, email, IP)
          </label>
          <input
            id="tbTuKhoaLog"
            type="text"
            className="civic-input"
            placeholder="Ví dụ: DANG_NHAP, KHOA, minhanh..."
            value={tuKhoa}
            onChange={(e) => setTuKhoa(e.target.value)}
          />
        </div>
        <div className="flex items-end">
          <button type="submit" className="civic-btn-primary w-full">
            <Search className="w-4 h-4" />
            Tra cứu nhật ký
          </button>
        </div>
      </form>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <section className="civic-card overflow-hidden">
        <div className="px-5 py-4 border-b border-[#DCE2E9] flex items-center justify-between">
          <h2 className="text-base font-semibold text-[#192536] flex items-center gap-2">
            <ScrollText className="w-4 h-4 text-[#215DA8]" />
            Dòng thời gian sự kiện hệ thống
          </h2>
          <span className="text-xs text-[#5F6C7B] tabular-nums">{logs.length} bản ghi</span>
        </div>

        <div className="overflow-x-auto">
          <table id="dgvNhatKyHeThong" className="civic-table">
            <thead>
              <tr>
                <th>Thời gian</th>
                <th>Mức độ &amp; Hành động</th>
                <th>Người thực hiện / IP</th>
                <th>Dữ liệu trước &rarr; sau</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={4} className="text-center py-8 text-[#5F6C7B]">
                    Đang tải nhật ký kiểm toán...
                  </td>
                </tr>
              ) : logs.length === 0 ? (
                <tr>
                  <td colSpan={4} className="text-center py-8 text-[#5F6C7B]">
                    Không có bản ghi nhật ký phù hợp.
                  </td>
                </tr>
              ) : (
                logs.map((log) => (
                  <tr key={log.pkMaNhatKy}>
                    <td className="tabular-nums text-xs text-[#5F6C7B] whitespace-nowrap">
                      {formatDateTimeVN(log.dThoiGianGhi)}
                      <div className="font-mono text-[#192536]">#{log.pkMaNhatKy}</div>
                    </td>
                    <td>
                      <div className="mb-1">{severityBadge(log.sMucDo)}</div>
                      <div className="font-mono text-xs font-semibold text-[#192536]">
                        {log.sLoaiHanhDong}
                      </div>
                    </td>
                    <td>
                      <div className="text-sm font-medium text-[#192536]">{log.tenNguoiThucHien}</div>
                      <div className="text-xs font-mono text-[#5F6C7B]">
                        {log.sEmail || "—"} · IP: {log.sDiaChiIP || "127.0.0.1"}
                      </div>
                    </td>
                    <td className="font-mono text-xs text-[#5F6C7B] max-w-md break-all">
                      {log.sDuLieuCu && <div>Trước: {log.sDuLieuCu}</div>}
                      {log.sDuLieuMoi && <div className="text-[#192536]">Sau: {log.sDuLieuMoi}</div>}
                      {!log.sDuLieuCu && !log.sDuLieuMoi && "—"}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

export default function NhatKyPage() {
  return (
    <RoleGate allowedRoles={[4]}>
      <NhatKyHeThongContent />
    </RoleGate>
  );
}
