package vn.gov.hanoi.dvvl.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gov.hanoi.dvvl.Dto.VBaoCaoCungCau;

/**
 * Thống kê Cung - Cầu lao động & Tổng hợp số liệu từ Stored Procedure sp_ThongKeCungCauLaoDong
 * và View vv_ThongKeCungCauTheoNganh trong db_SanGiaoDichViecLamHN (R24, R25).
 */
@Service
public class ThongKeService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public VBaoCaoCungCau.TongHopView layBaoCaoCungCau(
            LocalDate tuNgay,
            LocalDate denNgay,
            Integer maNganhNghe,
            String maQuanHuyen
    ) {
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            throw new BusinessException("Ngày bắt đầu (Từ ngày) không được lớn hơn Ngày kết thúc (Đến ngày).");
        }

        // Gọi Stored Procedure sp_ThongKeCungCauLaoDong
        String sqlQuan = "EXEC dbo.sp_ThongKeCungCauLaoDong @dTuNgay = :tu, @dDenNgay = :den, @iMaNganhNghe = :nganh, @sMaQuanHuyen = :quan";
        List<Object[]> rowsQuan = entityManager.createNativeQuery(sqlQuan)
                .setParameter("tu", tuNgay != null ? Date.valueOf(tuNgay) : null)
                .setParameter("den", denNgay != null ? Date.valueOf(denNgay) : null)
                .setParameter("nganh", maNganhNghe)
                .setParameter("quan", (maQuanHuyen != null && !maQuanHuyen.isBlank()) ? maQuanHuyen.trim() : null)
                .getResultList();

        List<VBaoCaoCungCau.DongTheoQuan> theoQuan = new ArrayList<>();
        int sumCung = 0;
        int sumCau = 0;
        int sumNop = 0;
        int sumTrung = 0;

        for (Object[] r : rowsQuan) {
            VBaoCaoCungCau.DongTheoQuan d = new VBaoCaoCungCau.DongTheoQuan();
            d.setPkMaQuanHuyen(str(r[0]));
            d.setSTenQuanHuyen(str(r[1]));
            d.setSKhuVuc(str(r[2]));
            int cung = num(r[3]);
            int cau = num(r[4]);
            int nop = num(r[5]);
            int trung = num(r[6]);
            d.setITongCung(cung);
            d.setITongCau(cau);
            d.setIChenhLech(cung - cau);
            d.setISoHoSoNop(nop);
            d.setISoTrungTuyen(trung);
            theoQuan.add(d);

            sumCung += cung;
            sumCau += cau;
            sumNop += nop;
            sumTrung += trung;
        }

        // Truy vấn View vv_ThongKeCungCauTheoNganh
        StringBuilder sqlNganh = new StringBuilder("SELECT * FROM dbo.vv_ThongKeCungCauTheoNganh");
        if (maNganhNghe != null) {
            sqlNganh.append(" WHERE PK_MaNganhNghe = :nganh");
        }
        sqlNganh.append(" ORDER BY PK_MaNganhNghe ASC");
        var qNganh = entityManager.createNativeQuery(sqlNganh.toString());
        if (maNganhNghe != null) qNganh.setParameter("nganh", maNganhNghe);
        List<Object[]> rowsNganh = qNganh.getResultList();

        List<VBaoCaoCungCau.DongTheoNganh> theoNganh = new ArrayList<>();
        for (Object[] r : rowsNganh) {
            VBaoCaoCungCau.DongTheoNganh n = new VBaoCaoCungCau.DongTheoNganh();
            n.setPkMaNganhNghe(num(r[0]));
            n.setSTenNganhNghe(str(r[1]));
            n.setSNhomNganh(str(r[2]));
            n.setISoTinTuyenDung(num(r[3]));
            n.setITongCauChiTieu(num(r[4]));
            n.setITongCungHoSo(num(r[5]));
            n.setITongLuotUngTuyen(num(r[6]));
            n.setISoTrungTuyen(num(r[7]));
            theoNganh.add(n);
        }

        int tongDn = num(entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM dbo.tbl_DoanhNghiep WHERE bDaXoa = 0 AND iTrangThaiThamDinh = 1")
                .getSingleResult());
        int tongTinCongKhai = num(entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM dbo.vv_TinTuyenDungCongKhai")
                .getSingleResult());

        VBaoCaoCungCau.TongHopView res = new VBaoCaoCungCau.TongHopView();
        res.setTongCung(sumCung);
        res.setTongCau(sumCau);
        res.setTongHoSoUngTuyen(sumNop);
        res.setTongTrungTuyen(sumTrung);
        res.setTongDoanhNghiepDaXacMinh(tongDn);
        res.setTongTinCongKhai(tongTinCongKhai);
        res.setTheoQuanHuyen(theoQuan);
        res.setTheoNganhNghe(theoNganh);
        return res;
    }

    private String str(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private int num(Object o) {
        return o == null ? 0 : ((Number) o).intValue();
    }
}
