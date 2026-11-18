package vn.gov.hanoi.dvvl.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import vn.gov.hanoi.dvvl.Dto.VBaoCaoCungCau;
import vn.gov.hanoi.dvvl.Dto.VNhatKyHeThong;

/**
 * Kết xuất Bảng tính Excel (.xlsx) với công thức tổng hợp động phục vụ Sở LĐ-TB&XH (R25)
 * và xuất Nhật ký kiểm toán hệ thống (R28).
 */
@Service
public class ExcelExportService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] xuatBaoCaoExcel(VBaoCaoCungCau.TongHopView report, String tuNgay, String denNgay) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle titleStyle = createHeaderStyle(wb, 13, false);
            CellStyle headStyle = createHeaderStyle(wb, 11, true);
            CellStyle cellStyle = createDataStyle(wb);
            CellStyle totalStyle = createHeaderStyle(wb, 11, true);

            // Sheet 1: Báo cáo theo Địa bàn Quận/Huyện
            Sheet sQuan = wb.createSheet("BaoCao_QuanHuyen");
            sQuan.createRow(0).createCell(0).setCellValue("TRUNG TÂM DỊCH VỤ VIỆC LÀM HÀ NỘI - BÁO CÁO CUNG CẦU LAO ĐỘNG THEO QUẬN/HUYỆN");
            sQuan.getRow(0).getCell(0).setCellStyle(titleStyle);
            sQuan.createRow(1).createCell(0).setCellValue("Kỳ báo cáo: " + safe(tuNgay, "Đầu kỳ") + " -> " + safe(denNgay, "Hiện tại"));

            String[] qHeaders = {"Mã quận/huyện", "Tên quận / huyện", "Khu vực", "Cung (Hồ sơ NTV)", "Cầu (Chỉ tiêu)", "Chênh lệch", "Lượt nộp hồ sơ", "Trúng tuyển"};
            Row hRow = sQuan.createRow(3);
            for (int i = 0; i < qHeaders.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(qHeaders[i]);
                c.setCellStyle(headStyle);
            }

            int rIdx = 4;
            if (report.getTheoQuanHuyen() != null) {
                for (var q : report.getTheoQuanHuyen()) {
                    Row r = sQuan.createRow(rIdx++);
                    setCell(r, 0, q.getPkMaQuanHuyen(), cellStyle);
                    setCell(r, 1, q.getSTenQuanHuyen(), cellStyle);
                    setCell(r, 2, q.getSKhuVuc(), cellStyle);
                    setNumCell(r, 3, q.getITongCung(), cellStyle);
                    setNumCell(r, 4, q.getITongCau(), cellStyle);
                    setNumCell(r, 5, q.getIChenhLech(), cellStyle);
                    setNumCell(r, 6, q.getISoHoSoNop(), cellStyle);
                    setNumCell(r, 7, q.getISoTrungTuyen(), cellStyle);
                }
            }
            if (rIdx > 4) {
                Row totalRow = sQuan.createRow(rIdx);
                setCell(totalRow, 0, "", totalStyle);
                setCell(totalRow, 1, "TỔNG CỘNG TOÀN THÀNH PHỐ", totalStyle);
                setCell(totalRow, 2, "", totalStyle);
                for (int col = 3; col <= 7; col++) {
                    char colLetter = (char) ('A' + col);
                    Cell c = totalRow.createCell(col);
                    c.setCellFormula("SUM(" + colLetter + "5:" + colLetter + rIdx + ")");
                    c.setCellStyle(totalStyle);
                }
            }
            for (int i = 0; i < qHeaders.length; i++) sQuan.autoSizeColumn(i);

            // Sheet 2: Báo cáo theo Ngành nghề
            Sheet sNganh = wb.createSheet("BaoCao_NganhNghe");
            sNganh.createRow(0).createCell(0).setCellValue("TRUNG TÂM DỊCH VỤ VIỆC LÀM HÀ NỘI - BÁO CÁO CUNG CẦU THEO NGÀNH NGHỀ");
            sNganh.getRow(0).getCell(0).setCellStyle(titleStyle);

            String[] nHeaders = {"Mã ngành", "Tên ngành nghề", "Nhóm ngành", "Số tin công khai", "Cầu (Chỉ tiêu)", "Cung (Hồ sơ)", "Lượt ứng tuyển", "Trúng tuyển"};
            Row hnRow = sNganh.createRow(2);
            for (int i = 0; i < nHeaders.length; i++) {
                Cell c = hnRow.createCell(i);
                c.setCellValue(nHeaders[i]);
                c.setCellStyle(headStyle);
            }

            int nIdx = 3;
            if (report.getTheoNganhNghe() != null) {
                for (var n : report.getTheoNganhNghe()) {
                    Row r = sNganh.createRow(nIdx++);
                    setNumCell(r, 0, n.getPkMaNganhNghe(), cellStyle);
                    setCell(r, 1, n.getSTenNganhNghe(), cellStyle);
                    setCell(r, 2, n.getSNhomNganh(), cellStyle);
                    setNumCell(r, 3, n.getISoTinTuyenDung(), cellStyle);
                    setNumCell(r, 4, n.getITongCauChiTieu(), cellStyle);
                    setNumCell(r, 5, n.getITongCungHoSo(), cellStyle);
                    setNumCell(r, 6, n.getITongLuotUngTuyen(), cellStyle);
                    setNumCell(r, 7, n.getISoTrungTuyen(), cellStyle);
                }
            }
            if (nIdx > 3) {
                Row totalRow = sNganh.createRow(nIdx);
                setCell(totalRow, 0, "", totalStyle);
                setCell(totalRow, 1, "TỔNG CỘNG THEO NGÀNH", totalStyle);
                setCell(totalRow, 2, "", totalStyle);
                for (int col = 3; col <= 7; col++) {
                    char colLetter = (char) ('A' + col);
                    Cell c = totalRow.createCell(col);
                    c.setCellFormula("SUM(" + colLetter + "4:" + colLetter + nIdx + ")");
                    c.setCellStyle(totalStyle);
                }
            }
            for (int i = 0; i < nHeaders.length; i++) sNganh.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("Lỗi xuất Báo cáo Excel: " + ex.getMessage());
        }
    }

    public byte[] xuatNhatKyExcel(List<VNhatKyHeThong> logs) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headStyle = createHeaderStyle(wb, 11, true);
            CellStyle cellStyle = createDataStyle(wb);
            Sheet sheet = wb.createSheet("NhatKy_HeThong");

            String[] headers = {"Mã nhật ký", "Thời gian ghi", "Mã tài khoản", "Người thực hiện", "Email", "Hành động", "Mức độ", "Địa chỉ IP", "Thiết bị", "Dữ liệu cũ", "Dữ liệu mới"};
            Row hRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headStyle);
            }

            int idx = 1;
            for (var l : logs) {
                Row r = sheet.createRow(idx++);
                setNumCell(r, 0, l.getPkMaNhatKy() != null ? l.getPkMaNhatKy().intValue() : 0, cellStyle);
                setCell(r, 1, l.getDThoiGianGhi() != null ? l.getDThoiGianGhi().format(DT_FMT) : "", cellStyle);
                setCell(r, 2, l.getFkMaTaiKhoan(), cellStyle);
                setCell(r, 3, l.getTenNguoiThucHien(), cellStyle);
                setCell(r, 4, l.getSEmail(), cellStyle);
                setCell(r, 5, l.getSLoaiHanhDong(), cellStyle);
                setCell(r, 6, l.getSMucDo(), cellStyle);
                setCell(r, 7, l.getSDiaChiIP(), cellStyle);
                setCell(r, 8, l.getSTrinhDuyet(), cellStyle);
                setCell(r, 9, l.getSDuLieuCu(), cellStyle);
                setCell(r, 10, l.getSDuLieuMoi(), cellStyle);
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("Lỗi xuất Audit Log Excel: " + ex.getMessage());
        }
    }

    private CellStyle createHeaderStyle(Workbook wb, int fontSize, boolean shaded) {
        CellStyle st = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) fontSize);
        f.setFontName("Times New Roman");
        st.setFont(f);
        if (shaded) {
            st.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            st.setBorderBottom(BorderStyle.THIN);
            st.setBorderTop(BorderStyle.THIN);
            st.setBorderLeft(BorderStyle.THIN);
            st.setBorderRight(BorderStyle.THIN);
        }
        return st;
    }

    private CellStyle createDataStyle(Workbook wb) {
        CellStyle st = wb.createCellStyle();
        Font f = wb.createFont();
        f.setFontHeightInPoints((short) 11);
        f.setFontName("Times New Roman");
        st.setFont(f);
        st.setBorderBottom(BorderStyle.THIN);
        st.setBorderTop(BorderStyle.THIN);
        st.setBorderLeft(BorderStyle.THIN);
        st.setBorderRight(BorderStyle.THIN);
        return st;
    }

    private void setCell(Row r, int col, String val, CellStyle st) {
        Cell c = r.createCell(col);
        c.setCellValue(val != null ? val : "");
        c.setCellStyle(st);
    }

    private void setNumCell(Row r, int col, int val, CellStyle st) {
        Cell c = r.createCell(col);
        c.setCellValue(val);
        c.setCellStyle(st);
    }

    private String safe(String v, String fallback) {
        return (v != null && !v.isBlank()) ? v : fallback;
    }
}
