"use client";

import React, { useEffect, useState } from "react";
import { KeyRound, Save, Upload, UserCircle2 } from "lucide-react";
import { apiFetch, apiUpload, formatDateTimeVN } from "@/lib/apiClient";
import { useAuth } from "@/lib/authContext";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { TaiKhoanView } from "@/types/models";

function TaiKhoanContent() {
  const { user, refreshUser } = useAuth();
  const [sHoTen, setSHoTen] = useState("");
  const [sSDT, setSSDT] = useState("");

  const [matKhauHienTai, setMatKhauHienTai] = useState("");
  const [matKhauMoi, setMatKhauMoi] = useState("");
  const [xacNhanMatKhauMoi, setXacNhanMatKhauMoi] = useState("");

  const [savingInfo, setSavingInfo] = useState(false);
  const [savingPwd, setSavingPwd] = useState(false);
  const [uploadingAvatar, setUploadingAvatar] = useState(false);

  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (user) {
      setSHoTen(user.sHoTen || "");
      setSSDT(user.sSDT || "");
    }
  }, [user]);

  const handleUpdateInfo = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSavingInfo(true);
    try {
      const res = await apiFetch<TaiKhoanView>("/api/tai-khoan/cap-nhat", {
        method: "PUT",
        body: JSON.stringify({ sHoTen, sSDT }),
      });
      await refreshUser();
      setSuccess(res.thongBao);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Cập nhật thông tin thất bại.");
    } finally {
      setSavingInfo(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSavingPwd(true);
    try {
      const res = await apiFetch<void>("/api/tai-khoan/doi-mat-khau", {
        method: "PUT",
        body: JSON.stringify({ matKhauHienTai, matKhauMoi, xacNhanMatKhauMoi }),
      });
      setMatKhauHienTai("");
      setMatKhauMoi("");
      setXacNhanMatKhauMoi("");
      setSuccess(res.thongBao);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Đổi mật khẩu thất bại.");
    } finally {
      setSavingPwd(false);
    }
  };

  const handleAvatarUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setError(null);
    setSuccess(null);
    setUploadingAvatar(true);
    try {
      const fd = new FormData();
      fd.append("file", file);
      const res = await apiUpload<TaiKhoanView>("/api/tai-khoan/anh-dai-dien", fd, "POST");
      await refreshUser();
      setSuccess(res.thongBao);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Tải ảnh đại diện thất bại.");
    } finally {
      setUploadingAvatar(false);
      e.target.value = "";
    }
  };

  if (!user) return null;

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[#192536]">Thông tin tài khoản &amp; Bảo mật</h1>
        <p className="text-sm text-[#5F6C7B] mt-1">
          Quản lý thông tin liên hệ, ảnh đại diện (PNG/JPG tối đa 2MB) và đổi mật khẩu BCrypt.
        </p>
      </div>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <section className="civic-card p-6 space-y-4">
          <div className="flex flex-col items-center text-center space-y-3">
            {user.sDuongDanAnhDaiDien ? (
              <img
                src={user.sDuongDanAnhDaiDien}
                alt={`Ảnh đại diện ${user.sHoTen || user.sEmail}`}
                className="w-24 h-24 rounded-full object-cover border border-[#DCE2E9]"
              />
            ) : (
              <div className="w-24 h-24 rounded-full bg-[#ECF3FC] text-[#215DA8] flex items-center justify-center border border-[#BFD7F5]">
                <UserCircle2 className="w-12 h-12" />
              </div>
            )}
            <div>
              <div className="font-semibold text-[#192536]">{user.sHoTen || user.sEmail}</div>
              <div className="text-xs font-mono text-[#5F6C7B] mt-0.5">{user.pkMaTaiKhoan}</div>
            </div>
            <StatusBadge variant="info">{user.sTenVaiTro}</StatusBadge>
          </div>

          <div className="pt-3 border-t border-[#DCE2E9] text-xs text-[#5F6C7B] space-y-1.5">
            <div>
              Email: <span className="font-medium text-[#192536]">{user.sEmail}</span>
            </div>
            <div>
              Ngày tạo: <span className="tabular-nums">{formatDateTimeVN(user.dNgayTao)}</span>
            </div>
          </div>

          <label className="civic-btn-secondary w-full cursor-pointer">
            <Upload className="w-4 h-4" />
            {uploadingAvatar ? "Đang tải ảnh..." : "Đổi ảnh đại diện"}
            <input
              type="file"
              accept="image/png,image/jpeg"
              className="hidden"
              onChange={handleAvatarUpload}
              disabled={uploadingAvatar}
            />
          </label>
        </section>

        <div className="md:col-span-2 space-y-6">
          <form onSubmit={handleUpdateInfo} className="civic-card p-6 space-y-4">
            <h2 className="text-lg font-semibold text-[#192536]">Cập nhật thông tin cá nhân</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label htmlFor="tbHoTenTk" className="block text-sm font-medium text-[#192536] mb-1">
                  Họ và tên *
                </label>
                <input
                  id="tbHoTenTk"
                  type="text"
                  className="civic-input"
                  value={sHoTen}
                  onChange={(e) => setSHoTen(e.target.value)}
                />
              </div>
              <div>
                <label htmlFor="tbSdtTk" className="block text-sm font-medium text-[#192536] mb-1">
                  Số điện thoại *
                </label>
                <input
                  id="tbSdtTk"
                  type="tel"
                  className="civic-input tabular-nums"
                  value={sSDT}
                  onChange={(e) => setSSDT(e.target.value)}
                />
              </div>
            </div>
            <button id="btnCapNhatTaiKhoan" type="submit" disabled={savingInfo} className="civic-btn-primary">
              <Save className="w-4 h-4" />
              {savingInfo ? "Đang lưu..." : "Lưu thay đổi"}
            </button>
          </form>

          <form onSubmit={handleChangePassword} className="civic-card p-6 space-y-4">
            <h2 className="text-lg font-semibold text-[#192536]">Đổi mật khẩu tài khoản</h2>
            <div>
              <label htmlFor="tbMatKhauCu" className="block text-sm font-medium text-[#192536] mb-1">
                Mật khẩu hiện tại *
              </label>
              <input
                id="tbMatKhauCu"
                type="password"
                className="civic-input"
                value={matKhauHienTai}
                onChange={(e) => setMatKhauHienTai(e.target.value)}
              />
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label htmlFor="tbMatKhauMoi" className="block text-sm font-medium text-[#192536] mb-1">
                  Mật khẩu mới *
                </label>
                <input
                  id="tbMatKhauMoi"
                  type="password"
                  className="civic-input"
                  value={matKhauMoi}
                  onChange={(e) => setMatKhauMoi(e.target.value)}
                />
              </div>
              <div>
                <label htmlFor="tbXacNhanMatKhauMoi" className="block text-sm font-medium text-[#192536] mb-1">
                  Xác nhận mật khẩu mới *
                </label>
                <input
                  id="tbXacNhanMatKhauMoi"
                  type="password"
                  className="civic-input"
                  value={xacNhanMatKhauMoi}
                  onChange={(e) => setXacNhanMatKhauMoi(e.target.value)}
                />
              </div>
            </div>
            <button id="btnDoiMatKhau" type="submit" disabled={savingPwd} className="civic-btn-secondary">
              <KeyRound className="w-4 h-4" />
              {savingPwd ? "Đang cập nhật..." : "Cập nhật mật khẩu"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default function TaiKhoanPage() {
  return (
    <RoleGate allowedRoles={[1, 2, 3, 4]}>
      <TaiKhoanContent />
    </RoleGate>
  );
}
