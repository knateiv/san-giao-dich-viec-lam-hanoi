"use client";

import React, { Suspense, useCallback, useEffect, useState } from "react";
import { useSearchParams } from "next/navigation";
import { CalendarClock, CheckCircle2, Download, Eye, Users } from "lucide-react";
import { apiFetch, downloadBinary, formatDateTimeVN } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { TinTuyenDungView, UngTuyenView } from "@/types/models";

function appStatusBadge(code: number, label: string) {
  switch (code) {
    case 0:
      return <StatusBadge variant="neutral">{label}</StatusBadge>;
    case 1:
      return <StatusBadge variant="info">{label}</StatusBadge>;
    case 2:
      return <StatusBadge variant="warning">{label}</StatusBadge>;
    case 3:
      return <StatusBadge variant="success">{label}</StatusBadge>;
    case 4:
    case 5:
      return <StatusBadge variant="danger">{label}</StatusBadge>;
    default:
      return <StatusBadge variant="neutral">{label}</StatusBadge>;
  }
}

function HoSoUngTuyenContent() {
  const searchParams = useSearchParams();
  const initialMaTin = searchParams.get("maTin") || "";

  const [tins, setTins] = useState<TinTuyenDungView[]>([]);
  const [selectedTin, setSelectedTin] = useState(initialMaTin);
  const [ungViens, setUngViens] = useState<UngTuyenView[]>([]);
  const [activeUt, setActiveUt] = useState<UngTuyenView | null>(null);

  const [newStatus, setNewStatus] = useState<number>(2);
  const [dThoiGianPv, setDThoiGianPv] = useState("");
  const [sDiaDiemPv, setSDiaDiemPv] = useState("Văn phòng Công ty tại Hà Nội");
  const [sHinhThucPv, setSHinhThucPv] = useState("Trực tiếp");
  const [sGhiChuPv, setSGhiChuPv] = useState("");

  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const loadApplicants = useCallback(async (maTin: string) => {
    if (!maTin) {
      setUngViens([]);
      return;
    }
    try {
      const res = await apiFetch<UngTuyenView[]>(`/api/tin-tuyen-dung/${encodeURIComponent(maTin)}/ung-vien`);
      setUngViens(res.duLieu || []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không tải được danh sách ứng viên.");
    }
  }, []);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const res = await apiFetch<TinTuyenDungView[]>("/api/tin-tuyen-dung/cua-toi");
        if (!active) return;
        const list = res.duLieu || [];
        setTins(list);
        const chosen = initialMaTin && list.some((t) => t.pkMaTin === initialMaTin)
          ? initialMaTin
          : list[0]?.pkMaTin || "";
        setSelectedTin(chosen);
        if (chosen) await loadApplicants(chosen);
      } catch (err) {
        if (active) setError(err instanceof Error ? err.message : "Không tải được danh sách tin.");
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => {
      active = false;
    };
  }, [initialMaTin, loadApplicants]);

  const handleInspect = async (maUngTuyen: string) => {
    setError(null);
    setSuccess(null);
    try {
      // R21: GET chi-tiet tự động chuyển trạng thái 0 Chờ duyệt -> 1 Đã xem
      const res = await apiFetch<UngTuyenView>(`/api/ung-tuyen/chi-tiet/${encodeURIComponent(maUngTuyen)}`);
      const detail = res.duLieu;
      setActiveUt(detail);
      setNewStatus(detail.iTrangThaiUngTuyen === 2 ? 3 : 2);
      if (detail.dThoiGianPhongVan) {
        setDThoiGianPv(detail.dThoiGianPhongVan.slice(0, 16));
      } else {
        const d = new Date();
        d.setDate(d.getDate() + 3);
        d.setHours(9, 30, 0, 0);
        setDThoiGianPv(d.toISOString().slice(0, 16));
      }
      if (detail.sDiaDiemPhongVan) setSDiaDiemPv(detail.sDiaDiemPhongVan);
      if (detail.sHinhThucPhongVan) setSHinhThucPv(detail.sHinhThucPhongVan);
      setSGhiChuPv(detail.sGhiChuPhongVan || "");
      await loadApplicants(selectedTin);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không xem được chi tiết hồ sơ ứng tuyển.");
    }
  };

  const handleUpdateProgress = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeUt) return;
    setError(null);
    setSuccess(null);
    setUpdating(true);
    try {
      const formattedTime =
        newStatus === 2 && dThoiGianPv
          ? dThoiGianPv.length === 16
            ? `${dThoiGianPv}:00`
            : dThoiGianPv
          : null;
      const res = await apiFetch<UngTuyenView>(
        `/api/ung-tuyen/${encodeURIComponent(activeUt.pkMaUngTuyen)}/tien-trinh`,
        {
          method: "PUT",
          body: JSON.stringify({
            iTrangThaiUngTuyen: newStatus,
            dThoiGianPhongVan: formattedTime,
            sDiaDiemPhongVan: newStatus === 2 ? sDiaDiemPv : undefined,
            sHinhThucPhongVan: newStatus === 2 ? sHinhThucPv : undefined,
            sGhiChuPhongVan: sGhiChuPv.trim() || undefined,
          }),
        }
      );
      setActiveUt(res.duLieu);
      setSuccess(res.thongBao);
      await loadApplicants(selectedTin);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Cập nhật tiến trình phỏng vấn thất bại.");
    } finally {
      setUpdating(false);
    }
  };

  if (loading) {
    return <div className="civic-card p-6 text-sm text-[#5F6C7B]">Đang tải hồ sơ ứng tuyển...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[#192536]">Sàng lọc Hồ sơ &amp; Lên lịch Phỏng vấn</h1>
          <p className="text-sm text-[#5F6C7B] mt-1">
            Mở xem chi tiết ứng viên chưa duyệt sẽ tự động cập nhật trạng thái sang &ldquo;Đã xem&rdquo; để ứng viên nắm được tiến độ.
          </p>
        </div>

        <div className="w-full sm:w-80">
          <label htmlFor="cbbChonTinTuyenDung" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Chọn tin tuyển dụng cần sàng lọc
          </label>
          <select
            id="cbbChonTinTuyenDung"
            className="civic-input"
            value={selectedTin}
            onChange={(e) => {
              const v = e.target.value;
              setSelectedTin(v);
              setActiveUt(null);
              void loadApplicants(v);
            }}
          >
            {tins.length === 0 ? (
              <option value="">Chưa có tin tuyển dụng</option>
            ) : (
              tins.map((t) => (
                <option key={t.pkMaTin} value={t.pkMaTin}>
                  {t.pkMaTin} — {t.sTieuDe} ({t.soHoSoUngTuyen})
                </option>
              ))
            )}
          </select>
        </div>
      </div>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        <section className="lg:col-span-7 civic-card overflow-hidden">
          <div className="px-5 py-4 border-b border-[#DCE2E9] flex items-center justify-between">
            <h2 className="text-base font-semibold text-[#192536] flex items-center gap-2">
              <Users className="w-4 h-4 text-[#215DA8]" />
              Danh sách ứng viên đã nộp
            </h2>
            <span className="text-xs text-[#5F6C7B] tabular-nums">{ungViens.length} hồ sơ</span>
          </div>

          <div className="overflow-x-auto">
            <table id="dgvUngVienNopHoSo" className="civic-table">
              <thead>
                <tr>
                  <th>Ứng viên</th>
                  <th>Ngày nộp</th>
                  <th>Trạng thái</th>
                  <th className="text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {ungViens.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="text-center py-8 text-[#5F6C7B]">
                      Chưa có ứng viên nào nộp hồ sơ vào tin này.
                    </td>
                  </tr>
                ) : (
                  ungViens.map((uv) => (
                    <tr key={uv.pkMaUngTuyen}>
                      <td>
                        <div className="font-semibold text-[#192536]">{uv.sHoTenUngVien}</div>
                        <div className="text-xs text-[#5F6C7B] tabular-nums">
                          {uv.sSDTUngVien} · {uv.sTrinhDoChuyenMon || "Chưa cập nhật chuyên môn"}
                        </div>
                      </td>
                      <td className="tabular-nums text-xs text-[#5F6C7B]">{formatDateTimeVN(uv.dNgayNop)}</td>
                      <td>{appStatusBadge(uv.iTrangThaiUngTuyen, uv.tenTrangThaiUngTuyen)}</td>
                      <td className="text-right">
                        <button
                          type="button"
                          onClick={() => void handleInspect(uv.pkMaUngTuyen)}
                          className="civic-btn-secondary py-1 px-2.5 text-xs"
                        >
                          <Eye className="w-3.5 h-3.5" />
                          Xem &amp; Xử lý
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </section>

        <section className="lg:col-span-5 civic-card p-6 space-y-4">
          {!activeUt ? (
            <div className="text-sm text-[#5F6C7B] py-8 text-center">
              Chọn một ứng viên bên trái để xem hồ sơ chi tiết, tải CV PDF và cập nhật lịch phỏng vấn.
            </div>
          ) : (
            <>
              <div className="flex items-start justify-between gap-2 pb-3 border-b border-[#DCE2E9]">
                <div>
                  <h2 className="text-lg font-semibold text-[#192536]">{activeUt.sHoTenUngVien}</h2>
                  <div className="text-xs text-[#5F6C7B] tabular-nums">
                    {activeUt.sSDTUngVien} · {activeUt.sEmailUngVien}
                  </div>
                </div>
                {appStatusBadge(activeUt.iTrangThaiUngTuyen, activeUt.tenTrangThaiUngTuyen)}
              </div>

              <div className="text-sm space-y-1.5 text-[#192536]">
                <div>
                  <span className="font-semibold">Chuyên môn:</span> {activeUt.sTrinhDoChuyenMon || "—"}
                </div>
                <div>
                  <span className="font-semibold">Tin học:</span> {activeUt.sKyNangTinHoc || "—"}
                </div>
                <div>
                  <span className="font-semibold">Kinh nghiệm:</span> {activeUt.sKinhNghiemLamViec || "—"}
                </div>
              </div>

              {activeUt.sDuongDanCV && (
                <button
                  type="button"
                  onClick={() =>
                    void downloadBinary(
                      `/api/nguoi-tim-viec/cv-file/${encodeURIComponent(activeUt.fkMaUngVien)}`,
                      `CV_${activeUt.fkMaUngVien}.pdf`
                    )
                  }
                  className="civic-btn-secondary w-full text-xs"
                >
                  <Download className="w-4 h-4 text-[#215DA8]" />
                  Tải xuống tệp CV PDF của ứng viên
                </button>
              )}

              <form onSubmit={handleUpdateProgress} className="pt-3 border-t border-[#DCE2E9] space-y-3">
                <h3 className="text-sm font-semibold text-[#192536] flex items-center gap-1.5">
                  <CalendarClock className="w-4 h-4 text-[#215DA8]" />
                  Cập nhật kết quả xét duyệt
                </h3>

                <div>
                  <label htmlFor="cbbTrangThaiPv" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                    Trạng thái tiếp theo
                  </label>
                  <select
                    id="cbbTrangThaiPv"
                    className="civic-input"
                    value={newStatus}
                    onChange={(e) => setNewStatus(Number(e.target.value))}
                  >
                    <option value={2}>Mời phỏng vấn (Lên lịch hẹn)</option>
                    <option value={3}>Trúng tuyển (Tiếp nhận làm việc)</option>
                    <option value={4}>Từ chối / Chưa phù hợp</option>
                  </select>
                </div>

                {newStatus === 2 && (
                  <>
                    <div>
                      <label htmlFor="tbThoiGianPv" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                        Thời gian phỏng vấn *
                      </label>
                      <input
                        id="tbThoiGianPv"
                        type="datetime-local"
                        className="civic-input tabular-nums"
                        value={dThoiGianPv}
                        onChange={(e) => setDThoiGianPv(e.target.value)}
                        required
                      />
                    </div>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      <div>
                        <label htmlFor="cbbHinhThucPv" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                          Hình thức
                        </label>
                        <select
                          id="cbbHinhThucPv"
                          className="civic-input"
                          value={sHinhThucPv}
                          onChange={(e) => setSHinhThucPv(e.target.value)}
                        >
                          <option value="Trực tiếp">Trực tiếp</option>
                          <option value="Trực tuyến">Trực tuyến (Video)</option>
                        </select>
                      </div>
                      <div>
                        <label htmlFor="tbDiaDiemPv" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                          Địa điểm / Link họp *
                        </label>
                        <input
                          id="tbDiaDiemPv"
                          type="text"
                          className="civic-input"
                          value={sDiaDiemPv}
                          onChange={(e) => setSDiaDiemPv(e.target.value)}
                        />
                      </div>
                    </div>
                  </>
                )}

                <div>
                  <label htmlFor="tbGhiChuPv" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                    Ghi chú / Thông báo gửi ứng viên
                  </label>
                  <textarea
                    id="tbGhiChuPv"
                    rows={2}
                    className="civic-input"
                    value={sGhiChuPv}
                    onChange={(e) => setSGhiChuPv(e.target.value)}
                  />
                </div>

                <button id="btnCapNhatTienTrinh" type="submit" disabled={updating} className="civic-btn-primary w-full">
                  <CheckCircle2 className="w-4 h-4" />
                  {updating ? "Đang cập nhật..." : "Lưu trạng thái xét duyệt"}
                </button>
              </form>
            </>
          )}
        </section>
      </div>
    </div>
  );
}

export default function HoSoUngTuyenPage() {
  return (
    <RoleGate allowedRoles={[2]}>
      <Suspense fallback={<div className="civic-card p-6 text-sm text-[#5F6C7B]">Đang tải...</div>}>
        <HoSoUngTuyenContent />
      </Suspense>
    </RoleGate>
  );
}
