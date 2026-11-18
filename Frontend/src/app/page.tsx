"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  ArrowRight,
  Briefcase,
  Building2,
  CheckCircle2,
  MapPin,
  Search,
  ShieldCheck,
  Users,
} from "lucide-react";
import { apiFetch, formatDateVN, formatSalaryRange } from "@/lib/apiClient";
import { useAuth } from "@/lib/authContext";
import { AlertBanner, StatusBadge } from "@/components/AppShell";
import type { DanhMucVaTongQuan, TimViecPage, TinTuyenDungView } from "@/types/models";

export default function HomePage() {
  const router = useRouter();
  const { user } = useAuth();

  const [danhMuc, setDanhMuc] = useState<DanhMucVaTongQuan | null>(null);
  const [tinMoi, setTinMoi] = useState<TinTuyenDungView[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [tuKhoa, setTuKhoa] = useState("");
  const [maNganhNghe, setMaNganhNghe] = useState("");
  const [maQuanHuyen, setMaQuanHuyen] = useState("");

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const [dmRes, tinRes] = await Promise.all([
          apiFetch<DanhMucVaTongQuan>("/api/tim-viec/danh-muc"),
          apiFetch<TimViecPage>("/api/tim-viec?page=0&size=6"),
        ]);
        if (!active) return;
        setDanhMuc(dmRes.duLieu);
        setTinMoi(tinRes.duLieu?.items || []);
      } catch (err) {
        if (!active) return;
        setError(err instanceof Error ? err.message : "Không thể tải dữ liệu sàn giao dịch.");
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => {
      active = false;
    };
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    const params = new URLSearchParams();
    if (tuKhoa.trim()) params.set("tuKhoa", tuKhoa.trim());
    if (maNganhNghe) params.set("maNganhNghe", maNganhNghe);
    if (maQuanHuyen) params.set("maQuanHuyen", maQuanHuyen);
    router.push(`/viec-lam${params.toString() ? `?${params.toString()}` : ""}`);
  };

  const tq = danhMuc?.tongQuan;

  return (
    <div className="space-y-8">
      <section className="civic-card p-6 md:p-8 bg-white">
        <div className="max-w-3xl space-y-2">
          <StatusBadge variant="info">Cổng dữ liệu chính thức · 30 Quận/Huyện Hà Nội</StatusBadge>
          <h1 className="text-2xl md:text-3xl font-bold text-[#192536] tracking-tight pt-1">
            Kết nối Cung – Cầu lao động thành phố Hà Nội
          </h1>
          <p className="text-sm md:text-base text-[#5F6C7B] leading-relaxed">
            Tra cứu việc làm đã qua kiểm duyệt của Trung tâm Dịch vụ việc làm Hà Nội, đăng ký hồ sơ ứng viên,
            cấp Giấy giới thiệu việc làm và theo dõi tiến trình phỏng vấn trực tuyến.
          </p>
        </div>

        <form onSubmit={handleSearch} className="mt-6 grid grid-cols-1 md:grid-cols-12 gap-3">
          <div className="md:col-span-5">
            <label htmlFor="tbTuKhoa" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Từ khóa công việc / vị trí
            </label>
            <input
              id="tbTuKhoa"
              type="text"
              className="civic-input"
              placeholder="Ví dụ: Kế toán, Kỹ sư Java, Nhân viên kinh doanh..."
              value={tuKhoa}
              onChange={(e) => setTuKhoa(e.target.value)}
            />
          </div>
          <div className="md:col-span-3">
            <label htmlFor="cbbNganhNghe" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Ngành nghề
            </label>
            <select
              id="cbbNganhNghe"
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
          <div className="md:col-span-2">
            <label htmlFor="cbbQuanHuyen" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
              Quận / Huyện
            </label>
            <select
              id="cbbQuanHuyen"
              className="civic-input"
              value={maQuanHuyen}
              onChange={(e) => setMaQuanHuyen(e.target.value)}
            >
              <option value="">Toàn Hà Nội</option>
              {(danhMuc?.quanHuyen || []).map((qh) => (
                <option key={qh.pkMaQuanHuyen} value={qh.pkMaQuanHuyen}>
                  {qh.sTenQuanHuyen}
                </option>
              ))}
            </select>
          </div>
          <div className="md:col-span-2 flex items-end">
            <button id="btnTimViec" type="submit" className="civic-btn-primary w-full">
              <Search className="w-4 h-4" />
              Tìm việc
            </button>
          </div>
        </form>
      </section>

      {error && <AlertBanner type="error" message={error} onClose={() => setError(null)} />}

      <section aria-label="Thống kê dữ liệu thực" className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="civic-card p-5">
          <div className="flex items-center justify-between text-xs font-semibold text-[#5F6C7B]">
            <span>Tin tuyển dụng công khai</span>
            <Briefcase className="w-4 h-4 text-[#215DA8]" />
          </div>
          <p className="mt-2 text-2xl font-bold text-[#192536] tabular-nums">
            {tq ? tq.soTinCongKhai.toLocaleString("vi-VN") : "—"}
          </p>
        </div>
        <div className="civic-card p-5">
          <div className="flex items-center justify-between text-xs font-semibold text-[#5F6C7B]">
            <span>Doanh nghiệp đã xác minh</span>
            <Building2 className="w-4 h-4 text-[#215DA8]" />
          </div>
          <p className="mt-2 text-2xl font-bold text-[#192536] tabular-nums">
            {tq ? tq.soDnXacMinh.toLocaleString("vi-VN") : "—"}
          </p>
        </div>
        <div className="civic-card p-5">
          <div className="flex items-center justify-between text-xs font-semibold text-[#5F6C7B]">
            <span>Hồ sơ ứng viên công khai</span>
            <Users className="w-4 h-4 text-[#215DA8]" />
          </div>
          <p className="mt-2 text-2xl font-bold text-[#192536] tabular-nums">
            {tq ? tq.soHoSoCongKhai.toLocaleString("vi-VN") : "—"}
          </p>
        </div>
        <div className="civic-card p-5">
          <div className="flex items-center justify-between text-xs font-semibold text-[#5F6C7B]">
            <span>Lượt kết nối trúng tuyển</span>
            <CheckCircle2 className="w-4 h-4 text-[#15803D]" />
          </div>
          <p className="mt-2 text-2xl font-bold text-[#15803D] tabular-nums">
            {tq ? tq.soTrungTuyen.toLocaleString("vi-VN") : "—"}
          </p>
        </div>
      </section>

      <section className="civic-card overflow-hidden">
        <div className="px-6 py-4 border-b border-[#DCE2E9] flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-lg font-semibold text-[#192536]">Tin tuyển dụng đang mở</h2>
            <p className="text-xs text-[#5F6C7B]">
              Chỉ hiển thị tin thuộc doanh nghiệp đã xác minh pháp lý, được Cán bộ DVVL duyệt và còn hạn nộp hồ sơ.
            </p>
          </div>
          <Link href="/viec-lam" className="civic-btn-secondary">
            Xem tất cả việc làm
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        <div className="hidden md:block overflow-x-auto">
          <table id="dgvTinMoi" className="civic-table">
            <thead>
              <tr>
                <th>Vị trí / Tiêu đề tuyển dụng</th>
                <th>Doanh nghiệp</th>
                <th>Địa bàn Hà Nội</th>
                <th className="whitespace-nowrap">Mức lương</th>
                <th className="whitespace-nowrap">Hạn nộp</th>
                <th className="text-right whitespace-nowrap">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={6} className="text-center text-[#5F6C7B] py-8">
                    Đang tải danh sách tin tuyển dụng...
                  </td>
                </tr>
              ) : tinMoi.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center text-[#5F6C7B] py-8">
                    Hiện chưa có tin tuyển dụng công khai phù hợp.
                  </td>
                </tr>
              ) : (
                tinMoi.map((tin) => (
                  <tr key={tin.pkMaTin}>
                    <td>
                      <Link
                        href={`/viec-lam/${tin.pkMaTin}`}
                        className="font-semibold text-[#215DA8] hover:underline"
                      >
                        {tin.sTieuDe}
                      </Link>
                      <div className="text-xs text-[#5F6C7B] mt-0.5 flex flex-wrap gap-x-2">
                        <span>{tin.sViTriChucDanh}</span>
                        <span>({tin.sTenNganhNghe})</span>
                        <span>
                          Tuyển <span className="tabular-nums font-medium">{tin.iSoLuongTuyen}</span>
                        </span>
                      </div>
                    </td>
                    <td className="font-medium text-[#192536]">{tin.sTenDoanhNghiep}</td>
                    <td className="whitespace-nowrap">
                      <span className="inline-flex items-center gap-1 text-[#192536]">
                        <MapPin className="w-3.5 h-3.5 text-[#5F6C7B]" />
                        {tin.sTenQuanLamViec}
                      </span>
                    </td>
                    <td className="tabular-nums font-medium text-[#192536] whitespace-nowrap">
                      {formatSalaryRange(tin.fLuongToiThieu, tin.fLuongToiDa)}
                    </td>
                    <td className="tabular-nums text-[#5F6C7B] whitespace-nowrap">{formatDateVN(tin.dHanNopHoSo)}</td>
                    <td className="text-right whitespace-nowrap">
                      <Link
                        href={`/viec-lam/${tin.pkMaTin}`}
                        className="text-sm font-semibold text-[#215DA8] hover:underline whitespace-nowrap"
                      >
                        Chi tiết
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div className="md:hidden divide-y divide-[#DCE2E9]">
          {loading ? (
            <div className="p-6 text-center text-sm text-[#5F6C7B]">Đang tải danh sách tin tuyển dụng...</div>
          ) : tinMoi.length === 0 ? (
            <div className="p-6 text-center text-sm text-[#5F6C7B]">Hiện chưa có tin tuyển dụng công khai phù hợp.</div>
          ) : (
            tinMoi.map((tin) => (
              <div key={tin.pkMaTin} className="p-4 space-y-1.5">
                <Link href={`/viec-lam/${tin.pkMaTin}`} className="font-semibold text-[#215DA8] hover:underline block">
                  {tin.sTieuDe}
                </Link>
                <div className="text-sm font-medium text-[#192536]">{tin.sTenDoanhNghiep}</div>
                <div className="text-xs text-[#5F6C7B] flex flex-wrap gap-x-3 gap-y-1">
                  <span>{tin.sTenQuanLamViec}</span>
                  <span className="tabular-nums font-semibold text-[#192536]">
                    {formatSalaryRange(tin.fLuongToiThieu, tin.fLuongToiDa)}
                  </span>
                  <span className="tabular-nums">Hạn: {formatDateVN(tin.dHanNopHoSo)}</span>
                </div>
              </div>
            ))
          )}
        </div>
      </section>

      <section className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="civic-card p-5 space-y-2">
          <h3 className="text-base font-semibold text-[#192536]">Dành cho Người tìm việc</h3>
          <p className="text-sm text-[#5F6C7B] leading-relaxed">
            Kê khai hồ sơ điện tử, tải CV PDF, xuất Phiếu đăng ký tìm việc làm chuẩn mẫu A4 và theo dõi trạng thái phỏng vấn theo thời gian thực.
          </p>
          <div className="pt-2">
            <Link
              href={user?.fkMaVaiTro === 1 ? "/ung-vien/ho-so" : "/dang-ky?vaiTro=1"}
              className="text-sm font-semibold text-[#215DA8] hover:underline inline-flex items-center gap-1"
            >
              {user?.fkMaVaiTro === 1 ? "Mở hồ sơ của tôi" : "Đăng ký tài khoản ứng viên"}
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>

        <div className="civic-card p-5 space-y-2">
          <h3 className="text-base font-semibold text-[#192536]">Dành cho Doanh nghiệp</h3>
          <p className="text-sm text-[#5F6C7B] leading-relaxed">
            Hoàn thiện hồ sơ pháp lý ĐKKD, đăng tin tuyển dụng, sàng lọc ứng viên và tra cứu ngân hàng nhân lực có che thông tin nhạy cảm.
          </p>
          <div className="pt-2">
            <Link
              href={user?.fkMaVaiTro === 2 ? "/doanh-nghiep" : "/dang-ky?vaiTro=2"}
              className="text-sm font-semibold text-[#215DA8] hover:underline inline-flex items-center gap-1"
            >
              {user?.fkMaVaiTro === 2 ? "Vào không gian doanh nghiệp" : "Đăng ký nhà tuyển dụng"}
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>

        <div className="civic-card p-5 space-y-2">
          <h3 className="text-base font-semibold text-[#192536] flex items-center gap-1.5">
            <ShieldCheck className="w-4 h-4 text-[#215DA8]" />
            Điều phối Trung tâm DVVL
          </h3>
          <p className="text-sm text-[#5F6C7B] leading-relaxed">
            Thẩm định pháp lý doanh nghiệp, duyệt tin, cấp Giấy giới thiệu việc làm có mã QR và tổng hợp báo cáo cung-cầu Sở LĐ-TB&amp;XH.
          </p>
          <div className="pt-2">
            <Link
              href={user && (user.fkMaVaiTro === 3 || user.fkMaVaiTro === 4) ? user.duongDanDich || "/can-bo" : "/dang-nhap"}
              className="text-sm font-semibold text-[#215DA8] hover:underline inline-flex items-center gap-1"
            >
              {user && (user.fkMaVaiTro === 3 || user.fkMaVaiTro === 4) ? "Mở phân hệ nghiệp vụ" : "Đăng nhập cổng cán bộ"}
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
