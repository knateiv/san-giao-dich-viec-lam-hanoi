"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { KeyRound, LogIn, ShieldCheck } from "lucide-react";
import { useAuth } from "@/lib/authContext";
import { AlertBanner } from "@/components/AppShell";

const DEMO_ACCOUNTS = [
  {
    roleLabel: "Người tìm việc",
    name: "Nguyễn Minh Anh",
    email: "minhanh@dvvlhanoi.vn",
    desc: "Quản lý hồ sơ, tải CV, ứng tuyển & theo dõi lịch phỏng vấn",
  },
  {
    roleLabel: "Doanh nghiệp (Đã xác minh)",
    name: "Công ty TNHH Minh An",
    email: "tuyendung@minhan.vn",
    desc: "Đăng tin tuyển dụng, sàng lọc ứng viên & mời phỏng vấn",
  },
  {
    roleLabel: "Doanh nghiệp (Chờ thẩm định)",
    name: "Công ty CP Hoàng Hà",
    email: "contact@hoangha.vn",
    desc: "Kiểm thử luồng nộp hồ sơ pháp lý chờ Cán bộ thẩm định",
  },
  {
    roleLabel: "Cán bộ Trung tâm DVVL",
    name: "Trần Thị Lan",
    email: "canbo.lan@dvvlhanoi.gov.vn",
    desc: "Thẩm định DN, duyệt tin, cấp Giấy giới thiệu QR & báo cáo",
  },
  {
    roleLabel: "Quản trị viên Hệ thống",
    name: "Lê Minh Quang",
    email: "admin.quang@dvvlhanoi.gov.vn",
    desc: "Phân quyền RBAC, quản lý danh mục & tra cứu Audit Log",
  },
];

export default function DangNhapPage() {
  const router = useRouter();
  const { login } = useAuth();

  const [email, setEmail] = useState("");
  const [matKhau, setMatKhau] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const performLogin = async (targetEmail: string, targetPwd: string) => {
    setError(null);
    setSubmitting(true);
    try {
      const account = await login(targetEmail.trim(), targetPwd);
      router.push(account.duongDanDich || "/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Đăng nhập thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !matKhau) {
      setError("Vui lòng nhập đầy đủ Email và Mật khẩu.");
      return;
    }
    await performLogin(email, matKhau);
  };

  return (
    <div className="max-w-5xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-6 py-4">
      <section className="lg:col-span-6 civic-card p-6 md:p-8 space-y-5">
        <div>
          <h1 className="text-2xl font-bold text-[#192536]">Đăng nhập hệ thống</h1>
          <p className="text-sm text-[#5F6C7B] mt-1">
            Hệ thống tự động điều hướng theo vai trò được cấp quyền (RBAC). Sai mật khẩu 5 lần liên tiếp sẽ tự động tạm khóa 15 phút.
          </p>
        </div>

        <AlertBanner type="error" message={error} onClose={() => setError(null)} />

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="tbEmail" className="block text-sm font-medium text-[#192536] mb-1">
              Địa chỉ Email
            </label>
            <input
              id="tbEmail"
              type="email"
              className="civic-input"
              placeholder="ten@dvvlhanoi.vn"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
            />
          </div>

          <div>
            <label htmlFor="tbMatKhau" className="block text-sm font-medium text-[#192536] mb-1">
              Mật khẩu
            </label>
            <input
              id="tbMatKhau"
              type="password"
              className="civic-input"
              placeholder="Nhập mật khẩu"
              value={matKhau}
              onChange={(e) => setMatKhau(e.target.value)}
              autoComplete="current-password"
            />
          </div>

          <button id="btnDangNhap" type="submit" disabled={submitting} className="civic-btn-primary w-full">
            <LogIn className="w-4 h-4" />
            {submitting ? "Đang xác thực BCrypt..." : "Đăng nhập"}
          </button>
        </form>

        <div className="pt-3 border-t border-[#DCE2E9] text-sm text-[#5F6C7B] flex items-center justify-between">
          <span>Chưa có tài khoản trên Sàn?</span>
          <Link href="/dang-ky" className="font-semibold text-[#215DA8] hover:underline">
            Đăng ký tài khoản mới
          </Link>
        </div>
      </section>

      <section className="lg:col-span-6 civic-card p-6 md:p-8 space-y-4">
        <div className="flex items-start gap-2.5">
          <ShieldCheck className="w-5 h-5 text-[#215DA8] mt-0.5 shrink-0" />
          <div>
            <h2 className="text-base font-semibold text-[#192536]">
              Tài khoản kiểm thử nhanh 4 vai trò (Dữ liệu thật SQL Server)
            </h2>
            <p className="text-xs text-[#5F6C7B] mt-1">
              Mật khẩu dùng chung: <code className="font-mono text-[#192536] bg-slate-100 px-1.5 py-0.5 rounded">MatKhau@123</code>. Chọn một vai trò bên dưới để điền và đăng nhập trực tiếp qua Spring Boot BCrypt + JWT.
            </p>
          </div>
        </div>

        <div className="divide-y divide-[#DCE2E9] border-t border-b border-[#DCE2E9]">
          {DEMO_ACCOUNTS.map((acc) => (
            <div
              key={acc.email}
              className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 py-3"
            >
              <div>
                <div className="text-sm font-semibold text-[#192536]">
                  {acc.roleLabel} — {acc.name}
                </div>
                <div className="text-xs font-mono text-[#215DA8]">{acc.email}</div>
                <div className="text-xs text-[#5F6C7B] mt-0.5">{acc.desc}</div>
              </div>
              <button
                type="button"
                disabled={submitting}
                onClick={() => {
                  setEmail(acc.email);
                  setMatKhau("MatKhau@123");
                  void performLogin(acc.email, "MatKhau@123");
                }}
                className="civic-btn-secondary shrink-0 text-xs py-1.5 px-3"
              >
                <KeyRound className="w-3.5 h-3.5" />
                Vào nhanh
              </button>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
