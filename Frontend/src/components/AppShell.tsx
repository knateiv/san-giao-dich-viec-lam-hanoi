"use client";

import React, { useState } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import {
  AlertCircle,
  Briefcase,
  Building2,
  CheckCircle2,
  FileCheck2,
  FileText,
  Home,
  Info,
  LogIn,
  LogOut,
  Menu,
  ShieldCheck,
  UserCheck,
  UserCircle2,
  Users,
  BarChart3,
  Database,
  ScrollText,
  X,
} from "lucide-react";
import { useAuth } from "@/lib/authContext";

interface NavItem {
  href: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
}

export function StatusBadge({
  variant = "neutral",
  children,
}: {
  variant?: "neutral" | "info" | "success" | "warning" | "danger";
  children: React.ReactNode;
}) {
  const styles: Record<string, string> = {
    neutral: "bg-slate-100 text-slate-700 border-slate-200",
    info: "bg-[#ECF3FC] text-[#215DA8] border-[#BFD7F5]",
    success: "bg-emerald-50 text-emerald-800 border-emerald-200",
    warning: "bg-amber-50 text-amber-800 border-amber-200",
    danger: "bg-red-50 text-red-800 border-red-200",
  };
  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border whitespace-nowrap ${styles[variant]}`}
    >
      {children}
    </span>
  );
}

export function AlertBanner({
  type = "info",
  message,
  onClose,
}: {
  type?: "error" | "success" | "info" | "warning";
  message: string | null;
  onClose?: () => void;
}) {
  if (!message) return null;
  const styles = {
    error: "bg-red-50 border-red-200 text-red-800",
    success: "bg-emerald-50 border-emerald-200 text-emerald-800",
    warning: "bg-amber-50 border-amber-200 text-amber-800",
    info: "bg-[#ECF3FC] border-[#BFD7F5] text-[#192536]",
  }[type];
  const Icon = type === "success" ? CheckCircle2 : type === "info" ? Info : AlertCircle;
  return (
    <div
      role={type === "error" ? "alert" : "status"}
      className={`flex items-start justify-between gap-3 px-4 py-3 rounded-lg border text-sm ${styles}`}
    >
      <div className="flex items-start gap-2.5">
        <Icon className="w-4 h-4 mt-0.5 shrink-0" />
        <span className="leading-relaxed">{message}</span>
      </div>
      {onClose && (
        <button
          type="button"
          onClick={onClose}
          aria-label="Đóng thông báo"
          className="p-0.5 rounded hover:bg-black/5"
        >
          <X className="w-4 h-4" />
        </button>
      )}
    </div>
  );
}

export function RoleGate({
  allowedRoles,
  children,
}: {
  allowedRoles: number[];
  children: React.ReactNode;
}) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="max-w-3xl mx-auto my-8 civic-card p-6 text-sm text-[#5F6C7B]">
        Đang kiểm tra phiên đăng nhập...
      </div>
    );
  }

  if (!user) {
    return (
      <div className="max-w-xl mx-auto my-10 civic-card p-6 space-y-4">
        <h2 className="text-lg font-semibold text-[#192536]">Yêu cầu đăng nhập</h2>
        <p className="text-sm text-[#5F6C7B]">
          Vui lòng đăng nhập bằng tài khoản được phân quyền phù hợp để truy cập phân hệ nghiệp vụ này.
        </p>
        <div className="flex flex-wrap gap-3">
          <Link href="/dang-nhap" className="civic-btn-primary">
            <LogIn className="w-4 h-4" />
            Đăng nhập ngay
          </Link>
          <Link href="/" className="civic-btn-secondary">
            Về trang chủ
          </Link>
        </div>
      </div>
    );
  }

  if (!allowedRoles.includes(user.fkMaVaiTro)) {
    return (
      <div className="max-w-xl mx-auto my-10 civic-card p-6 space-y-4">
        <h2 className="text-lg font-semibold text-[#B91C1C]">Không có quyền truy cập</h2>
        <p className="text-sm text-[#5F6C7B]">
          Tài khoản hiện tại ({user.sTenVaiTro}) không được cấp quyền sử dụng phân hệ này.
        </p>
        <div className="flex flex-wrap gap-3">
          <Link href={user.duongDanDich || "/"} className="civic-btn-primary">
            Về không gian làm việc của tôi
          </Link>
          <Link href="/" className="civic-btn-secondary">
            Về trang chủ
          </Link>
        </div>
      </div>
    );
  }

  return <>{children}</>;
}

export default function AppShell({ children }: { children: React.ReactNode }) {
  const { user, loading, logout } = useAuth();
  const pathname = usePathname();
  const router = useRouter();
  const [mobileOpen, setMobileOpen] = useState(false);

  const navItems: NavItem[] = [
    { href: "/", label: "Trang chủ", icon: Home },
    { href: "/viec-lam", label: "Việc làm", icon: Briefcase },
  ];

  if (user?.fkMaVaiTro === 1) {
    navItems.push(
      { href: "/ung-vien/ho-so", label: "Hồ sơ & CV", icon: UserCheck },
      { href: "/ung-vien/ung-tuyen", label: "Ứng tuyển của tôi", icon: FileCheck2 }
    );
  } else if (user?.fkMaVaiTro === 2) {
    navItems.push(
      { href: "/doanh-nghiep", label: "Tin của tôi", icon: Building2 },
      { href: "/doanh-nghiep/phap-ly", label: "Pháp lý", icon: ShieldCheck },
      { href: "/doanh-nghiep/dang-tin", label: "Đăng tin", icon: FileText },
      { href: "/doanh-nghiep/ho-so-ung-tuyen", label: "Ứng viên", icon: Users },
      { href: "/doanh-nghiep/tim-ung-vien", label: "Tìm UV", icon: UserCheck }
    );
  } else if (user?.fkMaVaiTro === 3) {
    navItems.push(
      { href: "/can-bo", label: "Kiểm duyệt", icon: ShieldCheck },
      { href: "/can-bo/giay-gioi-thieu", label: "Giấy giới thiệu", icon: FileCheck2 },
      { href: "/can-bo/bao-cao", label: "Thống kê Cung-Cầu", icon: BarChart3 }
    );
  } else if (user?.fkMaVaiTro === 4) {
    navItems.push(
      { href: "/quan-tri", label: "Tài khoản & RBAC", icon: Users },
      { href: "/quan-tri/danh-muc", label: "Danh mục", icon: Database },
      { href: "/quan-tri/nhat-ky", label: "Nhật ký hệ thống", icon: ScrollText },
      { href: "/can-bo/bao-cao", label: "Báo cáo", icon: BarChart3 }
    );
  }

  const handleLogout = async () => {
    await logout();
    setMobileOpen(false);
    router.push("/dang-nhap");
  };

  return (
    <div className="min-h-screen flex flex-col bg-[#F6F8FB]">
      <header className="sticky top-0 z-30 bg-white border-b border-[#DCE2E9]">
        <div className="bg-[#215DA8] text-white text-xs py-1.5">
          <div className="max-w-7xl mx-auto px-4 flex flex-wrap items-center justify-between gap-2">
            <span className="font-medium">
              Trung tâm Dịch vụ việc làm Hà Nội — Cổng Giao dịch Việc làm Trực tuyến
            </span>
            <span className="tabular-nums opacity-90">
              215 Trung Kính, Cầu Giấy, Hà Nội · Hotline: 024 3782 2806
            </span>
          </div>
        </div>

        <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between gap-4">
          <Link href="/" className="flex items-center gap-2.5 shrink-0">
            <div className="w-9 h-9 rounded-lg bg-[#215DA8] text-white flex items-center justify-center">
              <Building2 className="w-5 h-5" />
            </div>
            <div>
              <div className="text-sm font-bold text-[#192536] leading-tight">
                Việc Làm Hà Nội
              </div>
              <div className="text-xs text-[#5F6C7B] leading-tight">
                dvvlhanoi.gov.vn
              </div>
            </div>
          </Link>

          <nav aria-label="Điều hướng chính" className="hidden lg:flex flex-1 min-w-0 items-center justify-center gap-0.5 overflow-x-auto">
            {navItems.map((item) => {
              const active =
                item.href === "/"
                  ? pathname === "/"
                  : pathname === item.href || pathname.startsWith(`${item.href}/`);
              const Icon = item.icon;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`inline-flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-[13px] font-medium whitespace-nowrap shrink-0 transition-colors duration-150 ${
                    active
                      ? "bg-[#ECF3FC] text-[#215DA8]"
                      : "text-[#5F6C7B] hover:text-[#192536] hover:bg-slate-100"
                  }`}
                >
                  <Icon className="w-3.5 h-3.5 shrink-0" />
                  <span>{item.label}</span>
                </Link>
              );
            })}
          </nav>

          <div className="hidden lg:flex items-center gap-2 shrink-0">
            {loading ? (
              <span className="text-xs text-[#5F6C7B]">Đang tải...</span>
            ) : user ? (
              <>
                <Link
                  href="/tai-khoan"
                  className="inline-flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg border border-[#DCE2E9] bg-[#F8FAFC] hover:bg-[#ECF3FC] transition-colors duration-150"
                >
                  <UserCircle2 className="w-4 h-4 text-[#215DA8] shrink-0" />
                  <span className="text-[13px] font-medium text-[#192536] max-w-[110px] truncate">
                    {user.sHoTen || user.sEmail}
                  </span>
                  <StatusBadge variant="info">{user.sTenVaiTro}</StatusBadge>
                </Link>
                <button
                  type="button"
                  id="btnDangXuatHeader"
                  onClick={handleLogout}
                  className="civic-btn-secondary py-1.5 px-3 text-[13px]"
                >
                  <LogOut className="w-3.5 h-3.5" />
                  Thoát
                </button>
              </>
            ) : (
              <>
                <Link href="/dang-nhap" className="civic-btn-secondary">
                  Đăng nhập
                </Link>
                <Link href="/dang-ky" className="civic-btn-primary">
                  Đăng ký
                </Link>
              </>
            )}
          </div>

          <button
            type="button"
            onClick={() => setMobileOpen((prev) => !prev)}
            aria-label="Mở menu điều hướng"
            className="lg:hidden p-2 rounded-lg border border-[#DCE2E9] text-[#192536]"
          >
            {mobileOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>

        {mobileOpen && (
          <div className="lg:hidden border-t border-[#DCE2E9] bg-white px-4 py-3 space-y-2">
            {navItems.map((item) => {
              const Icon = item.icon;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  onClick={() => setMobileOpen(false)}
                  className="flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium text-[#192536] hover:bg-[#ECF3FC]"
                >
                  <Icon className="w-4 h-4 text-[#215DA8]" />
                  <span>{item.label}</span>
                </Link>
              );
            })}
            <div className="pt-2 border-t border-[#DCE2E9] flex flex-wrap gap-2">
              {user ? (
                <>
                  <Link
                    href="/tai-khoan"
                    onClick={() => setMobileOpen(false)}
                    className="civic-btn-secondary flex-1"
                  >
                    Tài khoản ({user.sTenVaiTro})
                  </Link>
                  <button type="button" onClick={handleLogout} className="civic-btn-primary">
                    Đăng xuất
                  </button>
                </>
              ) : (
                <>
                  <Link
                    href="/dang-nhap"
                    onClick={() => setMobileOpen(false)}
                    className="civic-btn-secondary flex-1"
                  >
                    Đăng nhập
                  </Link>
                  <Link
                    href="/dang-ky"
                    onClick={() => setMobileOpen(false)}
                    className="civic-btn-primary flex-1"
                  >
                    Đăng ký
                  </Link>
                </>
              )}
            </div>
          </div>
        )}
      </header>

      <main className="flex-1 w-full max-w-7xl mx-auto px-4 py-6">{children}</main>

      <footer className="bg-white border-t border-[#DCE2E9] mt-10">
        <div className="max-w-7xl mx-auto px-4 py-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 text-xs text-[#5F6C7B]">
          <div>
            <p className="font-semibold text-[#192536]">
              Trung tâm Dịch vụ việc làm Hà Nội — Sàn Giao Dịch Việc Làm Trực Tuyến
            </p>
            <p className="mt-1">
              Trụ sở chính: Số 215 Trung Kính, Yên Hòa, Cầu Giấy, Hà Nội · Kết nối dữ liệu 30 quận, huyện, thị xã.
            </p>
          </div>
          <div className="text-left sm:text-right">
            <p>Đề tài BTL SE.G01 — Khoa CNTT, Trường Đại học Mở Hà Nội</p>
            <p className="mt-1">Hệ thống kết nối SQL Server, Spring Boot &amp; Next.js</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
