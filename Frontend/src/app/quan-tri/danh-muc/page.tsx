"use client";

import React, { useCallback, useEffect, useState } from "react";
import { Briefcase, Database, Edit3, MapPin, PlusCircle, Trash2 } from "lucide-react";
import { apiFetch } from "@/lib/apiClient";
import { AlertBanner, RoleGate, StatusBadge } from "@/components/AppShell";
import type { NganhNghe, QuanHuyen } from "@/types/models";

function DanhMucContent() {
  const [tab, setTab] = useState<"nganh" | "quan">("nganh");
  const [nganhList, setNganhList] = useState<NganhNghe[]>([]);
  const [quanList, setQuanList] = useState<QuanHuyen[]>([]);

  // Form Ngành nghề
  const [editNganhId, setEditNganhId] = useState<number | null>(null);
  const [tenNganh, setTenNganh] = useState("");
  const [nhomNganh, setNhomNganh] = useState("");
  const [hoatDong, setHoatDong] = useState(true);

  // Form Quận huyện
  const [editQuanId, setEditQuanId] = useState<string | null>(null);
  const [maQuan, setMaQuan] = useState("");
  const [tenQuan, setTenQuan] = useState("");
  const [khuVuc, setKhuVuc] = useState("Nội thành");
  const [thuTu, setThuTu] = useState(31);

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const loadAll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [nnRes, qhRes] = await Promise.all([
        apiFetch<NganhNghe[]>("/api/quan-tri/nganh-nghe"),
        apiFetch<QuanHuyen[]>("/api/quan-tri/quan-huyen"),
      ]);
      setNganhList(nnRes.duLieu || []);
      setQuanList(qhRes.duLieu || []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không tải được danh mục hệ thống.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadAll();
  }, [loadAll]);

  const handleSaveNganh = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const payload = {
        sTenNganhNghe: tenNganh,
        sNhomNganh: nhomNganh,
        bTrangThaiHoatDong: hoatDong,
      };
      const res = editNganhId
        ? await apiFetch<NganhNghe>(`/api/quan-tri/nganh-nghe/${editNganhId}`, {
            method: "PUT",
            body: JSON.stringify(payload),
          })
        : await apiFetch<NganhNghe>("/api/quan-tri/nganh-nghe", {
            method: "POST",
            body: JSON.stringify(payload),
          });
      setSuccess(res.thongBao);
      setEditNganhId(null);
      setTenNganh("");
      setNhomNganh("");
      setHoatDong(true);
      await loadAll();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Lưu ngành nghề thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteNganh = async (id: number) => {
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const res = await apiFetch<void>(`/api/quan-tri/nganh-nghe/${id}`, { method: "DELETE" });
      setSuccess(res.thongBao);
      await loadAll();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Xử lý xóa ngành nghề thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleSaveQuan = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const payload = {
        pkMaQuanHuyen: maQuan,
        sTenQuanHuyen: tenQuan,
        sKhuVuc: khuVuc,
        iThuTuHienThi: Number(thuTu) || 31,
      };
      const res = editQuanId
        ? await apiFetch<QuanHuyen>(`/api/quan-tri/quan-huyen/${encodeURIComponent(editQuanId)}`, {
            method: "PUT",
            body: JSON.stringify(payload),
          })
        : await apiFetch<QuanHuyen>("/api/quan-tri/quan-huyen", {
            method: "POST",
            body: JSON.stringify(payload),
          });
      setSuccess(res.thongBao);
      setEditQuanId(null);
      setMaQuan("");
      setTenQuan("");
      setKhuVuc("Nội thành");
      setThuTu(31);
      await loadAll();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Lưu quận/huyện thất bại.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteQuan = async (ma: string) => {
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      const res = await apiFetch<void>(`/api/quan-tri/quan-huyen/${encodeURIComponent(ma)}`, {
        method: "DELETE",
      });
      setSuccess(res.thongBao);
      await loadAll();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Không thể xóa quận/huyện.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-[#192536]">Quản trị Danh mục Hệ thống (Ngành nghề &amp; 30 Quận/Huyện)</h1>
        <p className="text-sm text-[#5F6C7B] mt-1">
          Áp dụng quy tắc toàn vẹn dữ liệu R26/R27: danh mục đang được tham chiếu bởi hồ sơ hoặc tin tuyển dụng sẽ tự động chặn xóa cứng.
        </p>
      </div>

      <div className="flex flex-wrap gap-2" role="tablist">
        <button
          type="button"
          onClick={() => setTab("nganh")}
          className={`inline-flex items-center gap-2 px-4 py-2.5 rounded-lg border text-sm font-semibold transition-colors duration-150 ${
            tab === "nganh"
              ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
              : "border-[#DCE2E9] bg-white text-[#5F6C7B]"
          }`}
        >
          <Briefcase className="w-4 h-4" />
          Danh mục Ngành nghề ({nganhList.length})
        </button>
        <button
          type="button"
          onClick={() => setTab("quan")}
          className={`inline-flex items-center gap-2 px-4 py-2.5 rounded-lg border text-sm font-semibold transition-colors duration-150 ${
            tab === "quan"
              ? "border-[#215DA8] bg-[#ECF3FC] text-[#215DA8]"
              : "border-[#DCE2E9] bg-white text-[#5F6C7B]"
          }`}
        >
          <MapPin className="w-4 h-4" />
          Địa bàn 30 Quận/Huyện Hà Nội ({quanList.length})
        </button>
      </div>

      <AlertBanner type="error" message={error} onClose={() => setError(null)} />
      <AlertBanner type="success" message={success} onClose={() => setSuccess(null)} />

      {tab === "nganh" && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          <form onSubmit={handleSaveNganh} className="lg:col-span-4 civic-card p-5 space-y-4 h-fit">
            <h2 className="text-base font-semibold text-[#192536] flex items-center gap-2">
              <Database className="w-4 h-4 text-[#215DA8]" />
              {editNganhId ? `Sửa ngành nghề #${editNganhId}` : "Thêm ngành nghề mới"}
            </h2>
            <div>
              <label htmlFor="tbTenNganh" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                Tên ngành nghề *
              </label>
              <input
                id="tbTenNganh"
                type="text"
                className="civic-input"
                value={tenNganh}
                onChange={(e) => setTenNganh(e.target.value)}
                required
              />
            </div>
            <div>
              <label htmlFor="tbNhomNganh" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                Nhóm ngành
              </label>
              <input
                id="tbNhomNganh"
                type="text"
                className="civic-input"
                value={nhomNganh}
                onChange={(e) => setNhomNganh(e.target.value)}
              />
            </div>
            <label className="inline-flex items-center gap-2 text-sm text-[#192536] cursor-pointer">
              <input
                type="checkbox"
                checked={hoatDong}
                onChange={(e) => setHoatDong(e.target.checked)}
                className="w-4 h-4 accent-[#215DA8]"
              />
              Đang áp dụng trên Sàn
            </label>
            <div className="flex gap-2">
              <button type="submit" disabled={submitting} className="civic-btn-primary flex-1 text-xs">
                <PlusCircle className="w-4 h-4" />
                {editNganhId ? "Cập nhật" : "Thêm mới"}
              </button>
              {editNganhId && (
                <button
                  type="button"
                  onClick={() => {
                    setEditNganhId(null);
                    setTenNganh("");
                    setNhomNganh("");
                  }}
                  className="civic-btn-secondary text-xs"
                >
                  Hủy
                </button>
              )}
            </div>
          </form>

          <section className="lg:col-span-8 civic-card overflow-hidden">
            <div className="overflow-x-auto">
              <table id="dgvNganhNghe" className="civic-table">
                <thead>
                  <tr>
                    <th>Mã</th>
                    <th>Tên ngành nghề</th>
                    <th>Nhóm ngành</th>
                    <th>Trạng thái</th>
                    <th className="text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {loading ? (
                    <tr>
                      <td colSpan={5} className="text-center py-8 text-[#5F6C7B]">
                        Đang tải...
                      </td>
                    </tr>
                  ) : (
                    nganhList.map((nn) => (
                      <tr key={nn.pkMaNganhNghe}>
                        <td className="font-mono tabular-nums">{nn.pkMaNganhNghe}</td>
                        <td className="font-medium text-[#192536]">{nn.sTenNganhNghe}</td>
                        <td className="text-xs text-[#5F6C7B]">{nn.sNhomNganh || "—"}</td>
                        <td>
                          {nn.bTrangThaiHoatDong ? (
                            <StatusBadge variant="success">Đang áp dụng</StatusBadge>
                          ) : (
                            <StatusBadge variant="neutral">Ngưng áp dụng</StatusBadge>
                          )}
                        </td>
                        <td className="text-right">
                          <div className="flex justify-end gap-1.5">
                            <button
                              type="button"
                              onClick={() => {
                                setEditNganhId(nn.pkMaNganhNghe);
                                setTenNganh(nn.sTenNganhNghe);
                                setNhomNganh(nn.sNhomNganh || "");
                                setHoatDong(nn.bTrangThaiHoatDong);
                              }}
                              className="civic-btn-secondary py-1 px-2 text-xs"
                            >
                              <Edit3 className="w-3.5 h-3.5" />
                            </button>
                            <button
                              type="button"
                              disabled={submitting}
                              onClick={() => void handleDeleteNganh(nn.pkMaNganhNghe)}
                              className="civic-btn-secondary py-1 px-2 text-xs text-[#B91C1C]"
                            >
                              <Trash2 className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      )}

      {tab === "quan" && (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          <form onSubmit={handleSaveQuan} className="lg:col-span-4 civic-card p-5 space-y-4 h-fit">
            <h2 className="text-base font-semibold text-[#192536] flex items-center gap-2">
              <MapPin className="w-4 h-4 text-[#215DA8]" />
              {editQuanId ? `Cập nhật ${editQuanId}` : "Thêm đơn vị hành chính"}
            </h2>
            <div>
              <label htmlFor="tbMaQuan" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                Mã Quận/Huyện (QHxxx) *
              </label>
              <input
                id="tbMaQuan"
                type="text"
                className="civic-input font-mono"
                placeholder="QH031"
                value={maQuan}
                disabled={Boolean(editQuanId)}
                onChange={(e) => setMaQuan(e.target.value)}
                required
              />
            </div>
            <div>
              <label htmlFor="tbTenQuan" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                Tên Quận / Huyện / Thị xã *
              </label>
              <input
                id="tbTenQuan"
                type="text"
                className="civic-input"
                value={tenQuan}
                onChange={(e) => setTenQuan(e.target.value)}
                required
              />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label htmlFor="cbbKhuVuc" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                  Khu vực
                </label>
                <select
                  id="cbbKhuVuc"
                  className="civic-input"
                  value={khuVuc}
                  onChange={(e) => setKhuVuc(e.target.value)}
                >
                  <option value="Nội thành">Nội thành</option>
                  <option value="Ngoại thành">Ngoại thành</option>
                </select>
              </div>
              <div>
                <label htmlFor="nudThuTuQuan" className="block text-xs font-semibold text-[#5F6C7B] mb-1">
                  Thứ tự
                </label>
                <input
                  id="nudThuTuQuan"
                  type="number"
                  className="civic-input tabular-nums"
                  value={thuTu}
                  onChange={(e) => setThuTu(Number(e.target.value))}
                />
              </div>
            </div>
            <div className="flex gap-2">
              <button type="submit" disabled={submitting} className="civic-btn-primary flex-1 text-xs">
                <PlusCircle className="w-4 h-4" />
                {editQuanId ? "Cập nhật" : "Thêm mới"}
              </button>
              {editQuanId && (
                <button
                  type="button"
                  onClick={() => {
                    setEditQuanId(null);
                    setMaQuan("");
                    setTenQuan("");
                  }}
                  className="civic-btn-secondary text-xs"
                >
                  Hủy
                </button>
              )}
            </div>
          </form>

          <section className="lg:col-span-8 civic-card overflow-hidden">
            <div className="overflow-x-auto max-h-[540px]">
              <table id="dgvQuanHuyen" className="civic-table">
                <thead>
                  <tr>
                    <th>Mã</th>
                    <th>Tên Quận / Huyện / Thị xã</th>
                    <th>Khu vực</th>
                    <th className="text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {quanList.map((qh) => (
                    <tr key={qh.pkMaQuanHuyen}>
                      <td className="font-mono">{qh.pkMaQuanHuyen}</td>
                      <td className="font-medium text-[#192536]">{qh.sTenQuanHuyen}</td>
                      <td>
                        <StatusBadge variant={qh.sKhuVuc === "Nội thành" ? "info" : "neutral"}>
                          {qh.sKhuVuc || "Hà Nội"}
                        </StatusBadge>
                      </td>
                      <td className="text-right">
                        <div className="flex justify-end gap-1.5">
                          <button
                            type="button"
                            onClick={() => {
                              setEditQuanId(qh.pkMaQuanHuyen);
                              setMaQuan(qh.pkMaQuanHuyen);
                              setTenQuan(qh.sTenQuanHuyen);
                              setKhuVuc(qh.sKhuVuc || "Nội thành");
                              setThuTu(qh.iThuTuHienThi || 1);
                            }}
                            className="civic-btn-secondary py-1 px-2 text-xs"
                          >
                            <Edit3 className="w-3.5 h-3.5" />
                          </button>
                          <button
                            type="button"
                            disabled={submitting}
                            onClick={() => void handleDeleteQuan(qh.pkMaQuanHuyen)}
                            className="civic-btn-secondary py-1 px-2 text-xs text-[#B91C1C]"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      )}
    </div>
  );
}

export default function DanhMucPage() {
  return (
    <RoleGate allowedRoles={[4]}>
      <DanhMucContent />
    </RoleGate>
  );
}
