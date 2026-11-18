"use client";

import React, { useCallback, useEffect, useState } from "react";
import { MapPin, Search, Send, Shield, UserCheck } from "lucide-react";
import { apiFetch, formatSalaryRange } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { DanhMucVaTongQuan, QuanHuyen, TinTuyenDungView, UngVienCongKhaiView } from "@/types/models";

function TimUngVienContent() {
  const [quanList, setQuanList] = useState<QuanHuyen[]>([]);
  const [myJobs, setMyJobs] = useState<TinTuyenDungView[]>([]);
  const [candidates, setCandidates] = useState<UngVienCongKhaiView[]>([]);

  const [tuKhoa, setTuKhoa] = useState("");
  const [maQuanHuyen, setMaQuanHuyen] = useState("");
  const [hocVan, setHocVan] = useState("");

  const [inviteTarget, setInviteTarget] = useState<UngVienCongKhaiView | null>(null);
  const [fkMaTin, setFkMaTin] = useState("");
  const [loiNhan, setLoiNhan] = useState("");

  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const searchCandidates = useCallback(async (kw: string, qh: string, hv: string) => {
    setError(null);
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (kw.trim()) params.set("tuKhoa", kw.trim());
      if (qh) params.set("maQuanHuyen", qh);
      if (hv) params.set("hocVan", hv);
      const res = await apiFetch<UngVienCongKhaiView[]>(`/api/tim-viec/ung-vien-cong-khai?${params.toString()}`);
      setCandidates(res.duLieu || []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không tải được danh sách ứng viên công khai.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const [dmRes, tinRes] = await Promise.all([
          apiFetch<DanhMucVaTongQuan>("/api/tim-viec/danh-muc"),
          apiFetch<TinTuyenDungView[]>("/api/tin-tuyen-dung/cua-toi"),
        ]);
        if (!active) return;
        setQuanList(dmRes.duLieu.quanHuyen || []);
        const approved = (tinRes.duLieu || []).filter((t) => t.iTrangThaiDuyet === 1 && !t.bDaDong);
        setMyJobs(approved);
        setFkMaTin(approved[0]?.pkMaTin || "");
        await searchCandidates("", "", "");
      } catch (err) {
        if (active) setError(err instanceof Error ? err.message : "Không tải được dữ liệu.");
      }
    })();
    return () => {
      active = false;
    };
  }, [searchCandidates]);

  const handleInvite = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inviteTarget || !fkMaTin) return;
    setError(null);
    setSuccess(null);
    setSending(true);
    try {
      const res = await apiFetch<void>("/api/tim-viec/moi-ung-tuyen", {
        method: "POST",
        body: JSON.stringify({
          fkMaUngVien: inviteTarget.pkMaUngVien,
          fkMaTin,
          loiNhan: loiNhan.trim() || "Trân trọng mời ứng viên tham gia dự tuyển vị trí phù hợp.",
        }),
      });
      setSuccess(res.thongBao);
      setInviteTarget(null);
      setLoiNhan("");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Gửi lời mời ứng tuyển thất bại.");
    } finally {
      setSending(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[#192536]">Ngân hàng Hồ sơ Ứng viên Công khai</h1>
          <p className="text-sm text-[#5F6C7B] mt-1">
            Số CCCD, SĐT và Email được hệ thống tự động che để bảo vệ dữ liệu cá nhân theo quy định (chỉ hiện đầy đủ khi ứng viên chủ động nộp hồ sơ).
          </p>
        </div>
        <StatusBadge variant="info">
          <Shield className="w-3.5 h-3.5 mr-1" />
          Bảo vệ dữ liệu cá nhân
        </StatusBadge>
      </div>

      <form
        onSubmit={(e) => {
          e.preventDefault();
          void searchCandidates(tuKhoa, maQuanHuyen, hocVan);
        }}
        className="civic-card p-5 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3"
      >
        <div>
          <label htmlFor="tbTuKhoaUv" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Kỹ năng / Vị trí mong muốn
          </label>
          <input
            id="tbTuKhoaUv"
            type="text"
            className="civic-input"
            placeholder="Ví dụ: Kế toán, Java, Tiếng Anh..."
            value={tuKhoa}
            onChange={(e) => setTuKhoa(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="cbbQuanUv" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Quận / Huyện cư trú
          </label>
          <select
            id="cbbQuanUv"
            className="civic-input"
            value={maQuanHuyen}
            onChange={(e) => setMaQuanHuyen(e.target.value)}
          >
            <option value="">Tất cả địa bàn Hà Nội</option>
            {quanList.map((qh) => (
              <option key={qh.pkMaQuanHuyen} value={qh.pkMaQuanHuyen}>
                {qh.sTenQuanHuyen}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="tbHocVanUv" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Chuyên môn / Bằng cấp
          </label>
          <input
            id="tbHocVanUv"
            type="text"
            className="civic-input"
            placeholder="Ví dụ: Đại học, Kỹ sư..."
            value={hocVan}
            onChange={(e) => setHocVan(e.target.value)}
          />
        </div>
        <div className="flex items-end">
          <button id="btnTimUngVien" type="submit" className="civic-btn-primary w-full">
            <Search className="w-4 h-4" />
            Tìm ứng viên
          </button>
        </div>
      </form>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      {inviteTarget && (
        <form onSubmit={handleInvite} className="civic-card p-5 bg-[#ECF3FC] border-[#BFD7F5] space-y-3">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-semibold text-[#192536]">
              Gửi lời mời ứng tuyển tới {inviteTarget.sHoTen} ({inviteTarget.pkMaUngVien})
            </h2>
            <button type="button" onClick={() => setInviteTarget(null)} className="text-xs font-semibold text-[#5F6C7B]">
              Đóng
            </button>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label htmlFor="cbbTinMoiUv" className="block text-xs font-semibold text-[#192536] mb-1">
                Vị trí tuyển dụng đang mở
              </label>
              <select
                id="cbbTinMoiUv"
                className="civic-input"
                value={fkMaTin}
                onChange={(e) => setFkMaTin(e.target.value)}
                required
              >
                {myJobs.length === 0 ? (
                  <option value="">Không có tin đã duyệt đang mở</option>
                ) : (
                  myJobs.map((t) => (
                    <option key={t.pkMaTin} value={t.pkMaTin}>
                      {t.pkMaTin} — {t.sTieuDe}
                    </option>
                  ))
                )}
              </select>
            </div>
            <div>
              <label htmlFor="tbLoiNhanMoi" className="block text-xs font-semibold text-[#192536] mb-1">
                Lời nhắn mời dự tuyển
              </label>
              <input
                id="tbLoiNhanMoi"
                type="text"
                className="civic-input"
                placeholder="Mời ứng viên tham gia ứng tuyển..."
                value={loiNhan}
                onChange={(e) => setLoiNhan(e.target.value)}
              />
            </div>
          </div>
          <button type="submit" disabled={sending || myJobs.length === 0} className="civic-btn-primary text-xs">
            <Send className="w-3.5 h-3.5" />
            {sending ? "Đang gửi lời mời..." : "Gửi lời mời ứng tuyển"}
          </button>
        </form>
      )}

      <section className="civic-card overflow-hidden">
        <div className="overflow-x-auto">
          <table id="dgvNganHangUngVien" className="civic-table">
            <thead>
              <tr>
                <th>Ứng viên &amp; Độ tuổi</th>
                <th>Chuyên môn &amp; Vị trí mong muốn</th>
                <th>Thông tin liên hệ (Đã che)</th>
                <th>Lương kỳ vọng</th>
                <th className="text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={5} className="text-center py-8 text-[#5F6C7B]">
                    Đang tải ngân hàng ứng viên...
                  </td>
                </tr>
              ) : candidates.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center py-8 text-[#5F6C7B]">
                    Không tìm thấy hồ sơ ứng viên công khai phù hợp.
                  </td>
                </tr>
              ) : (
                candidates.map((uv) => (
                  <tr key={uv.pkMaUngVien}>
                    <td>
                      <div className="font-semibold text-[#192536]">{uv.sHoTen}</div>
                      <div className="text-xs text-[#5F6C7B] mt-0.5 flex flex-wrap items-center gap-1.5">
                        <span>{uv.sGioiTinh} · <span className="tabular-nums">{uv.iDoTuoi} tuổi</span></span>
                        <span className="inline-flex items-center gap-0.5">
                          <MapPin className="w-3 h-3" />
                          {uv.sTenQuanCuTru}
                        </span>
                      </div>
                    </td>
                    <td>
                      <div className="text-sm font-medium text-[#192536]">{uv.sViTriMongMuon || "Linh hoạt"}</div>
                      <div className="text-xs text-[#5F6C7B] mt-0.5">
                        {uv.sTrinhDoChuyenMon || "—"} {uv.sKyNangTinHoc ? `· ${uv.sKyNangTinHoc}` : ""}
                      </div>
                    </td>
                    <td className="font-mono text-xs text-[#5F6C7B] tabular-nums space-y-0.5">
                      <div>CCCD: {uv.sSoCCCDChe}</div>
                      <div>SĐT: {uv.sSDTChe}</div>
                      <div>Email: {uv.sEmailChe}</div>
                    </td>
                    <td className="tabular-nums font-medium text-[#192536]">
                      {formatSalaryRange(uv.fMucLuongKyVong, null)}
                    </td>
                    <td className="text-right">
                      <button
                        type="button"
                        onClick={() => setInviteTarget(uv)}
                        className="civic-btn-secondary py-1 px-3 text-xs"
                      >
                        <UserCheck className="w-3.5 h-3.5 text-[#215DA8]" />
                        Mời ứng tuyển
                      </button>
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

export default function TimUngVienPage() {
  return (
    <RoleGate allowedRoles={[2]}>
      <TimUngVienContent />
    </RoleGate>
  );
}
