package vn.gov.hanoi.dvvl.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Service;
import vn.gov.hanoi.dvvl.Dto.VBaoCaoCungCau;
import vn.gov.hanoi.dvvl.Model.MDoanhNghiep;
import vn.gov.hanoi.dvvl.Model.MHoSoNguoiTimViec;
import vn.gov.hanoi.dvvl.Model.MTinTuyenDung;

/**
 * Kết xuất PDF khổ A4 (210x297mm) sử dụng Font Times New Roman UTF-8 chuẩn hành chính NĐ 30/2020/NĐ-CP:
 * - R09: Phiếu đăng ký tìm việc làm (kèm mã QR xác thực)
 * - R17: Giấy giới thiệu việc làm
 * - R25: Báo cáo định kỳ Sở LĐ-TB&XH
 */
@Service
public class PdfExportService {

    private static final String FONT_REGULAR = "C:/Windows/Fonts/times.ttf";
    private static final String FONT_BOLD = "C:/Windows/Fonts/timesbd.ttf";
    private static final String FONT_ITALIC = "C:/Windows/Fonts/timesi.ttf";
    private static final DateTimeFormatter VN_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat VN_NUM = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));

    private Font createFont(String path, float size, int style, Color color) throws Exception {
        BaseFont bf = BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        return new Font(bf, size, style, color);
    }

    private Image generateQrImage(String content, int size) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
        ByteArrayOutputStream pngOut = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", pngOut);
        Image img = Image.getInstance(pngOut.toByteArray());
        img.scaleAbsolute(64f, 64f);
        return img;
    }

    public byte[] taoPdfPhieuTimViec(MHoSoNguoiTimViec hs, String tenQuan) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 42, 42, 36, 36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font fReg = createFont(FONT_REGULAR, 11.5f, Font.NORMAL, Color.BLACK);
            Font fBold = createFont(FONT_BOLD, 11.5f, Font.NORMAL, Color.BLACK);
            Font fItalic = createFont(FONT_ITALIC, 11f, Font.NORMAL, Color.DARK_GRAY);
            Font fTitle = createFont(FONT_BOLD, 15f, Font.NORMAL, new Color(33, 93, 168));
            Font fHeaderSmall = createFont(FONT_BOLD, 10.5f, Font.NORMAL, Color.BLACK);

            // Quốc hiệu + Tiêu ngữ + QR xác thực góc phải (R09)
            PdfPTable header = new PdfPTable(new float[]{45f, 40f, 15f});
            header.setWidthPercentage(100);

            PdfPCell cLeft = new PdfPCell();
            cLeft.setBorder(Rectangle.NO_BORDER);
            Paragraph pOrg1 = new Paragraph("SỞ LAO ĐỘNG - TB&XH HÀ NỘI", fHeaderSmall);
            pOrg1.setAlignment(Element.ALIGN_CENTER);
            Paragraph pOrg2 = new Paragraph("TRUNG TÂM DỊCH VỤ VIỆC LÀM HÀ NỘI", fHeaderSmall);
            pOrg2.setAlignment(Element.ALIGN_CENTER);
            Paragraph pCode = new Paragraph("Mã hồ sơ: " + hs.getPkMaUngVien(), fItalic);
            pCode.setAlignment(Element.ALIGN_CENTER);
            cLeft.addElement(pOrg1);
            cLeft.addElement(pOrg2);
            cLeft.addElement(pCode);
            header.addCell(cLeft);

            PdfPCell cMid = new PdfPCell();
            cMid.setBorder(Rectangle.NO_BORDER);
            Paragraph pQuocHieu = new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", fHeaderSmall);
            pQuocHieu.setAlignment(Element.ALIGN_CENTER);
            Paragraph pTieuNgu = new Paragraph("Độc lập - Tự do - Hạnh phúc", fBold);
            pTieuNgu.setAlignment(Element.ALIGN_CENTER);
            Paragraph pGach = new Paragraph("----------------------", fReg);
            pGach.setAlignment(Element.ALIGN_CENTER);
            cMid.addElement(pQuocHieu);
            cMid.addElement(pTieuNgu);
            cMid.addElement(pGach);
            header.addCell(cMid);

            PdfPCell cQr = new PdfPCell(generateQrImage(
                    "DVVLHN|" + hs.getPkMaUngVien() + "|" + hs.getSHoTen() + "|HT:" + hs.getITyLeHoanThien() + "%", 180));
            cQr.setBorder(Rectangle.NO_BORDER);
            cQr.setHorizontalAlignment(Element.ALIGN_RIGHT);
            header.addCell(cQr);
            doc.add(header);

            Paragraph title = new Paragraph("\nPHIẾU ĐĂNG KÝ TÌM VIỆC LÀM\n", fTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            Paragraph sub = new Paragraph("(Ban hành kèm quy trình chuẩn hóa hồ sơ - Trung tâm DVVL Hà Nội)\n\n", fItalic);
            sub.setAlignment(Element.ALIGN_CENTER);
            doc.add(sub);

            doc.add(new Paragraph("I. THÔNG TIN CÁ NHÂN NGƯỜI LAO ĐỘNG", fBold));
            PdfPTable tInfo = new PdfPTable(new float[]{34f, 66f});
            tInfo.setWidthPercentage(100);
            tInfo.setSpacingBefore(6f);
            tInfo.setSpacingAfter(12f);
            addRow(tInfo, "Họ và tên:", hs.getSHoTen(), fBold, fReg);
            addRow(tInfo, "Số CCCD (12 số):", hs.getSSoCCCD(), fBold, fReg);
            addRow(tInfo, "Ngày sinh / Giới tính:",
                    (hs.getDNgaySinh() != null ? hs.getDNgaySinh().format(VN_DATE) : "") + "  —  " + safe(hs.getSGioiTinh()),
                    fBold, fReg);
            addRow(tInfo, "Số điện thoại:", safe(hs.getSSDT()), fBold, fReg);
            addRow(tInfo, "Email liên hệ:", safe(hs.getSEmailLienHe()), fBold, fReg);
            addRow(tInfo, "Quận/Huyện cư trú:", safe(tenQuan) + " — TP. Hà Nội", fBold, fReg);
            doc.add(tInfo);

            doc.add(new Paragraph("II. TRÌNH ĐỘ HỌC VẤN, KỸ NĂNG & KINH NGHIỆM", fBold));
            PdfPTable tSkill = new PdfPTable(new float[]{34f, 66f});
            tSkill.setWidthPercentage(100);
            tSkill.setSpacingBefore(6f);
            tSkill.setSpacingAfter(12f);
            addRow(tSkill, "Trình độ văn hóa:", safe(hs.getSTrinhDoVanHoa()), fBold, fReg);
            addRow(tSkill, "Trình độ chuyên môn:", safe(hs.getSTrinhDoChuyenMon()), fBold, fReg);
            addRow(tSkill, "Kỹ năng tin học:", safe(hs.getSKyNangTinHoc()), fBold, fReg);
            addRow(tSkill, "Trình độ ngoại ngữ:", safe(hs.getSTrinhDoNgoaiNgu()), fBold, fReg);
            addRow(tSkill, "Kinh nghiệm làm việc:", safe(hs.getSKinhNghiemLamViec()), fBold, fReg);
            doc.add(tSkill);

            doc.add(new Paragraph("III. NGUYỆN VỌNG VIỆC LÀM", fBold));
            PdfPTable tWish = new PdfPTable(new float[]{34f, 66f});
            tWish.setWidthPercentage(100);
            tWish.setSpacingBefore(6f);
            tWish.setSpacingAfter(16f);
            addRow(tWish, "Vị trí mong muốn:", safe(hs.getSViTriMongMuon()), fBold, fReg);
            String luong = hs.getFMucLuongKyVong() != null
                    ? VN_NUM.format(hs.getFMucLuongKyVong().longValue()) + " VNĐ / tháng"
                    : "Thỏa thuận";
            addRow(tWish, "Mức lương kỳ vọng:", luong, fBold, fReg);
            addRow(tWish, "Địa bàn mong muốn:", safe(hs.getSDiaBanMongMuon()), fBold, fReg);
            addRow(tWish, "Tỷ lệ hoàn thiện hồ sơ:", hs.getITyLeHoanThien() + "%", fBold, fReg);
            doc.add(tWish);

            LocalDate today = LocalDate.now();
            PdfPTable sign = new PdfPTable(new float[]{50f, 50f});
            sign.setWidthPercentage(100);
            PdfPCell cS1 = new PdfPCell(new Paragraph("CÁN BỘ TIẾP NHẬN HỒ SƠ\n(Ký, ghi rõ họ tên)", fBold));
            cS1.setBorder(Rectangle.NO_BORDER);
            cS1.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell cS2 = new PdfPCell();
            cS2.setBorder(Rectangle.NO_BORDER);
            Paragraph pDate = new Paragraph(
                    String.format("Hà Nội, ngày %02d tháng %02d năm %d", today.getDayOfMonth(), today.getMonthValue(), today.getYear()),
                    fItalic);
            pDate.setAlignment(Element.ALIGN_CENTER);
            Paragraph pSign = new Paragraph("NGƯỜI ĐĂNG KÝ TÌM VIỆC\n(Ký, ghi rõ họ tên)\n\n\n\n" + hs.getSHoTen(), fBold);
            pSign.setAlignment(Element.ALIGN_CENTER);
            cS2.addElement(pDate);
            cS2.addElement(pSign);
            sign.addCell(cS1);
            sign.addCell(cS2);
            doc.add(sign);

            doc.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("Lỗi kết xuất Phiếu đăng ký PDF: " + ex.getMessage());
        }
    }

    public byte[] taoPdfGiayGioiThieu(
            MHoSoNguoiTimViec hs,
            MDoanhNghiep dn,
            MTinTuyenDung tin,
            String tenQuanUv,
            String tenQuanDn,
            String soCongVan,
            String ngayHen,
            String ghiChu,
            String tenCanBo
    ) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 42, 42, 36, 36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font fReg = createFont(FONT_REGULAR, 12f, Font.NORMAL, Color.BLACK);
            Font fBold = createFont(FONT_BOLD, 12f, Font.NORMAL, Color.BLACK);
            Font fItalic = createFont(FONT_ITALIC, 11.5f, Font.NORMAL, Color.DARK_GRAY);
            Font fTitle = createFont(FONT_BOLD, 16f, Font.NORMAL, new Color(33, 93, 168));
            Font fHeaderSmall = createFont(FONT_BOLD, 10.5f, Font.NORMAL, Color.BLACK);

            PdfPTable header = new PdfPTable(new float[]{45f, 40f, 15f});
            header.setWidthPercentage(100);

            PdfPCell cLeft = new PdfPCell();
            cLeft.setBorder(Rectangle.NO_BORDER);
            Paragraph pOrg1 = new Paragraph("SỞ LAO ĐỘNG - TB&XH TP. HÀ NỘI", fHeaderSmall);
            pOrg1.setAlignment(Element.ALIGN_CENTER);
            Paragraph pOrg2 = new Paragraph("TRUNG TÂM DỊCH VỤ VIỆC LÀM HÀ NỘI", fHeaderSmall);
            pOrg2.setAlignment(Element.ALIGN_CENTER);
            Paragraph pRef = new Paragraph("Số: " + soCongVan, fReg);
            pRef.setAlignment(Element.ALIGN_CENTER);
            cLeft.addElement(pOrg1);
            cLeft.addElement(pOrg2);
            cLeft.addElement(pRef);
            header.addCell(cLeft);

            PdfPCell cMid = new PdfPCell();
            cMid.setBorder(Rectangle.NO_BORDER);
            Paragraph pQuocHieu = new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", fHeaderSmall);
            pQuocHieu.setAlignment(Element.ALIGN_CENTER);
            Paragraph pTieuNgu = new Paragraph("Độc lập - Tự do - Hạnh phúc", fBold);
            pTieuNgu.setAlignment(Element.ALIGN_CENTER);
            Paragraph pGach = new Paragraph("----------------------", fReg);
            pGach.setAlignment(Element.ALIGN_CENTER);
            cMid.addElement(pQuocHieu);
            cMid.addElement(pTieuNgu);
            cMid.addElement(pGach);
            header.addCell(cMid);

            PdfPCell cQr = new PdfPCell(generateQrImage(
                    soCongVan + "|" + hs.getPkMaUngVien() + "|" + dn.getPkMaDoanhNghiep() + "|" + tin.getPkMaTin(), 180));
            cQr.setBorder(Rectangle.NO_BORDER);
            cQr.setHorizontalAlignment(Element.ALIGN_RIGHT);
            header.addCell(cQr);
            doc.add(header);

            Paragraph title = new Paragraph("\nGIẤY GIỚI THIỆU VIỆC LÀM\n", fTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            Paragraph sub = new Paragraph("Về việc giới thiệu người lao động đến dự tuyển việc làm\n\n", fItalic);
            sub.setAlignment(Element.ALIGN_CENTER);
            doc.add(sub);

            Paragraph pKinhGui = new Paragraph("Kính gửi: " + dn.getSTenDoanhNghiep(), fBold);
            pKinhGui.setSpacingAfter(6f);
            doc.add(pKinhGui);

            doc.add(new Paragraph("Mã số thuế: " + safe(dn.getSMaSoThue()) + "   |   Địa chỉ: " + safe(dn.getSDiaChiTruSo()) + ", " + safe(tenQuanDn), fReg));
            Paragraph intro = new Paragraph(
                    "\nCăn cứ nhu cầu tuyển dụng của Quý doanh nghiệp và nguyện vọng tìm việc làm của người lao động, "
                    + "Trung tâm Dịch vụ việc làm Hà Nội trân trọng giới thiệu ông/bà có thông tin dưới đây đến liên hệ dự tuyển:",
                    fReg);
            intro.setSpacingAfter(10f);
            doc.add(intro);

            PdfPTable tbl = new PdfPTable(new float[]{34f, 66f});
            tbl.setWidthPercentage(100);
            tbl.setSpacingAfter(12f);
            addRow(tbl, "Ông / Bà:", hs.getSHoTen() + "  (" + hs.getPkMaUngVien() + ")", fBold, fReg);
            addRow(tbl, "Số CCCD:", hs.getSSoCCCD(), fBold, fReg);
            addRow(tbl, "Ngày sinh / Giới tính:",
                    (hs.getDNgaySinh() != null ? hs.getDNgaySinh().format(VN_DATE) : "") + " — " + safe(hs.getSGioiTinh()),
                    fBold, fReg);
            addRow(tbl, "Số điện thoại:", safe(hs.getSSDT()), fBold, fReg);
            addRow(tbl, "Địa chỉ cư trú:", safe(tenQuanUv) + " — Hà Nội", fBold, fReg);
            addRow(tbl, "Trình độ chuyên môn:", safe(hs.getSTrinhDoChuyenMon()), fBold, fReg);
            addRow(tbl, "Vị trí giới thiệu:", tin.getSViTriChucDanh() + " (" + tin.getPkMaTin() + ")", fBold, fReg);
            addRow(tbl, "Thời gian hẹn dự tuyển:", (ngayHen != null && !ngayHen.isBlank()) ? ngayHen : "Theo lịch hẹn của Quý doanh nghiệp", fBold, fReg);
            addRow(tbl, "Ghi chú nghiệp vụ:", (ghiChu != null && !ghiChu.isBlank()) ? ghiChu : "Đề nghị Quý doanh nghiệp tiếp nhận, phỏng vấn và phản hồi kết quả về Trung tâm.", fBold, fReg);
            doc.add(tbl);

            Paragraph pVal = new Paragraph("Giấy giới thiệu có giá trị trong thời hạn 15 ngày kể từ ngày cấp.\n\n", fItalic);
            doc.add(pVal);

            LocalDate today = LocalDate.now();
            PdfPTable sign = new PdfPTable(new float[]{45f, 55f});
            sign.setWidthPercentage(100);
            PdfPCell cLeftS = new PdfPCell(new Paragraph("CÁN BỘ TƯ VẤN GIỚI THIỆU\n\n\n\n" + safe(tenCanBo), fBold));
            cLeftS.setBorder(Rectangle.NO_BORDER);
            cLeftS.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell cRightS = new PdfPCell();
            cRightS.setBorder(Rectangle.NO_BORDER);
            Paragraph pDate = new Paragraph(
                    String.format("Hà Nội, ngày %02d tháng %02d năm %d", today.getDayOfMonth(), today.getMonthValue(), today.getYear()),
                    fItalic);
            pDate.setAlignment(Element.ALIGN_CENTER);
            Paragraph pDir = new Paragraph("KT. GIÁM ĐỐC TRUNG TÂM\n(Ký tên và đóng dấu)", fBold);
            pDir.setAlignment(Element.ALIGN_CENTER);
            cRightS.addElement(pDate);
            cRightS.addElement(pDir);
            sign.addCell(cLeftS);
            sign.addCell(cRightS);
            doc.add(sign);

            doc.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("Lỗi kết xuất Giấy giới thiệu việc làm PDF: " + ex.getMessage());
        }
    }

    public byte[] xuatBaoCaoPdf(VBaoCaoCungCau.TongHopView report, String tuNgay, String denNgay, String nguoiLap) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 32, 32);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font fReg = createFont(FONT_REGULAR, 10.5f, Font.NORMAL, Color.BLACK);
            Font fBold = createFont(FONT_BOLD, 10.5f, Font.NORMAL, Color.BLACK);
            Font fItalic = createFont(FONT_ITALIC, 10.5f, Font.NORMAL, Color.DARK_GRAY);
            Font fTitle = createFont(FONT_BOLD, 14f, Font.NORMAL, new Color(33, 93, 168));
            Font fHeaderSmall = createFont(FONT_BOLD, 10f, Font.NORMAL, Color.BLACK);

            PdfPTable header = new PdfPTable(new float[]{48f, 52f});
            header.setWidthPercentage(100);
            PdfPCell c1 = new PdfPCell(new Paragraph("SỞ LAO ĐỘNG - TB&XH TP. HÀ NỘI\nTRUNG TÂM DỊCH VỤ VIỆC LÀM HÀ NỘI", fHeaderSmall));
            c1.setBorder(Rectangle.NO_BORDER);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell c2 = new PdfPCell(new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\nĐộc lập - Tự do - Hạnh phúc", fHeaderSmall));
            c2.setBorder(Rectangle.NO_BORDER);
            c2.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.addCell(c1);
            header.addCell(c2);
            doc.add(header);

            Paragraph title = new Paragraph("\nBÁO CÁO ĐỊNH KỲ TÌNH HÌNH CUNG - CẦU LAO ĐỘNG HÀ NỘI", fTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            String ky = "Kỳ báo cáo: Từ " + (tuNgay != null && !tuNgay.isBlank() ? tuNgay : "Đầu kỳ")
                    + " đến " + (denNgay != null && !denNgay.isBlank() ? denNgay : "Hiện tại");
            Paragraph pKy = new Paragraph(ky + "\n\n", fItalic);
            pKy.setAlignment(Element.ALIGN_CENTER);
            doc.add(pKy);

            doc.add(new Paragraph("1. CHỈ TIÊU TỔNG HỢP THỊ TRƯỜNG LAO ĐỘNG", fBold));
            PdfPTable tKpi = new PdfPTable(new float[]{50f, 50f});
            tKpi.setWidthPercentage(100);
            tKpi.setSpacingBefore(6f);
            tKpi.setSpacingAfter(10f);
            addRow(tKpi, "Tổng nguồn cung hồ sơ người tìm việc:", report.getTongCung() + " lao động", fBold, fReg);
            addRow(tKpi, "Tổng nhu cầu tuyển dụng (chỉ tiêu):", report.getTongCau() + " vị trí", fBold, fReg);
            addRow(tKpi, "Tổng lượt nộp hồ sơ ứng tuyển:", report.getTongHoSoUngTuyen() + " lượt", fBold, fReg);
            addRow(tKpi, "Số lao động trúng tuyển kết nối thành công:", report.getTongTrungTuyen() + " người", fBold, fReg);
            addRow(tKpi, "Số doanh nghiệp đã xác minh pháp lý:", report.getTongDoanhNghiepDaXacMinh() + " doanh nghiệp", fBold, fReg);
            doc.add(tKpi);

            doc.add(new Paragraph("2. THỐNG KÊ CUNG - CẦU THEO NGÀNH NGHỀ", fBold));
            PdfPTable tNganh = new PdfPTable(new float[]{40f, 15f, 15f, 15f, 15f});
            tNganh.setWidthPercentage(100);
            tNganh.setSpacingBefore(6f);
            tNganh.setSpacingAfter(12f);
            addHeaderRow(tNganh, new String[]{"Ngành nghề", "Số tin", "Cầu (chỉ tiêu)", "Cung (hồ sơ)", "Trúng tuyển"}, fBold);
            if (report.getTheoNganhNghe() != null) {
                for (var r : report.getTheoNganhNghe()) {
                    addDataRow(tNganh, new String[]{
                        r.getSTenNganhNghe(),
                        String.valueOf(r.getISoTinTuyenDung()),
                        String.valueOf(r.getITongCauChiTieu()),
                        String.valueOf(r.getITongCungHoSo()),
                        String.valueOf(r.getISoTrungTuyen())
                    }, fReg);
                }
            }
            doc.add(tNganh);

            doc.add(new Paragraph("3. THỐNG KÊ CUNG - CẦU THEO ĐỊA BÀN QUẬN / HUYỆN HÀ NỘI", fBold));
            PdfPTable tQuan = new PdfPTable(new float[]{34f, 18f, 12f, 12f, 12f, 12f});
            tQuan.setWidthPercentage(100);
            tQuan.setSpacingBefore(6f);
            tQuan.setSpacingAfter(14f);
            addHeaderRow(tQuan, new String[]{"Quận / Huyện", "Khu vực", "Cung", "Cầu", "Hồ sơ nộp", "Trúng tuyển"}, fBold);
            if (report.getTheoQuanHuyen() != null) {
                for (var q : report.getTheoQuanHuyen()) {
                    addDataRow(tQuan, new String[]{
                        q.getSTenQuanHuyen(),
                        safe(q.getSKhuVuc()),
                        String.valueOf(q.getITongCung()),
                        String.valueOf(q.getITongCau()),
                        String.valueOf(q.getISoHoSoNop()),
                        String.valueOf(q.getISoTrungTuyen())
                    }, fReg);
                }
            }
            doc.add(tQuan);

            LocalDate today = LocalDate.now();
            Paragraph foot = new Paragraph(
                    String.format("Hà Nội, ngày %02d tháng %02d năm %d\nNGƯỜI LẬP BÁO CÁO\n\n\n%s",
                            today.getDayOfMonth(), today.getMonthValue(), today.getYear(), safe(nguoiLap)),
                    fBold);
            foot.setAlignment(Element.ALIGN_RIGHT);
            doc.add(foot);

            doc.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("Lỗi xuất Báo cáo PDF: " + ex.getMessage());
        }
    }

    private void addRow(PdfPTable table, String label, String value, Font fLabel, Font fVal) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fLabel));
        c1.setPadding(6f);
        c1.setBackgroundColor(new Color(246, 248, 251));
        c1.setBorderColor(new Color(220, 226, 233));
        PdfPCell c2 = new PdfPCell(new Phrase(value != null ? value : "", fVal));
        c2.setPadding(6f);
        c2.setBorderColor(new Color(220, 226, 233));
        table.addCell(c1);
        table.addCell(c2);
    }

    private void addHeaderRow(PdfPTable table, String[] headers, Font fBold) {
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, fBold));
            c.setPadding(5f);
            c.setBackgroundColor(new Color(236, 243, 252));
            c.setBorderColor(new Color(220, 226, 233));
            table.addCell(c);
        }
    }

    private void addDataRow(PdfPTable table, String[] cols, Font fReg) {
        for (int i = 0; i < cols.length; i++) {
            PdfPCell c = new PdfPCell(new Phrase(cols[i], fReg));
            c.setPadding(4.5f);
            c.setBorderColor(new Color(220, 226, 233));
            if (i >= 2) c.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
