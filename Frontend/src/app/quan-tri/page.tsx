"use client";

import React, { useCallback, useEffect, useState } from "react";
import { Lock, Search, Shield, Unlock, Users } from "lucide-react";
import { apiFetch, formatDateTimeVN } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { TaiKhoanView } from "@/types/models";

function QuanTriTaiKhoanContent() {
  const [accounts, setAccounts] = useState<TaiKhoanView[]>([]);
  const [roleId, setRoleId] = useState("");
  const [locked, setLocked] = useState("");
  const [q, setQ] = useState("");

  const [lockTarget, setLockTarget] = useState<TaiKhoanView | null>(null);
  const [lyDoKhoa, setLyDoKhoa] = useState("");

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const loadAccounts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const qs = new URLSearchParams();
      if (roleId) qs.set("roleId", roleId);
      if (locked !== "") qs.set("locked", locked);
      if (q.trim()) qs.set("q", q.trim());
      const res = await apiFetch<TaiKhoanView[]>(
        `/api/quan-tri/tai-khoan${qs.toString() ? `?${qs.toString()}` : ""}`
      );
      setAccounts(res.duLieu || []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không tải được danh sách tài khoản.");
    } finally {
      setLoading(false);
    }
  }, [roleId, locked, q]);

  useEffect(() => {
    void loadAccounts();
  }, [loadAccounts]);

  const handleChangeRole = async (acc: TaiKhoanView, newRoleId: number) => {
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const res = await apiFetch<TaiKhoanView>(
        `/api/quan-tri/tai-khoan/${encodeURIComponent(acc.pkMaTaiKhoan)}/phan-quyen`,
        {
          method: "PUT",
          body: JSON.stringify({ fkMaVaiTro: newRoleId }),
        }
      );
      setSuccess(res.thongBao);
      await loadAccounts();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Cập nhật vai trò thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleUnlock = async (acc: TaiKhoanView) => {
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const res = await apiFetch<TaiKhoanView>(
        `/api/quan-tri/tai-khoan/${encodeURIComponent(acc.pkMaTaiKhoan)}/khoa-mo`,
        {
          method: "PUT",
          body: JSON.stringify({ bBiKhoa: false, sLyDoKhoa: "" }),
        }
      );
      setSuccess(res.thongBao);
      await loadAccounts();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Mở khóa tài khoản thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleConfirmLock = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!lockTarget) return;
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const res = await apiFetch<TaiKhoanView>(
        `/api/quan-tri/tai-khoan/${encodeURIComponent(lockTarget.pkMaTaiKhoan)}/khoa-mo`,
        {
          method: "PUT",
          body: JSON.stringify({ bBiKhoa: true, sLyDoKhoa: lyDoKhoa.trim() }),
        }
      );
      setSuccess(res.thongBao);
      setLockTarget(null);
      setLyDoKhoa("");
      await loadAccounts();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Khóa tài khoản thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[#192536]">Quản trị Tài khoản &amp; Phân quyền RBAC</h1>
        <p className="text-sm text-[#5F6C7B] mt-1">
          Điều chỉnh vai trò hệ thống hoặc khóa/mở khóa tài khoản vi phạm. JWT phiên cũ của tài khoản bị khóa hoặc đổi vai trò tự động mất hiệu lực ngay lập tức.
        </p>
      </div>

      <form
        onSubmit={(e) => {
          e.preventDefault();
          void loadAccounts();
        }}
        className="civic-card p-5 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3"
      >
        <div>
          <label htmlFor="tbTimTk" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Email / Họ tên / Mã tài khoản
          </label>
          <input
            id="tbTimTk"
            type="text"
            className="civic-input"
            placeholder="Nhập từ khóa tìm kiếm..."
            value={q}
            onChange={(e) => setQ(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="cbbLocVaiTro" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Vai trò
          </label>
          <select
            id="cbbLocVaiTro"
            className="civic-input"
            value={roleId}
            onChange={(e) => setRoleId(e.target.value)}
          >
            <option value="">Tất cả vai trò</option>
            <option value="1">Người tìm việc</option>
            <option value="2">Doanh nghiệp</option>
            <option value="3">Cán bộ DVVL</option>
            <option value="4">Admin</option>
          </select>
        </div>
        <div>
          <label htmlFor="cbbLocTrangThaiKhoa" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
            Trạng thái khóa
          </label>
          <select
            id="cbbLocTrangThaiKhoa"
            className="civic-input"
            value={locked}
            onChange={(e) => setLocked(e.target.value)}
          >
            <option value="">Tất cả trạng thái</option>
            <option value="false">Đang hoạt động</option>
            <option value="true">Đang bị khóa</option>
          </select>
        </div>
        <div className="flex items-end">
          <button type="submit" className="civic-btn-primary w-full">
            <Search className="w-4 h-4" />
            Tìm tài khoản
          </button>
        </div>
      </form>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      {lockTarget && (
        <form onSubmit={handleConfirmLock} className="civic-card p-5 bg-red-50 border-red-200 space-y-3">
          <div className="flex items-center justify-between">
            <h2 className="text-sm font-semibold text-[#B91C1C] flex items-center gap-2">
              <Shield className="w-4 h-4" />
              Khóa tài khoản {lockTarget.sEmail} ({lockTarget.pkMaTaiKhoan})
            </h2>
            <button type="button" onClick={() => setLockTarget(null)} className="text-xs font-semibold text-[#5F6C7B]">
              Đóng
            </button>
          </div>
          <input
            id="tbLyDoKhoaTk"
            type="text"
            className="civic-input"
            placeholder="Nhập lý do khóa tài khoản (bắt buộc)..."
            value={lyDoKhoa}
            onChange={(e) => setLyDoKhoa(e.target.value)}
            required
          />
          <button type="submit" disabled={submitting} className="civic-btn-primary text-xs">
            Xác nhận khóa ngay
          </button>
        </form>
      )}

      <section className="civic-card overflow-hidden">
        <div className="px-5 py-4 border-b border-[#DCE2E9] flex items-center justify-between">
          <h2 className="text-base font-semibold text-[#192536] flex items-center gap-2">
            <Users className="w-4 h-4 text-[#215DA8]" />
            Danh sách tài khoản hệ thống
          </h2>
          <span className="text-xs text-[#5F6C7B] tabular-nums">{accounts.length} tài khoản</span>
        </div>

        <div className="overflow-x-auto">
          <table id="dgvTaiKhoan" className="civic-table">
            <thead>
              <tr>
                <th>Tài khoản / Họ tên</th>
                <th>Phân quyền RBAC</th>
                <th>Trạng thái</th>
                <th>Ngày tạo</th>
                <th className="text-right">Khóa / Mở khóa</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={5} className="text-center py-8 text-[#5F6C7B]">
                    Đang tải danh sách tài khoản...
                  </td>
                </tr>
              ) : accounts.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center py-8 text-[#5F6C7B]">
                    Không tìm thấy tài khoản phù hợp.
                  </td>
                </tr>
              ) : (
                accounts.map((acc) => (
                  <tr key={acc.pkMaTaiKhoan}>
                    <td>
                      <div className="font-semibold text-[#192536]">{acc.sHoTen || "Chưa cập nhật"}</div>
                      <div className="text-xs font-mono text-[#215DA8]">{acc.sEmail}</div>
                      <div className="text-xs text-[#5F6C7B] tabular-nums">
                        {acc.pkMaTaiKhoan} {acc.sSDT ? `· ${acc.sSDT}` : ""}
                      </div>
                      {acc.bBiKhoa && acc.sLyDoKhoa && (
                        <div className="text-xs text-[#B91C1C] mt-1">Lý do khóa: {acc.sLyDoKhoa}</div>
                      )}
                    </td>
                    <td>
                      <select
                        aria-label={`Vai trò của ${acc.sEmail}`}
                        className="civic-input py-1.5 text-xs w-44"
                        value={acc.fkMaVaiTro}
                        disabled={submitting}
                        onChange={(e) => void handleChangeRole(acc, Number(e.target.value))}
                      >
                        <option value={1}>Người tìm việc</option>
                        <option value={2}>Doanh nghiệp</option>
                        <option value={3}>Cán bộ DVVL</option>
                        <option value={4}>Admin</option>
                      </select>
                    </td>
                    <td>
                      <div className="flex flex-col items-start gap-1">
                        {acc.bBiKhoa ? (
                          <StatusBadge variant="danger">Đang bị khóa</StatusBadge>
                        ) : acc.bDaKichHoat ? (
                          <StatusBadge variant="success">Hoạt động</StatusBadge>
                        ) : (
                          <StatusBadge variant="warning">Chờ OTP</StatusBadge>
                        )}
                      </div>
                    </td>
                    <td className="tabular-nums text-xs text-[#5F6C7B]">{formatDateTimeVN(acc.dNgayTao)}</td>
                    <td className="text-right">
                      {acc.bBiKhoa ? (
                        <button
                          type="button"
                          disabled={submitting}
                          onClick={() => void handleUnlock(acc)}
                          className="civic-btn-secondary py-1 px-2.5 text-xs text-[#15803D]"
                        >
                          <Unlock className="w-3.5 h-3.5" />
                          Mở khóa
                        </button>
                      ) : (
                        <button
                          type="button"
                          disabled={submitting}
                          onClick={() => {
                            setLockTarget(acc);
                            setLyDoKhoa("");
                          }}
                          className="civic-btn-secondary py-1 px-2.5 text-xs text-[#B91C1C]"
                        >
                          <Lock className="w-3.5 h-3.5" />
                          Khóa
                        </button>
                      )}
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

export default function QuanTriPage() {
  return (
    <RoleGate allowedRoles={[4]}>
      <QuanTriTaiKhoanContent />
    </RoleGate>
  );
}
