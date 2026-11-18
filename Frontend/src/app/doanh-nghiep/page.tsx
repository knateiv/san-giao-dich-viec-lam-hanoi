"use client";

import React, { useCallback, useEffect, useState } from "react";
import Link from "next/link";
import { CalendarPlus, Edit3, Lock, PlusCircle, ShieldCheck, Unlock, Users } from "lucide-react";
import { apiFetch, formatDateVN, formatSalaryRange } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { DoanhNghiepView, TinTuyenDungView } from "@/types/models";

function doanhNghiepStatusBadge(status: number) {
  if (status === 1) return <StatusBadge variant="success">Đã xác minh pháp lý</StatusBadge>;
  if (status === 2) return <StatusBadge variant="danger">Yêu cầu bổ sung</StatusBadge>;
  return <StatusBadge variant="warning">Chờ Cán bộ DVVL thẩm định</StatusBadge>;
}

function tinStatusBadge(tin: TinTuyenDungView) {
  const todayIso = new Date().toISOString().slice(0, 10);
  if (tin.iTrangThaiDuyet === 3) return <StatusBadge variant="danger">Đã gỡ vi phạm</StatusBadge>;
  if (tin.iTrangThaiDuyet === 2) return <StatusBadge variant="danger">Từ chối</StatusBadge>;
  if (tin.bDaDong) return <StatusBadge variant="neutral">Đang đóng nhận HS</StatusBadge>;
  if (tin.dHanNopHoSo && tin.dHanNopHoSo < todayIso) return <StatusBadge variant="neutral">Hết hạn nộp</StatusBadge>;
  if (tin.iTrangThaiDuyet === 1) return <StatusBadge variant="success">Đang hiển thị</StatusBadge>;
  return <StatusBadge variant="warning">Chờ duyệt</StatusBadge>;
}

function DoanhNghiepDashboardContent() {
  const [dn, setDn] = useState<DoanhNghiepView | null>(null);
  const [tins, setTins] = useState<TinTuyenDungView[]>([]);
  const [loading, setLoading] = useState(true);
  const [extendTarget, setExtendTarget] = useState<string | null>(null);
  const [extendDate, setExtendDate] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [dnRes, tinRes] = await Promise.all([
        apiFetch<DoanhNghiepView>("/api/doanh-nghiep/ho-so"),
        apiFetch<TinTuyenDungView[]>("/api/tin-tuyen-dung/cua-toi"),
      ]);
      setDn(dnRes.duLieu);
      setTins(tinRes.duLieu || []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không tải được dữ liệu doanh nghiệp.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadData();
  }, [loadData]);

  const handleToggleClose = async (tin: TinTuyenDungView) => {
    setError(null);
    setSuccess(null);
    try {
      const res = await apiFetch<TinTuyenDungView>(
        `/api/tin-tuyen-dung/${encodeURIComponent(tin.pkMaTin)}/dong-tin`,
        {
          method: "PATCH",
          body: JSON.stringify({ bDaDong: !tin.bDaDong }),
        }
      );
      setSuccess(res.thongBao);
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Cập nhật trạng thái đóng/mở tin thất bại.");
    }
  };

  const handleExtend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!extendTarget || !extendDate) return;
    setError(null);
    setSuccess(null);
    try {
      const res = await apiFetch<TinTuyenDungView>(
        `/api/tin-tuyen-dung/${encodeURIComponent(extendTarget)}/gia-han`,
        {
          method: "PATCH",
          body: JSON.stringify({ dHanNopMoi: extendDate }),
        }
      );
      setSuccess(res.thongBao);
      setExtendTarget(null);
      setExtendDate("");
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Gia hạn hạn nộp thất bại.");
    }
  };

  if (loading) {
    return <div className="civic-card p-6 text-sm text-[#5F6C7B]">Đang tải không gian doanh nghiệp...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[#192536]">
            {dn ? dn.sTenDoanhNghiep : "Quản lý Tuyển dụng Doanh nghiệp"}
          </h1>
          <p className="text-sm text-[#5F6C7B] mt-1">
            Theo dõi trạng thái thẩm định pháp lý, quản lý tin tuyển dụng, gia hạn hồ sơ và sàng lọc ứng viên.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Link href="/doanh-nghiep/phap-ly" className="civic-btn-secondary">
            <ShieldCheck className="w-4 h-4 text-[#215DA8]" />
            Hồ sơ pháp lý
          </Link>
          <Link href="/doanh-nghiep/dang-tin" className="civic-btn-primary">
            <PlusCircle className="w-4 h-4" />
            Đăng tin tuyển dụng
          </Link>
        </div>
      </div>

      {dn && (
        <section className="civic-card p-5 flex flex-wrap items-center justify-between gap-4">
          <div className="space-y-1">
            <div className="flex flex-wrap items-center gap-2">
              <span className="text-sm font-semibold text-[#192536]">Trạng thái pháp lý doanh nghiệp:</span>
              {doanhNghiepStatusBadge(dn.iTrangThaiThamDinh)}
              <span className="text-xs font-mono text-[#5F6C7B]">MST: {dn.sMaSoThue}</span>
            </div>
            {dn.sGhiChuThamDinh && (
              <p className="text-xs text-[#5F6C7B]">Ghi chú thẩm định: {dn.sGhiChuThamDinh}</p>
            )}
          </div>
          <Link href="/doanh-nghiep/ho-so-ung-tuyen" className="civic-btn-secondary text-xs">
            <Users className="w-4 h-4" />
            Sàng lọc ứng viên nộp hồ sơ
          </Link>
        </section>
      )}

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      {extendTarget && (
        <form onSubmit={handleExtend} className="civic-card p-4 bg-[#ECF3FC] border-[#BFD7F5] flex flex-wrap items-end gap-3">
          <div>
            <label htmlFor="tbGiaHanMoi" className="block text-xs font-semibold text-[#192536] mb-1">
              Gia hạn hạn nộp cho tin <span className="font-mono">{extendTarget}</span>
            </label>
            <input
              id="tbGiaHanMoi"
              type="date"
              className="civic-input tabular-nums"
              value={extendDate}
              onChange={(e) => setExtendDate(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="civic-btn-primary text-xs">
            Xác nhận gia hạn
          </button>
          <button
            type="button"
            onClick={() => setExtendTarget(null)}
            className="civic-btn-secondary text-xs"
          >
            Hủy
          </button>
        </form>
      )}

      <section className="civic-card overflow-hidden">
        <div className="px-5 py-4 border-b border-[#DCE2E9] flex items-center justify-between">
          <h2 className="text-base font-semibold text-[#192536]">Danh sách tin tuyển dụng đã đăng</h2>
          <span className="text-xs text-[#5F6C7B] tabular-nums">{tins.length} tin</span>
        </div>

        <div className="overflow-x-auto">
          <table id="dgvTinDoanhNghiep" className="civic-table">
            <thead>
              <tr>
                <th>Tiêu đề &amp; Vị trí</th>
                <th>Mức lương</th>
                <th>Hạn nộp</th>
                <th>Trạng thái</th>
                <th>Ứng viên</th>
                <th className="text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {tins.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center py-8 text-[#5F6C7B]">
                    Chưa có tin tuyển dụng nào. Nhấn &ldquo;Đăng tin tuyển dụng&rdquo; để khởi tạo tin mới.
                  </td>
                </tr>
              ) : (
                tins.map((tin) => (
                  <tr key={tin.pkMaTin}>
                    <td>
                      <div className="font-semibold text-[#192536]">{tin.sTieuDe}</div>
                      <div className="text-xs text-[#5F6C7B] mt-0.5 flex flex-wrap gap-x-2">
                        <span>{tin.sViTriChucDanh}</span>
                        <span>{tin.sTenQuanLamViec}</span>
                        <span className="font-mono">[{tin.pkMaTin}]</span>
                      </div>
                      {tin.sLyDoTuChoi && (
                        <div className="text-xs text-[#B91C1C] mt-1">Ghi chú kiểm duyệt: {tin.sLyDoTuChoi}</div>
                      )}
                    </td>
                    <td className="tabular-nums text-[#192536]">
                      {formatSalaryRange(tin.fLuongToiThieu, tin.fLuongToiDa)}
                    </td>
                    <td className="tabular-nums text-[#5F6C7B]">{formatDateVN(tin.dHanNopHoSo)}</td>
                    <td>{tinStatusBadge(tin)}</td>
                    <td className="tabular-nums font-semibold">
                      <Link
                        href={`/doanh-nghiep/ho-so-ung-tuyen?maTin=${encodeURIComponent(tin.pkMaTin)}`}
                        className="text-[#215DA8] hover:underline"
                      >
                        {tin.soHoSoUngTuyen} hồ sơ
                      </Link>
                    </td>
                    <td className="text-right">
                      <div className="flex flex-wrap justify-end gap-1.5">
                        <Link
                          href={`/doanh-nghiep/dang-tin?edit=${encodeURIComponent(tin.pkMaTin)}`}
                          className="civic-btn-secondary py-1 px-2.5 text-xs"
                        >
                          <Edit3 className="w-3.5 h-3.5" />
                          Sửa
                        </Link>
                        {tin.iTrangThaiDuyet === 1 && (
                          <>
                            <button
                              type="button"
                              onClick={() => void handleToggleClose(tin)}
                              className="civic-btn-secondary py-1 px-2.5 text-xs"
                            >
                              {tin.bDaDong ? <Unlock className="w-3.5 h-3.5" /> : <Lock className="w-3.5 h-3.5" />}
                              {tin.bDaDong ? "Mở lại" : "Đóng tin"}
                            </button>
                            <button
                              type="button"
                              onClick={() => {
                                setExtendTarget(tin.pkMaTin);
                                setExtendDate(tin.dHanNopHoSo || "");
                              }}
                              className="civic-btn-secondary py-1 px-2.5 text-xs"
                            >
                              <CalendarPlus className="w-3.5 h-3.5" />
                              Gia hạn
                            </button>
                          </>
                        )}
                      </div>
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

export default function DoanhNghiepPage() {
  return (
    <RoleGate allowedRoles={[2]}>
      <DoanhNghiepDashboardContent />
    </RoleGate>
  );
}
