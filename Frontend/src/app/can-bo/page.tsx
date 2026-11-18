"use client";

import React, { useCallback, useEffect, useState } from "react";
import { AlertTriangle, Building2, CheckCircle2, Download, FileCheck2, ShieldAlert, XCircle } from "lucide-react";
import { apiFetch, downloadBinary, formatDateVN, formatSalaryRange } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { DoanhNghiepView, TinTuyenDungView } from "@/types/models";

function dnBadge(st: number) {
  if (st === 1) return <StatusBadge variant="success">Đã xác minh</StatusBadge>;
  if (st === 2) return <StatusBadge variant="danger">Từ chối / Yêu cầu bổ sung</StatusBadge>;
  return <StatusBadge variant="warning">Chờ thẩm định</StatusBadge>;
}

function tinBadge(st: number) {
  if (st === 1) return <StatusBadge variant="success">Đã duyệt</StatusBadge>;
  if (st === 2) return <StatusBadge variant="danger">Từ chối</StatusBadge>;
  if (st === 3) return <StatusBadge variant="danger">Gỡ vi phạm khẩn cấp</StatusBadge>;
  return <StatusBadge variant="warning">Chờ duyệt</StatusBadge>;
}

function CanBoKiemDuyetContent() {
  const [tab, setTab] = useState<"dn" | "tin">("dn");
  const [dnFilter, setDnFilter] = useState("");
  const [tinFilter, setTinFilter] = useState("");

  const [dnList, setDnList] = useState<DoanhNghiepView[]>([]);
  const [tinList, setTinList] = useState<TinTuyenDungView[]>([]);

  const [actionType, setActionType] = useState<"dn-reject" | "tin-reject" | "tin-takedown" | null>(null);
  const [targetId, setTargetId] = useState("");
  const [reason, setReason] = useState("");

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [dnRes, tinRes] = await Promise.all([
        apiFetch<DoanhNghiepView[]>(
          `/api/kiem-duyet/doanh-nghiep${dnFilter !== "" ? `?trangThai=${dnFilter}` : ""}`
        ),
        apiFetch<TinTuyenDungView[]>(
          `/api/kiem-duyet/tin-tuyen-dung${tinFilter !== "" ? `?trangThai=${tinFilter}` : ""}`
        ),
      ]);
      setDnList(dnRes.duLieu || []);
      setTinList(tinRes.duLieu || []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không tải được danh sách kiểm duyệt.");
    } finally {
      setLoading(false);
    }
  }, [dnFilter, tinFilter]);

  useEffect(() => {
    void loadData();
  }, [loadData]);

  const handleApproveDn = async (maDn: string) => {
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const res = await apiFetch<DoanhNghiepView>(`/api/kiem-duyet/doanh-nghiep/${encodeURIComponent(maDn)}`, {
        method: "PUT",
        body: JSON.stringify({
          iTrangThaiThamDinh: 1,
          sGhiChuThamDinh: "Đạt thẩm định pháp lý ĐKKD tại Trung tâm DVVL Hà Nội.",
        }),
      });
      setSuccess(res.thongBao);
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Phê duyệt doanh nghiệp thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleApproveTin = async (maTin: string) => {
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const res = await apiFetch<TinTuyenDungView>(
        `/api/kiem-duyet/tin-tuyen-dung/${encodeURIComponent(maTin)}`,
        {
          method: "PUT",
          body: JSON.stringify({ iTrangThaiDuyet: 1, sLyDoTuChoi: "" }),
        }
      );
      setSuccess(res.thongBao);
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Duyệt tin tuyển dụng thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleSubmitReason = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!reason.trim()) {
      setError("Vui lòng nhập đầy đủ lý do / căn cứ xử lý.");
      return;
    }
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      if (actionType === "dn-reject") {
        const res = await apiFetch<DoanhNghiepView>(
          `/api/kiem-duyet/doanh-nghiep/${encodeURIComponent(targetId)}`,
          {
            method: "PUT",
            body: JSON.stringify({ iTrangThaiThamDinh: 2, sGhiChuThamDinh: reason.trim() }),
          }
        );
        setSuccess(res.thongBao);
      } else if (actionType === "tin-reject") {
        const res = await apiFetch<TinTuyenDungView>(
          `/api/kiem-duyet/tin-tuyen-dung/${encodeURIComponent(targetId)}`,
          {
            method: "PUT",
            body: JSON.stringify({ iTrangThaiDuyet: 2, sLyDoTuChoi: reason.trim() }),
          }
        );
        setSuccess(res.thongBao);
      } else if (actionType === "tin-takedown") {
        const res = await apiFetch<void>(
          `/api/kiem-duyet/tin-tuyen-dung/${encodeURIComponent(targetId)}/go-khan-cap`,
          {
            method: "PUT",
            body: JSON.stringify({ sLyDo: reason.trim() }),
          }
        );
        setSuccess(res.thongBao);
      }
      setActionType(null);
      setTargetId("");
      setReason("");
      await loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Xử lý yêu cầu kiểm duyệt thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[#192536]">Kiểm duyệt Pháp lý &amp; Tin tuyển dụng</h1>
        <p className="text-sm text-[#5F6C7B] mt-1">
          Phân hệ nghiệp vụ Cán bộ Trung tâm DVVL Hà Nội: Thẩm định Doanh nghiệp, duyệt tin đăng và gỡ khẩn cấp tin vi phạm.
        </p>
      </div>

      <div className="flex flex-wrap gap-2" role="tablist">
        <button
          type="button"
          onClick={() => setTab("dn")}
          className={`inline-flex items-center gap-2 px-4 py-2.5 rounded-lg border text-sm font-semibold transition-colors duration-150 ${
            tab === "dn"
              ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
              : "border-[#DCE2E9] bg-white text-[#5F6C7B]"
          }`}
        >
          <Building2 className="w-4 h-4" />
          Thẩm định Doanh nghiệp ({dnList.length})
        </button>
        <button
          type="button"
          onClick={() => setTab("tin")}
          className={`inline-flex items-center gap-2 px-4 py-2.5 rounded-lg border text-sm font-semibold transition-colors duration-150 ${
            tab === "tin"
              ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
              : "border-[#DCE2E9] bg-white text-[#5F6C7B]"
          }`}
        >
          <FileCheck2 className="w-4 h-4" />
          Kiểm duyệt Tin tuyển dụng ({tinList.length})
        </button>
      </div>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      {actionType && (
        <form onSubmit={handleSubmitReason} className="civic-card p-5 bg-amber-50 border-amber-200 space-y-3">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold text-[#192536] flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 text-[#B45309]" />
              {actionType === "dn-reject"
                ? `Từ chối / Yêu cầu bổ sung pháp lý (${targetId})`
                : actionType === "tin-reject"
                ? `Từ chối phê duyệt tin (${targetId})`
                : `Gỡ bỏ khẩn cấp tin vi phạm (${targetId})`}
            </h2>
            <button type="button" onClick={() => setActionType(null)} className="text-xs font-semibold text-[#5F6C7B]">
              Đóng
            </button>
          </div>
          <input
            id="tbLyDoKiemDuyet"
            type="text"
            className="civic-input"
            placeholder="Nhập lý do / căn cứ kiểm duyệt (bắt buộc)..."
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            required
          />
          <button type="submit" disabled={submitting} className="civic-btn-primary text-xs">
            Xác nhận quyết định
          </button>
        </form>
      )}

      {tab === "dn" && (
        <section className="civic-card overflow-hidden">
          <div className="px-5 py-4 border-b border-[#DCE2E9] flex flex-wrap items-center justify-between gap-3">
            <h2 className="text-base font-semibold text-[#192536]">Danh sách hồ sơ doanh nghiệp</h2>
            <select
              id="cbbLocTrangThaiDn"
              className="civic-input w-full sm:w-56"
              value={dnFilter}
              onChange={(e) => setDnFilter(e.target.value)}
            >
              <option value="">Tất cả trạng thái</option>
              <option value="0">Chờ thẩm định</option>
              <option value="1">Đã xác minh</option>
              <option value="2">Yêu cầu bổ sung</option>
            </select>
          </div>
          <div className="overflow-x-auto">
            <table id="dgvThamDinhDn" className="civic-table">
              <thead>
                <tr>
                  <th>Doanh nghiệp / MST</th>
                  <th>Trụ sở &amp; Đại diện</th>
                  <th>GPKD &amp; Trạng thái</th>
                  <th className="text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan={4} className="text-center py-8 text-[#5F6C7B]">
                      Đang tải...
                    </td>
                  </tr>
                ) : dnList.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="text-center py-8 text-[#5F6C7B]">
                      Không có hồ sơ doanh nghiệp phù hợp.
                    </td>
                  </tr>
                ) : (
                  dnList.map((dn) => (
                    <tr key={dn.pkMaDoanhNghiep}>
                      <td>
                        <div className="font-semibold text-[#192536]">{dn.sTenDoanhNghiep}</div>
                        <div className="text-xs font-mono text-[#5F6C7B]">
                          {dn.pkMaDoanhNghiep} · MST: {dn.sMaSoThue}
                        </div>
                      </td>
                      <td>
                        <div className="text-sm text-[#192536]">{dn.sTenQuanTruSo}</div>
                        <div className="text-xs text-[#5F6C7B]">
                          Đại diện: {dn.sNguoiDaiDien || "—"} · {dn.sSDTNguoiLienHe || "—"}
                        </div>
                      </td>
                      <td className="space-y-1">
                        <div>{dnBadge(dn.iTrangThaiThamDinh)}</div>
                        {dn.sDuongDanGiayPhep && (
                          <button
                            type="button"
                            onClick={() =>
                              void downloadBinary(
                                `/api/doanh-nghiep/giay-phep/${encodeURIComponent(dn.pkMaDoanhNghiep)}`,
                                `GPKD_${dn.pkMaDoanhNghiep}`
                              )
                            }
                            className="inline-flex items-center gap-1 text-xs font-medium text-[#215DA8] hover:underline"
                          >
                            <Download className="w-3 h-3" />
                            Tải GPKD
                          </button>
                        )}
                      </td>
                      <td className="text-right">
                        <div className="flex flex-wrap justify-end gap-1.5">
                          {dn.iTrangThaiThamDinh !== 1 && (
                            <button
                              type="button"
                              disabled={submitting}
                              onClick={() => void handleApproveDn(dn.pkMaDoanhNghiep)}
                              className="civic-btn-primary py-1 px-2.5 text-xs"
                            >
                              <CheckCircle2 className="w-3.5 h-3.5" />
                              Xác minh đạt
                            </button>
                          )}
                          <button
                            type="button"
                            disabled={submitting}
                            onClick={() => {
                              setActionType("dn-reject");
                              setTargetId(dn.pkMaDoanhNghiep);
                              setReason("");
                            }}
                            className="civic-btn-secondary py-1 px-2.5 text-xs text-[#B91C1C]"
                          >
                            <XCircle className="w-3.5 h-3.5" />
                            Yêu cầu bổ sung
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </section>
      )}

      {tab === "tin" && (
        <section className="civic-card overflow-hidden">
          <div className="px-5 py-4 border-b border-[#DCE2E9] flex flex-wrap items-center justify-between gap-3">
            <h2 className="text-base font-semibold text-[#192536]">Danh sách tin tuyển dụng kiểm duyệt</h2>
            <select
              id="cbbLocTrangThaiTin"
              className="civic-input w-full sm:w-56"
              value={tinFilter}
              onChange={(e) => setTinFilter(e.target.value)}
            >
              <option value="">Tất cả trạng thái</option>
              <option value="0">Chờ duyệt</option>
              <option value="1">Đã duyệt</option>
              <option value="2">Từ chối</option>
            </select>
          </div>
          <div className="overflow-x-auto">
            <table id="dgvDuyetTin" className="civic-table">
              <thead>
                <tr>
                  <th>Tiêu đề &amp; Doanh nghiệp</th>
                  <th>Mức lương &amp; Hạn nộp</th>
                  <th>Trạng thái</th>
                  <th className="text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan={4} className="text-center py-8 text-[#5F6C7B]">
                      Đang tải...
                    </td>
                  </tr>
                ) : tinList.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="text-center py-8 text-[#5F6C7B]">
                      Không có tin tuyển dụng phù hợp.
                    </td>
                  </tr>
                ) : (
                  tinList.map((t) => (
                    <tr key={t.pkMaTin}>
                      <td>
                        <div className="font-semibold text-[#192536]">{t.sTieuDe}</div>
                        <div className="text-xs text-[#5F6C7B] flex flex-wrap gap-x-2">
                          <span>{t.sTenDoanhNghiep}</span>
                          <span>{t.sTenQuanLamViec}</span>
                          <span className="font-mono">[{t.pkMaTin}]</span>
                        </div>
                        {t.sLyDoTuChoi && (
                          <div className="text-xs text-[#B91C1C] mt-1">Lý do: {t.sLyDoTuChoi}</div>
                        )}
                      </td>
                      <td className="tabular-nums text-xs text-[#192536]">
                        <div className="font-semibold">{formatSalaryRange(t.fLuongToiThieu, t.fLuongToiDa)}</div>
                        <div className="text-[#5F6C7B]">Hạn: {formatDateVN(t.dHanNopHoSo)}</div>
                      </td>
                      <td>{tinBadge(t.iTrangThaiDuyet)}</td>
                      <td className="text-right">
                        <div className="flex flex-wrap justify-end gap-1.5">
                          {t.iTrangThaiDuyet !== 1 && (
                            <button
                              type="button"
                              disabled={submitting}
                              onClick={() => void handleApproveTin(t.pkMaTin)}
                              className="civic-btn-primary py-1 px-2.5 text-xs"
                            >
                              <CheckCircle2 className="w-3.5 h-3.5" />
                              Duyệt đăng
                            </button>
                          )}
                          {t.iTrangThaiDuyet === 0 && (
                            <button
                              type="button"
                              disabled={submitting}
                              onClick={() => {
                                setActionType("tin-reject");
                                setTargetId(t.pkMaTin);
                                setReason("");
                              }}
                              className="civic-btn-secondary py-1 px-2.5 text-xs text-[#B91C1C]"
                            >
                              <XCircle className="w-3.5 h-3.5" />
                              Từ chối
                            </button>
                          )}
                          {t.iTrangThaiDuyet === 1 && (
                            <button
                              type="button"
                              disabled={submitting}
                              onClick={() => {
                                setActionType("tin-takedown");
                                setTargetId(t.pkMaTin);
                                setReason("");
                              }}
                              className="civic-btn-secondary py-1 px-2.5 text-xs text-[#B91C1C]"
                            >
                              <ShieldAlert className="w-3.5 h-3.5" />
                              Gỡ khẩn cấp
                            </button>
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
      )}
    </div>
  );
}

export default function CanBoPage() {
  return (
    <RoleGate allowedRoles={[3, 4]}>
      <CanBoKiemDuyetContent />
    </RoleGate>
  );
}
