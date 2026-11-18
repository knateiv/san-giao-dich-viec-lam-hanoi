"use client";

import React, { createContext, useCallback, useContext, useEffect, useState } from "react";
import { apiFetch, getStoredToken, setStoredToken } from "@/lib/apiClient";
import type { TaiKhoanView } from "@/types/models";

interface AuthContextValue {
  user: TaiKhoanView | null;
  loading: boolean;
  login: (sEmail: string, sMatKhau: string) => Promise<TaiKhoanView>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<TaiKhoanView | null>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<TaiKhoanView | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const refreshUser = useCallback(async (): Promise<TaiKhoanView | null> => {
    try {
      const res = await apiFetch<TaiKhoanView>("/api/auth/me");
      setUser(res.duLieu);
      return res.duLieu;
    } catch {
      if (getStoredToken()) {
        setStoredToken(null);
      }
      setUser(null);
      return null;
    }
  }, []);

  useEffect(() => {
    let mounted = true;
    (async () => {
      if (!getStoredToken()) {
        if (mounted) {
          setUser(null);
          setLoading(false);
        }
        return;
      }
      try {
        const res = await apiFetch<TaiKhoanView>("/api/auth/me");
        if (mounted) setUser(res.duLieu);
      } catch {
        if (mounted) {
          setStoredToken(null);
          setUser(null);
        }
      } finally {
        if (mounted) setLoading(false);
      }
    })();
    return () => {
      mounted = false;
    };
  }, []);

  const login = useCallback(async (sEmail: string, sMatKhau: string): Promise<TaiKhoanView> => {
    const res = await apiFetch<TaiKhoanView>("/api/auth/dang-nhap", {
      method: "POST",
      body: JSON.stringify({ sEmail, sMatKhau }),
    });
    if (res.duLieu?.token) {
      setStoredToken(res.duLieu.token);
    }
    setUser(res.duLieu);
    return res.duLieu;
  }, []);

  const logout = useCallback(async () => {
    try {
      await apiFetch<void>("/api/auth/dang-xuat", { method: "POST" });
    } catch {
      // Clear local session regardless of network outcome
    } finally {
      setStoredToken(null);
      setUser(null);
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth phải được sử dụng bên trong AuthProvider.");
  }
  return ctx;
}
