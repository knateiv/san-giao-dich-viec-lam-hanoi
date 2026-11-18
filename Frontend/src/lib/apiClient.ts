import type { ApiResponse } from "@/types/models";

const TOKEN_KEY = "dvvl_token";

export function getStoredToken(): string | null {
  if (typeof window === "undefined") return null;
  try {
    return window.localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}

export function setStoredToken(token: string | null): void {
  if (typeof window === "undefined") return;
  try {
    if (token) {
      window.localStorage.setItem(TOKEN_KEY, token);
    } else {
      window.localStorage.removeItem(TOKEN_KEY);
    }
  } catch {
    // Ignore storage quota errors
  }
}

function buildAuthHeaders(extra?: HeadersInit): Headers {
  const headers = new Headers(extra);
  const token = getStoredToken();
  if (token && !headers.has("Authorization")) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  return headers;
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<ApiResponse<T>> {
  const headers = buildAuthHeaders(options.headers);
  if (options.body && !(options.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const res = await fetch(path, {
    ...options,
    headers,
    credentials: "include",
    cache: "no-store",
  });

  const contentType = res.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    const json = (await res.json()) as ApiResponse<T>;
    if (!res.ok || json.thanhCong === false) {
      throw new Error(json.thongBao || `Yêu cầu thất bại (mã ${res.status}).`);
    }
    return json;
  }

  if (!res.ok) {
    throw new Error(`Lỗi máy chủ (${res.status}).`);
  }

  return {
    thanhCong: true,
    thongBao: "Thành công",
    duLieu: undefined as unknown as T,
  };
}

export async function apiUpload<T>(
  path: string,
  formData: FormData,
  method: "POST" | "PUT" = "POST"
): Promise<ApiResponse<T>> {
  const headers = buildAuthHeaders();
  const res = await fetch(path, {
    method,
    body: formData,
    headers,
    credentials: "include",
  });
  const json = (await res.json()) as ApiResponse<T>;
  if (!res.ok || json.thanhCong === false) {
    throw new Error(json.thongBao || `Tải tệp thất bại (mã ${res.status}).`);
  }
  return json;
}

export async function downloadBinary(path: string, defaultFilename: string): Promise<void> {
  const headers = buildAuthHeaders();
  const res = await fetch(path, {
    method: "GET",
    headers,
    credentials: "include",
  });

  if (!res.ok) {
    const ct = res.headers.get("content-type") || "";
    if (ct.includes("application/json")) {
      const json = (await res.json()) as ApiResponse<unknown>;
      throw new Error(json.thongBao || `Không thể tải tệp (${res.status}).`);
    }
    throw new Error(`Không thể tải tệp (${res.status}).`);
  }

  const blob = await res.blob();
  const cd = res.headers.get("content-disposition") || "";
  const match = cd.match(/filename\*?=(?:UTF-8'')?["']?([^"';]+)["']?/i);
  const filename = match ? decodeURIComponent(match[1]) : defaultFilename;

  const url = window.URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  anchor.remove();
  window.setTimeout(() => window.URL.revokeObjectURL(url), 1000);
}

export function formatSalaryRange(min?: number | null, max?: number | null): string {
  if ((min === null || min === undefined) && (max === null || max === undefined)) {
    return "Thỏa thuận";
  }
  const fmt = (val: number) =>
    val >= 1000000
      ? Number((val / 1000000).toFixed(1)).toString().replace(/\.0$/, "")
      : val.toLocaleString("vi-VN");
  if (min && max) {
    if (min === max) return `${fmt(min)} triệu đ`;
    return `${fmt(min)} – ${fmt(max)} triệu đ`;
  }
  if (min) return `Từ ${fmt(min)} triệu đ`;
  if (max) return `Tới ${fmt(max)} triệu đ`;
  return "Thỏa thuận";
}

export function formatDateVN(iso?: string | null): string {
  if (!iso) return "—";
  const d = new Date(iso.length === 10 ? `${iso}T00:00:00` : iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

export function formatDateTimeVN(iso?: string | null): string {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}
