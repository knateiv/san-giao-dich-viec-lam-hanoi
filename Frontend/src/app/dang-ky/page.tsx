"use client";

import React, { Suspense, useEffect, useState } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { Building2, CheckCircle2, KeyRound, UserCheck, UserPlus } from "lucide-react";
import { apiFetch } from "@/lib/apiClient";
import { AlertBanner } from "@/components/AppShell";

function DangKyContent() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const [step, setStep] = useState<1 | 2>(1);
  const [roleId, setRoleId] = useState<1 | 2>(1);

  const [sHoTen, setSHoTen] = useState("");
  const [sSDT, setSSDT] = useState("");
  const [sEmail, setSEmail] = useState("");
  const [sMatKhau, setSMatKhau] = useState("");
  const [xacNhanMatKhau, setXacNhanMatKhau] = useState("");
  const [sTenDoanhNghiep, setSTenDoanhNghiep] = useState("");
  const [sMaSoThue, setSMaSoThue] = useState("");

  const [registeredEmail, setRegisteredEmail] = useState("");
  const [otpHint, setOtpHint] = useState("");
  const [maOtp, setMaOtp] = useState("");

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [activated, setActivated] = useState(false);

  useEffect(() => {
    const vt = searchParams.get("vaiTro");
    if (vt === "2") setRoleId(2);
    else if (vt === "1") setRoleId(1);
  }, [searchParams]);

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);

    if (!sHoTen.trim() || !sSDT.trim() || !sEmail.trim() || !sMatKhau || !xacNhanMatKhau) {
      setError("Vui lòng nhập đầy đủ các trường thông tin bắt buộc.");
      return;
    }
    if (roleId === 2 && (!sTenDoanhNghiep.trim() || !sMaSoThue.trim())) {
      setError("Doanh nghiệp vui lòng nhập Tên doanh nghiệp và Mã số thuế (10 hoặc 13 chữ số).");
      return;
    }

    setSubmitting(true);
    try {
      const res = await apiFetch<{ pkMaTaiKhoan: string; sEmail: string; maOtpMinhHoa: string }>(
        "/api/auth/dang-ky",
        {
          method: "POST",
          body: JSON.stringify({
            fkMaVaiTro: roleId,
            sHoTen,
            sSDT,
            sEmail,
            sMatKhau,
            xacNhanMatKhau,
            sTenDoanhNghiep: roleId === 2 ? sTenDoanhNghiep : undefined,
            sMaSoThue: roleId === 2 ? sMaSoThue : undefined,
          }),
        }
      );
      setRegisteredEmail(res.duLieu.sEmail);
      setOtpHint(res.duLieu.maOtpMinhHoa);
      setMaOtp(res.duLieu.maOtpMinhHoa);
      setSuccessMsg(res.thongBao);
      setStep(2);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Đăng ký thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleActivate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);
    setSubmitting(true);
    try {
      const res = await apiFetch<void>("/api/auth/kich-hoat", {
        method: "POST",
        body: JSON.stringify({
          sEmail: registeredEmail || sEmail,
          maOtp: maOtp.trim(),
        }),
      });
      setActivated(true);
      setSuccessMsg(res.thongBao);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Xác thực OTP thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto civic-card p-6 md:p-8 space-y-6">
      <div className="space-y-1">
        <h1 className="text-2xl font-bold text-[#192536]">Đăng ký tài khoản mới</h1>
        <p className="text-sm text-[#5F6C7B]">
          Quy trình 2 bước: Kê khai thông tin tài khoản và kích hoạt bằng mã OTP 6 chữ số.
        </p>
      </div>

      <div className="grid grid-cols-2 gap-3" role="tablist" aria-label="Tiến trình đăng ký">
        <button
          type="button"
          onClick={() => !activated && setStep(1)}
          className={`p-3 rounded-lg border text-left transition-colors duration-150 ${
            step === 1
              ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
              : "border-[#DCE2E9] bg-[#F8FAFC] text-[#5F6C7B]"
          }`}
        >
          <div className="text-xs font-semibold">Bước 1</div>
          <div className="text-sm font-bold">Thông tin đăng ký</div>
        </button>
        <button
          type="button"
          onClick={() => registeredEmail && setStep(2)}
          disabled={!registeredEmail}
          className={`p-3 rounded-lg border text-left transition-colors duration-150 ${
            step === 2
              ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
              : "border-[#DCE2E9] bg-[#F8FAFC] text-[#5F6C7B] disabled:opacity-50"
          }`}
        >
          <div className="text-xs font-semibold">Bước 2</div>
          <div className="text-sm font-bold">Kích hoạt mã OTP</div>
        </button>
      </div>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={successMsg} onClose={() => setSuccessMsg(null)} />

      {step === 1 && (
        <form onSubmit={handleRegister} className="space-y-4">
          <div>
            <span className="block text-sm font-medium text-[#192536] mb-2">
              Loại tài khoản đăng ký *
            </span>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <button
                type="button"
                id="rdbVaiTroNTV"
                onClick={() => setRoleId(1)}
                className={`flex items-center gap-2.5 p-3 rounded-lg border text-sm font-semibold transition-colors duration-150 ${
                  roleId === 1
                    ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
                    : "border-[#DCE2E9] bg-white text-[#192536]"
                }`}
              >
                <UserCheck className="w-4 h-4" />
                Người tìm việc
              </button>
              <button
                type="button"
                id="rdbVaiTroDN"
                onClick={() => setRoleId(2)}
                className={`flex items-center gap-2.5 p-3 rounded-lg border text-sm font-semibold transition-colors duration-150 ${
                  roleId === 2
                    ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
                    : "border-[#DCE2E9] bg-white text-[#192536]"
                }`}
              >
                <Building2 className="w-4 h-4" />
                Doanh nghiệp / NTD
              </button>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label htmlFor="tbHoTen" className="block text-sm font-medium text-[#192536] mb-1">
                Họ và tên người đại diện *
              </label>
              <input
                id="tbHoTen"
                type="text"
                className="civic-input"
                placeholder="Nguyễn Văn An"
                value={sHoTen}
                onChange={(e) => setSHoTen(e.target.value)}
              />
            </div>
            <div>
              <label htmlFor="tbSDT" className="block text-sm font-medium text-[#192536] mb-1">
                Số điện thoại (10 chữ số) *
              </label>
              <input
                id="tbSDT"
                type="tel"
                className="civic-input tabular-nums"
                placeholder="0912345678"
                value={sSDT}
                onChange={(e) => setSSDT(e.target.value)}
              />
            </div>
          </div>

          <div>
            <label htmlFor="tbEmailDangKy" className="block text-sm font-medium text-[#192536] mb-1">
              Địa chỉ Email *
            </label>
            <input
              id="tbEmailDangKy"
              type="email"
              className="civic-input"
              placeholder="ungvien@dvvlhanoi.vn"
              value={sEmail}
              onChange={(e) => setSEmail(e.target.value)}
            />
          </div>

          {roleId === 2 && (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-4 border-t border-[#DCE2E9]">
              <div>
                <label htmlFor="tbTenDoanhNghiep" className="block text-sm font-medium text-[#192536] mb-1">
                  Tên doanh nghiệp theo ĐKKD *
                </label>
                <input
                  id="tbTenDoanhNghiep"
                  type="text"
                  className="civic-input"
                  placeholder="Công ty TNHH ABC Hà Nội"
                  value={sTenDoanhNghiep}
                  onChange={(e) => setSTenDoanhNghiep(e.target.value)}
                />
              </div>
              <div>
                <label htmlFor="tbMaSoThue" className="block text-sm font-medium text-[#192536] mb-1">
                  Mã số thuế (10 hoặc 13 số) *
                </label>
                <input
                  id="tbMaSoThue"
                  type="text"
                  className="civic-input font-mono tabular-nums"
                  placeholder="0109998888"
                  value={sMaSoThue}
                  onChange={(e) => setSMaSoThue(e.target.value)}
                />
              </div>
            </div>
          )}

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label htmlFor="tbMatKhauDangKy" className="block text-sm font-medium text-[#192536] mb-1">
                Mật khẩu mạnh *
              </label>
              <input
                id="tbMatKhauDangKy"
                type="password"
                className="civic-input"
                placeholder="Tối thiểu 8 ký tự, Hoa, thường, số, ký tự đặc biệt"
                value={sMatKhau}
                onChange={(e) => setSMatKhau(e.target.value)}
              />
            </div>
            <div>
              <label htmlFor="tbXacNhanMatKhau" className="block text-sm font-medium text-[#192536] mb-1">
                Xác nhận mật khẩu *
              </label>
              <input
                id="tbXacNhanMatKhau"
                type="password"
                className="civic-input"
                placeholder="Nhập lại mật khẩu"
                value={xacNhanMatKhau}
                onChange={(e) => setXacNhanMatKhau(e.target.value)}
              />
            </div>
          </div>

          <button id="btnDangKy" type="submit" disabled={submitting} className="civic-btn-primary w-full">
            <UserPlus className="w-4 h-4" />
            {submitting ? "Đang tạo tài khoản..." : "Đăng ký và nhận mã OTP"}
          </button>
        </form>
      )}

      {step === 2 && (
        <form onSubmit={handleActivate} className="space-y-4">
          {otpHint && !activated && (
            <div className="p-4 rounded-lg border border-[#BFD7F5] bg-[#ECF3FC] text-sm text-[#192536]">
              Mã OTP mô phỏng gửi tới email <strong className="font-mono">{registeredEmail}</strong> là:{" "}
              <span className="font-mono font-bold text-[#215DA8] text-base">{otpHint}</span>
            </div>
          )}

          <div>
            <label htmlFor="tbMaOtp" className="block text-sm font-medium text-[#192536] mb-1">
              Mã OTP kích hoạt tài khoản (6 chữ số)
            </label>
            <input
              id="tbMaOtp"
              type="text"
              className="civic-input font-mono tabular-nums"
              value={maOtp}
              onChange={(e) => setMaOtp(e.target.value)}
              maxLength={6}
              disabled={activated}
            />
          </div>

          {!activated ? (
            <button id="btnKichHoatOtp" type="submit" disabled={submitting} className="civic-btn-primary w-full">
              <KeyRound className="w-4 h-4" />
              {submitting ? "Đang kích hoạt..." : "Xác thực OTP và kích hoạt"}
            </button>
          ) : (
            <button
              type="button"
              onClick={() => router.push("/dang-nhap")}
              className="civic-btn-primary w-full"
            >
              <CheckCircle2 className="w-4 h-4" />
              Chuyển đến trang Đăng nhập
            </button>
          )}
        </form>
      )}

      <div className="pt-3 border-t border-[#DCE2E9] text-sm text-[#5F6C7B] flex items-center justify-between">
        <span>Đã có tài khoản hoạt động?</span>
        <Link href="/dang-nhap" className="font-semibold text-[#215DA8] hover:underline">
          Đăng nhập
        </Link>
      </div>
    </div>
  );
}

export default function DangKyPage() {
  return (
    <Suspense fallback={<div className="max-w-2xl mx-auto civic-card p-6 text-sm text-[#5F6C7B]">Đang tải biểu mẫu đăng ký...</div>}>
      <DangKyContent />
    </Suspense>
  );
}
