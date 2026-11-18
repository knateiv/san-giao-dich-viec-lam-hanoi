"use client";

import React, { useCallback, useEffect, useState } from "react";
import Link from "next/link";
import { CalendarClock, CheckCircle2, History, RefreshCw, XCircle } from "lucide-react";
import { apiFetch, formatDateTimeVN } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { UngTuyenView } from "@/types/models";

function trangThaiVariant(code: number): "neutral" | "info" | "success" | "warning" | "danger" {
  switch (code) {
    case 0:
      return "neutral";
    case 1:
      return "info";
    case 2:
      return "warning";
    case 3:
      return "success";
    case 4:
    case 5:
      return "danger";
    default:
      return "neutral";
  }
}

function LichSuUngTuyenContent() {
  const [list, setList] = useState<UngTuyenView[]>([]);
  const [loading, setLoading] = useState(true);
  const [expandedId, setExpandedId] = useState<string | null>(null);
  const [livePolling, setLivePolling] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const fetchHistory = useCallback(async (silent = false) => {
    if (!silent) setLoading(true);
    try {
      const res = await apiFetch<UngTuyenView[]>("/api/ung-tuyen/lich-su-cua-toi");
      setList(res.duLieu || []);
    } catch (err) {
      if (!silent) {
        setError(err instanceof Error ? err.message : "Không tải được lịch sử ứng tuyển.");
      }
    } finally {
      if (!silent) setLoading(false);
    }
  }, []);

  useEffect(() => {
    void fetchHistory(false);
  }, [fetchHistory]);

  // R22 & R32: Đồng bộ trạng thái ứng tuyển gần thời gian thực (2 giây)
  useEffect(() => {
    if (!livePolling) return;
    const timer = window.setInterval(() => {
      void fetchHistory(true);
    }, 2000);
    return () => window.clearInterval(timer);
  }, [livePolling, fetchHistory]);

  const handleConfirmInterview = async (maUngTuyen: string) => {
    setError(null);
    setSuccess(null);
    try {
      const res = await apiFetch<UngTuyenView>(`/api/ung-tuyen/${encodeURIComponent(maUngTuyen)}/xac-nhan-pv`, {
        method: "PATCH",
      });
      setSuccess(res.thongBao);
      await fetchHistory(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Xác nhận phỏng vấn thất bại.");
    }
  };

  const handleCancel = async (maUngTuyen: string) => {
    setError(null);
    setSuccess(null);
    try {
      const res = await apiFetch<UngTuyenView>(`/api/ung-tuyen/${encodeURIComponent(maUngTuyen)}/huy`, {
        method: "PUT",
        body: JSON.stringify({ sLyDo: "Ứng viên chủ động rút hồ sơ." }),
      });
      setSuccess(res.thongBao);
      await fetchHistory(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Hủy hồ sơ thất bại.");
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[#192536]">Lịch sử ứng tuyển &amp; Tiến trình phỏng vấn</h1>
          <p className="text-sm text-[#5F6C7B] mt-1">
            Theo dõi trạng thái xét duyệt hồ sơ trực tuyến tự động cập nhật mỗi 2 giây từ nhà tuyển dụng.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <button
            type="button"
            onClick={() => setLivePolling((prev) => !prev)}
            className="civic-btn-secondary text-xs"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${livePolling ? "text-[#15803D]" : "text-[#5F6C7B]"}`} />
            {livePolling ? "Đồng bộ 2 giây: Đang bật" : "Đồng bộ 2 giây: Đang tạm dừng"}
          </button>
          <Link href="/viec-lam" className="civic-btn-primary text-xs">
            Tìm thêm việc làm
          </Link>
        </div>
      </div>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <section className="civic-card overflow-hidden">
        <div className="overflow-x-auto">
          <table id="dgvLichSuUngTuyen" className="civic-table">
            <thead>
              <tr>
                <th>Vị trí ứng tuyển</th>
                <th>Doanh nghiệp</th>
                <th>Ngày nộp</th>
                <th>Trạng thái</th>
                <th>Lịch phỏng vấn</th>
                <th className="text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={6} className="text-center py-8 text-[#5F6C7B]">
                    Đang tải lịch sử ứng tuyển...
                  </td>
                </tr>
              ) : list.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center py-8 text-[#5F6C7B]">
                    Bạn chưa nộp hồ sơ vào vị trí tuyển dụng nào.
                  </td>
                </tr>
              ) : (
                list.map((ut) => (
                  <React.Fragment key={ut.pkMaUngTuyen}>
                    <tr>
                      <td>
                        <Link
                          href={`/viec-lam/${ut.fkMaTin}`}
                          className="font-semibold text-[#215DA8] hover:underline"
                        >
                          {ut.sTieuDeTin}
                        </Link>
                        <div className="text-xs text-[#5F6C7B] mt-0.5">
                          {ut.sViTriChucDanh} · <span className="font-mono">{ut.pkMaUngTuyen}</span>
                        </div>
                      </td>
                      <td className="font-medium text-[#192536]">{ut.sTenDoanhNghiep}</td>
                      <td className="tabular-nums text-[#5F6C7B]">{formatDateTimeVN(ut.dNgayNop)}</td>
                      <td>
                        <StatusBadge variant={trangThaiVariant(ut.iTrangThaiUngTuyen)}>
                          {ut.tenTrangThaiUngTuyen}
                        </StatusBadge>
                      </td>
                      <td>
                        {ut.dThoiGianPhongVan ? (
                          <div className="text-xs space-y-0.5">
                            <div className="font-semibold text-[#192536] tabular-nums inline-flex items-center gap-1">
                              <CalendarClock className="w-3.5 h-3.5 text-[#215DA8]" />
                              {formatDateTimeVN(ut.dThoiGianPhongVan)}
                            </div>
                            <div className="text-[#5F6C7B]">
                              {ut.sHinhThucPhongVan || "Trực tiếp"} · {ut.sDiaDiemPhongVan || "Tại văn phòng"}
                            </div>
                            {ut.sGhiChuPhongVan && (
                              <div className="text-[#5F6C7B]">{ut.sGhiChuPhongVan}</div>
                            )}
                          </div>
                        ) : (
                          <span className="text-xs text-[#5F6C7B]">Chưa có lịch hẹn</span>
                        )}
                      </td>
                      <td className="text-right">
                        <div className="flex flex-wrap justify-end gap-1.5">
                          <button
                            type="button"
                            onClick={() =>
                              setExpandedId((prev) => (prev === ut.pkMaUngTuyen ? null : ut.pkMaUngTuyen))
                            }
                            className="civic-btn-secondary py-1 px-2.5 text-xs"
                          >
                            <History className="w-3.5 h-3.5" />
                            Nhật ký
                          </button>
                          {ut.iTrangThaiUngTuyen === 2 && (
                            <button
                              type="button"
                              onClick={() => void handleConfirmInterview(ut.pkMaUngTuyen)}
                              className="civic-btn-primary py-1 px-2.5 text-xs"
                            >
                              <CheckCircle2 className="w-3.5 h-3.5" />
                              Xác nhận PV
                            </button>
                          )}
                          {(ut.iTrangThaiUngTuyen === 0 || ut.iTrangThaiUngTuyen === 1) && (
                            <button
                              type="button"
                              onClick={() => void handleCancel(ut.pkMaUngTuyen)}
                              className="civic-btn-secondary py-1 px-2.5 text-xs text-[#B91C1C]"
                            >
                              <XCircle className="w-3.5 h-3.5" />
                              Rút hồ sơ
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                    {expandedId === ut.pkMaUngTuyen && (
                      <tr className="bg-[#F8FAFC]">
                        <td colSpan={6} className="px-6 py-4">
                          <div className="text-xs font-semibold text-[#192536] mb-2">
                            Lịch sử biến động trạng thái ({ut.pkMaUngTuyen})
                          </div>
                          {ut.lichSu && ut.lichSu.length > 0 ? (
                            <ul className="space-y-1.5 text-xs text-[#5F6C7B]">
                              {ut.lichSu.map((ls) => (
                                <li key={ls.pkMaLichSu} className="flex flex-wrap items-center gap-2">
                                  <span className="tabular-nums font-mono text-[#192536]">
                                    {formatDateTimeVN(ls.dThoiGianThayDoi)}
                                  </span>
                                  <StatusBadge variant={trangThaiVariant(ls.iTrangThaiMoi)}>
                                    {ls.tenTrangThaiMoi}
                                  </StatusBadge>
                                  <span>{ls.sGhiChu || "Cập nhật hệ thống"}</span>
                                </li>
                              ))}
                            </ul>
                          ) : (
                            <p className="text-xs text-[#5F6C7B]">Chưa có ghi nhận lịch sử chi tiết.</p>
                          )}
                        </td>
                      </tr>
                    )}
                  </React.Fragment>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

export default function LichSuUngTuyenPage() {
  return (
    <RoleGate allowedRoles={[1]}>
      <LichSuUngTuyenContent />
    </RoleGate>
  );
}
