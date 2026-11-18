"use client";

import React, { useCallback, useEffect, useState } from "react";
import { BarChart3, Download, FileSpreadsheet, Filter } from "lucide-react";
import { apiFetch, downloadBinary } from "@/lib/apiClient";
import { AlertBanner, RoleGate } from "@/components/AppShell";
import type { DanhMucVaTongQuan, NganhNghe, QuanHuyen, TongHopBaoCaoView } from "@/types/models";

function BaoCaoCungCauContent() {
  const [nganhList, setNganhList] = useState<NganhNghe[]>([]);
  const [quanList, setQuanList] = useState<QuanHuyen[]>([]);
  const [data, setData] = useState<TongHopBaoCaoView | null>(null);

  const [tuNgay, setTuNgay] = useState("");
  const [denNgay, setDenNgay] = useState("");
  const [maNganhNghe, setMaNganhNghe] = useState("");
  const [maQuanHuyen, setMaQuanHuyen] = useState("");

  const [loading, setLoading] = useState(true);
  const [exporting, setExporting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const buildQueryString = useCallback(() => {
    const qs = new URLSearchParams();
    if (tuNgay) qs.set("tuNgay", tuNgay);
    if (denNgay) qs.set("denNgay", denNgay);
    if (maNganhNghe) qs.set("maNganhNghe", maNganhNghe);
    if (maQuanHuyen) qs.set("maQuanHuyen", maQuanHuyen);
    return qs.toString() ? `?${qs.toString()}` : "";
  }, [tuNgay, denNgay, maNganhNghe, maQuanHuyen]);

  const loadReport = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiFetch<TongHopBaoCaoView>(`/api/thong-ke/cung-cau${buildQueryString()}`);
      setData(res.duLieu);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không tải được báo cáo cung-cầu lao động.");
    } finally {
      setLoading(false);
    }
  }, [buildQueryString]);

  useEffect(() => {
    apiFetch<DanhMucVaTongQuan>("/api/tim-viec/danh-muc")
      .then((res) => {
        setNganhList(res.duLieu.nganhNghe || []);
        setQuanList(res.duLieu.quanHuyen || []);
      })
      .catch(() => {});
    void loadReport();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleExportPdf = async () => {
    setError(null);
    setSuccess(null);
    setExporting(true);
    try {
      await downloadBinary(`/api/thong-ke/xuat-pdf${buildQueryString()}`, "Bao_Cao_Cung_Cau_HaNoi.pdf");
      setSuccess("Đã xuất Báo cáo thống kê Cung-Cầu định dạng PDF A4.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Xuất PDF thất bại.");
    } finally {
      setExporting(false);
    }
  };

  const handleExportExcel = async () => {
    setError(null);
    setSuccess(null);
    setExporting(true);
    try {
      await downloadBinary(`/api/thong-ke/xuat-excel${buildQueryString()}`, "Bao_Cao_Cung_Cau_HaNoi.xlsx");
      setSuccess("Đã xuất Báo cáo thống kê Cung-Cầu định dạng Excel (.xlsx) kèm công thức SUM.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Xuất Excel thất bại.");
    } finally {
      setExporting(false);
    }
  };

  const chartIndustries = (data?.theoNganhNghe || []).slice(0, 8);
  const maxBar = Math.max(
    1,
    ...chartIndustries.flatMap((d) => [d.iTongCungHoSo, d.iTongCauChiTieu, d.iTongLuotUngTuyen])
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[#192536]">Thống kê &amp; Báo cáo Cung – Cầu Lao động Hà Nội</h1>
          <p className="text-sm text-[#5F6C7B] mt-1">
            Tổng hợp số liệu thực tế từ Sở LĐ-TB&amp;XH / Trung tâm DVVL Hà Nội theo Quận/Huyện và Ngành nghề chuẩn.
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          <button
            id="btnXuatBaoCaoPdf"
            type="button"
            disabled={exporting}
            onClick={() => void handleExportPdf()}
            className="civic-btn-primary"
          >
            <Download className="w-4 h-4" />
            Xuất PDF A4
          </button>
          <button
            id="btnXuatBaoCaoExcel"
            type="button"
            disabled={exporting}
            onClick={() => void handleExportExcel()}
            className="civic-btn-secondary"
          >
            <FileSpreadsheet className="w-4 h-4 text-[#15803D]" />
            Xuất Excel (.xlsx)
          </button>
        </div>
      </div>

      <form
        onSubmit={(e) => {
          e.preventDefault();
          void loadReport();
        }}
        className="civic-card p-5 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3"
      >
        <div>
          <label htmlFor="tbTuNgay" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Từ ngày
          </label>
          <input
            id="tbTuNgay"
            type="date"
            className="civic-input tabular-nums"
            value={tuNgay}
            onChange={(e) => setTuNgay(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="tbDenNgay" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Đến ngày
          </label>
          <input
            id="tbDenNgay"
            type="date"
            className="civic-input tabular-nums"
            value={denNgay}
            onChange={(e) => setDenNgay(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="cbbNganhBaoCao" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Ngành nghề
          </label>
          <select
            id="cbbNganhBaoCao"
            className="civic-input"
            value={maNganhNghe}
            onChange={(e) => setMaNganhNghe(e.target.value)}
          >
            <option value="">Tất cả ngành nghề</option>
            {nganhList.map((nn) => (
              <option key={nn.pkMaNganhNghe} value={String(nn.pkMaNganhNghe)}>
                {nn.sTenNganhNghe}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="cbbQuanBaoCao" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Quận / Huyện
          </label>
          <select
            id="cbbQuanBaoCao"
            className="civic-input"
            value={maQuanHuyen}
            onChange={(e) => setMaQuanHuyen(e.target.value)}
          >
            <option value="">Tất cả 30 địa bàn</option>
            {quanList.map((qh) => (
              <option key={qh.pkMaQuanHuyen} value={qh.pkMaQuanHuyen}>
                {qh.sTenQuanHuyen}
              </option>
            ))}
          </select>
        </div>
        <div className="flex items-end">
          <button id="btnLocBaoCao" type="submit" className="civic-btn-primary w-full">
            <Filter className="w-4 h-4" />
            Tổng hợp
          </button>
        </div>
      </form>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <section className="grid grid-cols-2 lg:grid-cols-6 gap-3">
        {[
          { label: "Tổng Cung (Hồ sơ)", value: data?.tongCung },
          { label: "Tổng Cầu (Chỉ tiêu)", value: data?.tongCau },
          { label: "Lượt ứng tuyển", value: data?.tongHoSoUngTuyen },
          { label: "Trúng tuyển", value: data?.tongTrungTuyen },
          { label: "DN đã xác minh", value: data?.tongDoanhNghiepDaXacMinh },
          { label: "Tin công khai", value: data?.tongTinCongKhai },
        ].map((item) => (
          <div key={item.label} className="civic-card p-4">
            <div className="text-xs font-semibold text-[#5F6C7B]">{item.label}</div>
            <div className="mt-1.5 text-xl font-bold text-[#192536] tabular-nums">
              {item.value !== undefined ? item.value.toLocaleString("vi-VN") : "—"}
            </div>
          </div>
        ))}
      </section>

      <section className="civic-card p-6 space-y-4">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <h2 className="text-base font-semibold text-[#192536] flex items-center gap-2">
            <BarChart3 className="w-4 h-4 text-[#215DA8]" />
            Biểu đồ so sánh Cung – Cầu theo Ngành nghề trọng điểm (SVG)
          </h2>
          <div className="flex items-center gap-4 text-xs text-[#5F6C7B]">
            <span className="inline-flex items-center gap-1.5">
              <span className="w-3 h-3 rounded-sm bg-[#215DA8]" /> Cầu chỉ tiêu
            </span>
            <span className="inline-flex items-center gap-1.5">
              <span className="w-3 h-3 rounded-sm bg-[#15803D]" /> Lượt ứng tuyển
            </span>
          </div>
        </div>

        {loading ? (
          <p className="text-sm text-[#5F6C7B]">Đang tổng hợp biểu đồ...</p>
        ) : (
          <div className="overflow-x-auto">
            <svg
              viewBox={`0 0 ${Math.max(640, chartIndustries.length * 90)} 240`}
              className="w-full min-w-[600px] h-60"
              role="img"
              aria-label="Biểu đồ so sánh nhu cầu tuyển dụng và lượt ứng tuyển theo ngành"
            >
              <line x1="40" y1="20" x2="40" y2="190" stroke="#DCE2E9" strokeWidth="1" />
              <line x1="40" y1="190" x2={Math.max(620, chartIndustries.length * 90 - 10)} y2="190" stroke="#DCE2E9" strokeWidth="1" />
              {chartIndustries.map((item, idx) => {
                const xBase = 60 + idx * 84;
                const hCau = Math.round((item.iTongCauChiTieu / maxBar) * 150);
                const hUt = Math.round((item.iTongLuotUngTuyen / maxBar) * 150);
                const shortName =
                  item.sTenNganhNghe.length > 13
                    ? `${item.sTenNganhNghe.slice(0, 12)}…`
                    : item.sTenNganhNghe;
                return (
                  <g key={item.pkMaNganhNghe}>
                    <rect x={xBase} y={190 - hCau} width="22" height={hCau} fill="#215DA8" rx="3" />
                    <text x={xBase + 11} y={185 - hCau} textAnchor="middle" fontSize="11" fill="#192536">
                      {item.iTongCauChiTieu}
                    </text>
                    <rect x={xBase + 26} y={190 - hUt} width="22" height={hUt} fill="#15803D" rx="3" />
                    <text x={xBase + 37} y={185 - hUt} textAnchor="middle" fontSize="11" fill="#15803D">
                      {item.iTongLuotUngTuyen}
                    </text>
                    <text x={xBase + 24} y="210" textAnchor="middle" fontSize="11" fill="#5F6C7B">
                      {shortName}
                    </text>
                  </g>
                );
              })}
            </svg>
          </div>
        )}
      </section>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <section className="civic-card overflow-hidden">
          <div className="px-5 py-4 border-b border-[#DCE2E9]">
            <h2 className="text-base font-semibold text-[#192536]">Bảng Cung – Cầu theo Ngành nghề</h2>
          </div>
          <div className="overflow-x-auto">
            <table id="dgvCungCauNganhNghe" className="civic-table">
              <thead>
                <tr>
                  <th>Ngành nghề</th>
                  <th className="text-right">Số tin</th>
                  <th className="text-right">Cầu chỉ tiêu</th>
                  <th className="text-right">Ứng tuyển</th>
                  <th className="text-right">Trúng tuyển</th>
                </tr>
              </thead>
              <tbody>
                {(data?.theoNganhNghe || []).map((row) => (
                  <tr key={row.pkMaNganhNghe}>
                    <td className="font-medium text-[#192536]">{row.sTenNganhNghe}</td>
                    <td className="text-right tabular-nums">{row.iSoTinTuyenDung}</td>
                    <td className="text-right tabular-nums">{row.iTongCauChiTieu}</td>
                    <td className="text-right tabular-nums">{row.iTongLuotUngTuyen}</td>
                    <td className="text-right tabular-nums font-semibold text-[#15803D]">{row.iSoTrungTuyen}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="civic-card overflow-hidden">
          <div className="px-5 py-4 border-b border-[#DCE2E9]">
            <h2 className="text-base font-semibold text-[#192536]">Bảng Cung – Cầu theo Quận / Huyện Hà Nội</h2>
          </div>
          <div className="overflow-x-auto max-h-[420px]">
            <table id="dgvCungCauQuanHuyen" className="civic-table">
              <thead>
                <tr>
                  <th>Quận / Huyện</th>
                  <th className="text-right">Cung HS</th>
                  <th className="text-right">Cầu chỉ tiêu</th>
                  <th className="text-right">Chênh lệch</th>
                  <th className="text-right">Trúng tuyển</th>
                </tr>
              </thead>
              <tbody>
                {(data?.theoQuanHuyen || []).map((row) => (
                  <tr key={row.pkMaQuanHuyen}>
                    <td className="font-medium text-[#192536]">
                      {row.sTenQuanHuyen}
                      <div className="text-xs text-[#5F6C7B]">{row.sKhuVuc}</div>
                    </td>
                    <td className="text-right tabular-nums">{row.iTongCung}</td>
                    <td className="text-right tabular-nums">{row.iTongCau}</td>
                    <td className="text-right tabular-nums">{row.iChenhLech}</td>
                    <td className="text-right tabular-nums font-semibold text-[#15803D]">{row.iSoTrungTuyen}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </div>
  );
}

export default function BaoCaoPage() {
  return (
    <RoleGate allowedRoles={[3, 4]}>
      <BaoCaoCungCauContent />
    </RoleGate>
  );
}
