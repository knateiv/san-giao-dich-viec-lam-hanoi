"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { ArrowLeft, Briefcase, Building2, Calendar, CheckCircle2, MapPin, Send } from "lucide-react";
import { apiFetch, formatDateVN, formatSalaryRange } from "@/lib/apiClient";
import { useAuth } from "@/lib/authContext";
import { AlertBanner, StatusBadge } from "@/components/AppShell";
import type { TinTuyenDungView, UngTuyenView } from "@/types/models";

export default function ChiTietViecLamPage() {
  const params = useParams<{ maTin: string }>();
  const maTin = params?.maTin;
  const { user } = useAuth();

  const [tin, setTin] = useState<TinTuyenDungView | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [ghiChu, setGhiChu] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const loadDetail = async () => {
    if (!maTin) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiFetch<TinTuyenDungView>(`/api/tim-viec/${encodeURIComponent(maTin)}`);
      setTin(res.duLieu);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không tải được chi tiết tin tuyển dụng.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadDetail();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [maTin]);

  const handleApply = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!tin) return;
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const res = await apiFetch<UngTuyenView>("/api/ung-tuyen", {
        method: "POST",
        body: JSON.stringify({
          fkMaTin: tin.pkMaTin,
          sGhiChu: ghiChu.trim() || "Ứng viên nộp hồ sơ trực tuyến qua Sàn DVVL Hà Nội.",
        }),
      });
      setSuccess(res.thongBao);
      await loadDetail();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Nộp hồ sơ ứng tuyển thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="civic-card p-8 text-sm text-[#5F6C7B]">Đang tải thông tin tin tuyển dụng...</div>;
  }

  if (!tin) {
    return (
      <div className="max-w-2xl mx-auto space-y-4">
        <AlertBanner type="error" message={error || "Tin tuyển dụng không tồn tại."} />
        <Link href="/viec-lam" className="civic-btn-secondary">
          <ArrowLeft className="w-4 h-4" />
          Quay lại danh sách việc làm
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <Link href="/viec-lam" className="inline-flex items-center gap-1.5 text-sm font-medium text-[#215DA8] hover:underline">
        <ArrowLeft className="w-4 h-4" />
        Danh sách việc làm Hà Nội
      </Link>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        <div className="lg:col-span-8 space-y-6">
          <section className="civic-card p-6 space-y-4">
            <div className="flex flex-wrap items-center gap-2">
              <StatusBadge variant="info">{tin.sTenNganhNghe}</StatusBadge>
              <StatusBadge variant={tin.iTrangThaiDuyet === 1 ? "success" : "warning"}>
                {tin.tenTrangThaiDuyet}
              </StatusBadge>
              {tin.daNop && <StatusBadge variant="success">Bạn đã nộp hồ sơ</StatusBadge>}
            </div>

            <h1 className="text-2xl font-bold text-[#192536]">{tin.sTieuDe}</h1>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm text-[#5F6C7B] pt-2 border-t border-[#DCE2E9]">
              <div className="flex items-center gap-2">
                <Building2 className="w-4 h-4 text-[#215DA8]" />
                <span className="font-medium text-[#192536]">{tin.sTenDoanhNghiep}</span>
              </div>
              <div className="flex items-center gap-2">
                <MapPin className="w-4 h-4 text-[#215DA8]" />
                <span>
                  {tin.sDiaChiCuThe ? `${tin.sDiaChiCuThe}, ` : ""}
                  {tin.sTenQuanLamViec}
                </span>
              </div>
              <div className="flex items-center gap-2">
                <Briefcase className="w-4 h-4 text-[#215DA8]" />
                <span>
                  {tin.sViTriChucDanh} · {tin.sHinhThucLamViec || "Toàn thời gian"}
                </span>
              </div>
              <div className="flex items-center gap-2 tabular-nums">
                <Calendar className="w-4 h-4 text-[#215DA8]" />
                <span>Hạn nộp: {formatDateVN(tin.dHanNopHoSo)}</span>
              </div>
            </div>
          </section>

          <section className="civic-card p-6 space-y-5">
            <div>
              <h2 className="text-base font-semibold text-[#192536]">Mô tả công việc</h2>
              <p className="mt-2 text-sm text-[#192536] whitespace-pre-line leading-relaxed">
                {tin.sMoTaCongViec || "Chưa bổ sung mô tả chi tiết."}
              </p>
            </div>
            <div className="pt-4 border-t border-[#DCE2E9]">
              <h2 className="text-base font-semibold text-[#192536]">Yêu cầu ứng viên</h2>
              <ul className="mt-2 space-y-1.5 text-sm text-[#192536]">
                <li>Trình độ học vấn: {tin.sYeuCauHocVan || "Không yêu cầu"}</li>
                <li>Kinh nghiệm: {tin.sYeuCauKinhNghiem || "Không yêu cầu"}</li>
                <li>Cấp bậc: {tin.sCapBac || "Nhân viên"}</li>
              </ul>
            </div>
            <div className="pt-4 border-t border-[#DCE2E9]">
              <h2 className="text-base font-semibold text-[#192536]">Chế độ đãi ngộ</h2>
              <p className="mt-2 text-sm text-[#192536] whitespace-pre-line leading-relaxed">
                {tin.sCheDoDaiNgo || "Theo quy định Luật Lao động hiện hành."}
              </p>
            </div>
          </section>
        </div>

        <aside className="lg:col-span-4 space-y-4">
          <div className="civic-card p-6 space-y-4">
            <div>
              <div className="text-xs font-semibold text-[#5F6C7B]">Mức lương công bố</div>
              <div className="text-xl font-bold text-[#215DA8] tabular-nums mt-1">
                {formatSalaryRange(tin.fLuongToiThieu, tin.fLuongToiDa)}
              </div>
            </div>

            <div className="text-sm text-[#5F6C7B] space-y-1 pt-3 border-t border-[#DCE2E9]">
              <div>
                Số lượng tuyển: <span className="font-semibold text-[#192536] tabular-nums">{tin.iSoLuongTuyen} người</span>
              </div>
              <div>
                Hồ sơ đã nộp: <span className="font-semibold text-[#192536] tabular-nums">{tin.soHoSoUngTuyen} lượt</span>
              </div>
              <div>
                Mã tin: <span className="font-mono text-xs text-[#192536]">{tin.pkMaTin}</span>
              </div>
            </div>

            {!user ? (
              <div className="pt-3 border-t border-[#DCE2E9] space-y-3">
                <p className="text-xs text-[#5F6C7B]">
                  Đăng nhập bằng tài khoản Người tìm việc để ứng tuyển 1 chạm bằng hồ sơ điện tử và CV đã tải lên.
                </p>
                <Link href="/dang-nhap" className="civic-btn-primary w-full">
                  Đăng nhập để ứng tuyển
                </Link>
              </div>
            ) : user.fkMaVaiTro !== 1 ? (
              <div className="pt-3 border-t border-[#DCE2E9] text-xs text-[#5F6C7B]">
                Chỉ tài khoản vai trò <strong>Người tìm việc</strong> mới thực hiện nộp hồ sơ ứng tuyển.
              </div>
            ) : tin.daNop ? (
              <div className="pt-3 border-t border-[#DCE2E9] space-y-3">
                <div className="flex items-center gap-2 text-sm font-semibold text-[#15803D]">
                  <CheckCircle2 className="w-4 h-4" />
                  Hồ sơ của bạn đã được gửi tới doanh nghiệp
                </div>
                <Link href="/ung-vien/ung-tuyen" className="civic-btn-secondary w-full">
                  Theo dõi lịch sử ứng tuyển
                </Link>
              </div>
            ) : (
              <form onSubmit={handleApply} className="pt-3 border-t border-[#DCE2E9] space-y-3">
                <div>
                  <label htmlFor="tbGhiChuUngTuyen" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                    Lời nhắn tới nhà tuyển dụng
                  </label>
                  <textarea
                    id="tbGhiChuUngTuyen"
                    rows={3}
                    className="civic-input"
                    placeholder="Giới thiệu ngắn gọn kinh nghiệm phù hợp..."
                    value={ghiChu}
                    onChange={(e) => setGhiChu(e.target.value)}
                  />
                </div>
                <button id="btnNopHoSo" type="submit" disabled={submitting} className="civic-btn-primary w-full">
                  <Send className="w-4 h-4" />
                  {submitting ? "Đang nộp hồ sơ..." : "Ứng tuyển 1 chạm"}
                </button>
              </form>
            )}
          </div>
        </aside>
      </div>
    </div>
  );
}
