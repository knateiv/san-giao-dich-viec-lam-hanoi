"use client";

import React, { Suspense, useCallback, useEffect, useState } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Briefcase, Clock, Filter, MapPin, RotateCcw, Search } from "lucide-react";
import { apiFetch, formatDateVN, formatSalaryRange } from "@/lib/apiClient";
import { AlertBanner, StatusBadge } from "@/components/AppShell";
import type { DanhMucVaTongQuan, TimViecPage, TinTuyenDungView } from "@/types/models";

function ViecLamContent() {
  const searchParams = useSearchParams();

  const [danhMuc, setDanhMuc] = useState<DanhMucVaTongQuan | null>(null);
  const [items, setItems] = useState<TinTuyenDungView[]>([]);
  const [totalItems, setTotalItems] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [elapsedMs, setElapsedMs] = useState<number | null>(null);

  const [tuKhoa, setTuKhoa] = useState(searchParams.get("tuKhoa") || "");
  const [maNganhNghe, setMaNganhNghe] = useState(searchParams.get("maNganhNghe") || "");
  const [maQuanHuyen, setMaQuanHuyen] = useState(searchParams.get("maQuanHuyen") || "");
  const [luongToiThieu, setLuongToiThieu] = useState("");
  const [luongToiDa, setLuongToiDa] = useState("");
  const [hocVan, setHocVan] = useState("");
  const [hinhThuc, setHinhThuc] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    apiFetch<DanhMucVaTongQuan>("/api/tim-viec/danh-muc")
      .then((res) => setDanhMuc(res.duLieu))
      .catch(() => {});
  }, []);

  const fetchJobs = useCallback(
    async (
      targetPage: number,
      filters?: {
        tuKhoa: string;
        maNganhNghe: string;
        maQuanHuyen: string;
        luongToiThieu: string;
        luongToiDa: string;
        hocVan: string;
        hinhThuc: string;
      }
    ) => {
      const f = filters || {
        tuKhoa,
        maNganhNghe,
        maQuanHuyen,
        luongToiThieu,
        luongToiDa,
        hocVan,
        hinhThuc,
      };
      setError(null);
      setLoading(true);
      try {
        const params = new URLSearchParams();
        if (f.tuKhoa.trim()) params.set("tuKhoa", f.tuKhoa.trim());
        if (f.maNganhNghe) params.set("maNganhNghe", f.maNganhNghe);
        if (f.maQuanHuyen) params.set("maQuanHuyen", f.maQuanHuyen);
        if (f.luongToiThieu.trim()) params.set("luongToiThieu", f.luongToiThieu.trim());
        if (f.luongToiDa.trim()) params.set("luongToiDa", f.luongToiDa.trim());
        if (f.hocVan) params.set("hocVan", f.hocVan);
        if (f.hinhThuc) params.set("hinhThuc", f.hinhThuc);
        params.set("page", String(targetPage));
        params.set("size", "10");

        const res = await apiFetch<TimViecPage>(`/api/tim-viec?${params.toString()}`);
        setItems(res.duLieu.items || []);
        setTotalItems(res.duLieu.totalItems || 0);
        setTotalPages(res.duLieu.totalPages || 0);
        setPage(res.duLieu.page || 0);
        setElapsedMs(res.thoiGianXuLyMs ?? null);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Tra cứu việc làm thất bại.");
      } finally {
        setLoading(false);
      }
    },
    [tuKhoa, maNganhNghe, maQuanHuyen, luongToiThieu, luongToiDa, hocVan, hinhThuc]
  );

  useEffect(() => {
    void fetchJobs(0, {
      tuKhoa: searchParams.get("tuKhoa") || "",
      maNganhNghe: searchParams.get("maNganhNghe") || "",
      maQuanHuyen: searchParams.get("maQuanHuyen") || "",
      luongToiThieu: "",
      luongToiDa: "",
      hocVan: "",
      hinhThuc: "",
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    void fetchJobs(0);
  };

  const handleReset = () => {
    const empty = {
      tuKhoa: "",
      maNganhNghe: "",
      maQuanHuyen: "",
      luongToiThieu: "",
      luongToiDa: "",
      hocVan: "",
      hinhThuc: "",
    };
    setTuKhoa("");
    setMaNganhNghe("");
    setMaQuanHuyen("");
    setLuongToiThieu("");
    setLuongToiDa("");
    setHocVan("");
    setHinhThuc("");
    void fetchJobs(0, empty);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-[#192536]">Tra cứu việc làm tại Hà Nội</h1>
          <p className="text-sm text-[#5F6C7B] mt-1">
            Lọc kết hợp nhiều tiêu chí (AND) trên các tin tuyển dụng đã qua thẩm định và còn hạn nộp hồ sơ.
          </p>
        </div>
        <div className="flex items-center gap-2 text-xs text-[#5F6C7B]">
          <StatusBadge variant="info">
            <span className="tabular-nums">{totalItems} tin phù hợp</span>
          </StatusBadge>
          {elapsedMs !== null && (
            <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-white border border-[#DCE2E9] tabular-nums">
              <Clock className="w-3.5 h-3.5 text-[#215DA8]" />
              {elapsedMs} ms
            </span>
          )}
        </div>
      </div>

      <form onSubmit={handleSubmit} className="civic-card p-5 space-y-4">
        <div className="flex items-center gap-2 text-sm font-semibold text-[#192536]">
          <Filter className="w-4 h-4 text-[#215DA8]" />
          Bộ lọc tìm kiếm đa tiêu chí
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <div className="md:col-span-2">
            <label htmlFor="tbTuKhoaLoc" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Từ khóa (tiêu đề, vị trí, doanh nghiệp)
            </label>
            <input
              id="tbTuKhoaLoc"
              type="text"
              className="civic-input"
              placeholder="Ví dụ: Kế toán, Java, Bán hàng..."
              value={tuKhoa}
              onChange={(e) => setTuKhoa(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="cbbNganhNgheLoc" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Ngành nghề chuẩn
            </label>
            <select
              id="cbbNganhNgheLoc"
              className="civic-input"
              value={maNganhNghe}
              onChange={(e) => setMaNganhNghe(e.target.value)}
            >
              <option value="">Tất cả ngành nghề</option>
              {(danhMuc?.nganhNghe || []).map((nn) => (
                <option key={nn.pkMaNganhNghe} value={String(nn.pkMaNganhNghe)}>
                  {nn.sTenNganhNghe}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="cbbQuanHuyenLoc" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Quận / Huyện làm việc (30 địa bàn)
            </label>
            <select
              id="cbbQuanHuyenLoc"
              className="civic-input"
              value={maQuanHuyen}
              onChange={(e) => setMaQuanHuyen(e.target.value)}
            >
              <option value="">Tất cả 30 Quận/Huyện</option>
              {(danhMuc?.quanHuyen || []).map((qh) => (
                <option key={qh.pkMaQuanHuyen} value={qh.pkMaQuanHuyen}>
                  {qh.sTenQuanHuyen}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
          <div>
            <label htmlFor="tbLuongTu" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Lương tối thiểu (VNĐ)
            </label>
            <input
              id="tbLuongTu"
              type="number"
              className="civic-input tabular-nums"
              placeholder="Ví dụ: 10000000"
              value={luongToiThieu}
              onChange={(e) => setLuongToiThieu(e.target.value)}
            />
          </div>
          <div>
            <label htmlFor="tbLuongDen" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Lương tối đa (VNĐ)
            </label>
            <input
              id="tbLuongDen"
              type="number"
              className="civic-input tabular-nums"
              placeholder="Ví dụ: 30000000"
              value={luongToiDa}
              onChange={(e) => setLuongToiDa(e.target.value)}
            />
          </div>
          <div>
            <label htmlFor="cbbHocVanLoc" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Yêu cầu học vấn
            </label>
            <select
              id="cbbHocVanLoc"
              className="civic-input"
              value={hocVan}
              onChange={(e) => setHocVan(e.target.value)}
            >
              <option value="">Không giới hạn</option>
              <option value="THPT">THPT / Phổ thông</option>
              <option value="Trung cấp">Trung cấp</option>
              <option value="Cao đẳng">Cao đẳng</option>
              <option value="Đại học">Đại học</option>
              <option value="Sau đại học">Sau đại học</option>
            </select>
          </div>
          <div>
            <label htmlFor="cbbHinhThucLoc" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Hình thức làm việc
            </label>
            <select
              id="cbbHinhThucLoc"
              className="civic-input"
              value={hinhThuc}
              onChange={(e) => setHinhThuc(e.target.value)}
            >
              <option value="">Tất cả hình thức</option>
              <option value="Toàn thời gian">Toàn thời gian</option>
              <option value="Bán thời gian">Bán thời gian</option>
              <option value="Theo ca">Theo ca</option>
              <option value="Thực tập">Thực tập</option>
            </select>
          </div>
          <div className="flex items-end gap-2">
            <button id="btnApDungLoc" type="submit" className="civic-btn-primary flex-1">
              <Search className="w-4 h-4" />
              Lọc tin
            </button>
            <button id="btnXoaBoLoc" type="button" onClick={handleReset} className="civic-btn-secondary px-3" title="Xóa bộ lọc">
              <RotateCcw className="w-4 h-4" />
            </button>
          </div>
        </div>
      </form>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />

      <section className="civic-card overflow-hidden">
        <div className="overflow-x-auto">
          <table id="dgvKetQuaViecLam" className="civic-table">
            <thead>
              <tr>
                <th>Vị trí / Tiêu đề tuyển dụng</th>
                <th>Doanh nghiệp</th>
                <th>Ngành nghề &amp; Địa bàn</th>
                <th>Mức lương</th>
                <th>Hạn nộp</th>
                <th className="text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={6} className="text-center py-8 text-[#5F6C7B]">
                    Đang truy vấn dữ liệu việc làm...
                  </td>
                </tr>
              ) : items.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center py-8 text-[#5F6C7B]">
                    Không tìm thấy tin tuyển dụng phù hợp với bộ lọc đã chọn.
                  </td>
                </tr>
              ) : (
                items.map((tin) => (
                  <tr key={tin.pkMaTin}>
                    <td>
                      <div className="flex flex-wrap items-center gap-2">
                        <Link
                          href={`/viec-lam/${tin.pkMaTin}`}
                          className="font-semibold text-[#215DA8] hover:underline"
                        >
                          {tin.sTieuDe}
                        </Link>
                        {tin.daNop && <StatusBadge variant="success">Đã nộp</StatusBadge>}
                      </div>
                      <div className="text-xs text-[#5F6C7B] mt-0.5 flex flex-wrap items-center gap-2">
                        <span className="inline-flex items-center gap-1">
                          <Briefcase className="w-3.5 h-3.5" />
                          {tin.sViTriChucDanh}
                        </span>
                        <span>{tin.sHinhThucLamViec || "Toàn thời gian"}</span>
                        <span className="tabular-nums">Tuyển {tin.iSoLuongTuyen}</span>
                      </div>
                    </td>
                    <td className="font-medium text-[#192536]">{tin.sTenDoanhNghiep}</td>
                    <td>
                      <div className="text-sm text-[#192536]">{tin.sTenNganhNghe}</div>
                      <div className="text-xs text-[#5F6C7B] inline-flex items-center gap-1 mt-0.5">
                        <MapPin className="w-3 h-3" />
                        {tin.sTenQuanLamViec}
                      </div>
                    </td>
                    <td className="tabular-nums font-semibold text-[#192536]">
                      {formatSalaryRange(tin.fLuongToiThieu, tin.fLuongToiDa)}
                    </td>
                    <td className="tabular-nums text-[#5F6C7B]">{formatDateVN(tin.dHanNopHoSo)}</td>
                    <td className="text-right">
                      <Link href={`/viec-lam/${tin.pkMaTin}`} className="civic-btn-secondary py-1.5 px-3 text-xs">
                        Xem &amp; Ứng tuyển
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div id="pnPhanTrangViecLam" className="px-4 py-3 border-t border-[#DCE2E9] flex items-center justify-between text-sm">
            <span className="text-[#5F6C7B] tabular-nums">
              Trang {page + 1} / {totalPages}
            </span>
            <div className="flex gap-2">
              <button
                type="button"
                disabled={page <= 0}
                onClick={() => void fetchJobs(page - 1)}
                className="civic-btn-secondary py-1.5 px-3 text-xs"
              >
                Trang trước
              </button>
              <button
                type="button"
                disabled={page + 1 >= totalPages}
                onClick={() => void fetchJobs(page + 1)}
                className="civic-btn-secondary py-1.5 px-3 text-xs"
              >
                Trang sau
              </button>
            </div>
          </div>
        )}
      </section>
    </div>
  );
}

export default function ViecLamPage() {
  return (
    <Suspense fallback={<div className="civic-card p-6 text-sm text-[#5F6C7B]">Đang tải trang tìm việc...</div>}>
      <ViecLamContent />
    </Suspense>
  );
}
