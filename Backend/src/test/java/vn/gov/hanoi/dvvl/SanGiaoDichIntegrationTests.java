package vn.gov.hanoi.dvvl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SanGiaoDichIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String dangNhapLayToken(String email, String matKhau) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "sEmail", email,
                "sMatKhau", matKhau
        ));
        MvcResult result = mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return root.path("duLieu").path("token").asText();
    }

    @Test
    @Order(1)
    void r02_bcrypDangNhapVaPhanQuyenRbac() throws Exception {
        // 1. NTV login & cookie + token check
        String loginBody = objectMapper.writeValueAsString(Map.of(
                "sEmail", "minhanh@dvvlhanoi.vn",
                "sMatKhau", "MatKhau@123"
        ));
        MvcResult ntvLogin = mockMvc.perform(post("/api/auth/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.fkMaVaiTro").value(1))
                .andExpect(jsonPath("$.duLieu.duongDanDich").value("/ung-vien/ho-so"))
                .andReturn();

        String ntvToken = objectMapper.readTree(ntvLogin.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("duLieu").path("token").asText();
        assertThat(ntvToken).isNotBlank();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + ntvToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duLieu.sEmail").value("minhanh@dvvlhanoi.vn"));

        // NTV bị chặn truy cập endpoint Quản trị Admin
        mockMvc.perform(get("/api/quan-tri/tai-khoan")
                        .header("Authorization", "Bearer " + ntvToken))
                .andExpect(status().isForbidden());

        // 2. Doanh nghiệp, Cán bộ DVVL, Admin đăng nhập chuẩn RBAC
        for (Object[] tc : new Object[][]{
                {"tuyendung@minhan.vn", 2, "/doanh-nghiep"},
                {"canbo.lan@dvvlhanoi.gov.vn", 3, "/can-bo"},
                {"admin.quang@dvvlhanoi.gov.vn", 4, "/quan-tri"}
        }) {
            String email = (String) tc[0];
            int expectedRole = (int) tc[1];
            String expectedPath = (String) tc[2];
            String payload = objectMapper.writeValueAsString(Map.of("sEmail", email, "sMatKhau", "MatKhau@123"));
            mockMvc.perform(post("/api/auth/dang-nhap")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.duLieu.fkMaVaiTro").value(expectedRole))
                    .andExpect(jsonPath("$.duLieu.duongDanDich").value(expectedPath));
        }
    }

    @Test
    @Order(2)
    void r01_r03_dangKyOtpVaKhoa15PhutSau5LanSai() throws Exception {
        String tempEmail = "test.it." + System.currentTimeMillis() + "@dvvlhanoi.vn";
        try {
            String regBody = objectMapper.writeValueAsString(Map.of(
                    "fkMaVaiTro", 1,
                    "sEmail", tempEmail,
                    "sMatKhau", "MatKhau@123",
                    "xacNhanMatKhau", "MatKhau@123",
                    "sHoTen", "nguyễn văn kiểm thử",
                    "sSDT", "0988777666"
            ));
            MvcResult regRes = mockMvc.perform(post("/api/auth/dang-ky")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(regBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true))
                    .andExpect(jsonPath("$.duLieu.sEmail").value(tempEmail))
                    .andReturn();

            String otp = objectMapper.readTree(regRes.getResponse().getContentAsString(StandardCharsets.UTF_8))
                    .path("duLieu").path("maOtpMinhHoa").asText();
            assertThat(otp).hasSize(6);

            // Trước khi kích hoạt OTP -> HTTP 403
            String validLoginBody = objectMapper.writeValueAsString(Map.of(
                    "sEmail", tempEmail,
                    "sMatKhau", "MatKhau@123"
            ));
            mockMvc.perform(post("/api/auth/dang-nhap")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validLoginBody))
                    .andExpect(status().isForbidden());

            // Kích hoạt tài khoản bằng OTP
            String otpBody = objectMapper.writeValueAsString(Map.of(
                    "sEmail", tempEmail,
                    "maOtp", otp
            ));
            mockMvc.perform(post("/api/auth/kich-hoat")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(otpBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.thanhCong").value(true));

            // Nhập sai 4 lần -> HTTP 401
            String wrongBody = objectMapper.writeValueAsString(Map.of(
                    "sEmail", tempEmail,
                    "sMatKhau", "SaiMatKhau@999"
            ));
            for (int i = 1; i <= 4; i++) {
                mockMvc.perform(post("/api/auth/dang-nhap")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(wrongBody))
                        .andExpect(status().isUnauthorized());
            }

            // Lần sai thứ 5 -> tự động khóa 15 phút HTTP 423
            mockMvc.perform(post("/api/auth/dang-nhap")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(wrongBody))
                    .andExpect(status().isLocked());

            // Sau khi khóa, nhập đúng mật khẩu vẫn bị từ chối HTTP 423
            mockMvc.perform(post("/api/auth/dang-nhap")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validLoginBody))
                    .andExpect(status().isLocked());
        } finally {
            jdbcTemplate.update(
                    "UPDATE dbo.tbl_TaiKhoan SET bDaXoa = 1, bBiKhoa = 0, iSoLanDangNhapSai = 0, dKhoaDenLuc = NULL, sLyDoKhoa = NULL WHERE sEmail = ?",
                    tempEmail
            );
        }
    }

    @Test
    @Order(3)
    void r18_timKiemViecLamDaTieuChiVaDanhMuc() throws Exception {
        mockMvc.perform(get("/api/tim-viec/danh-muc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andExpect(jsonPath("$.duLieu.quanHuyen.length()").value(30))
                .andExpect(jsonPath("$.duLieu.quanHuyen[0].sTenQuanHuyen").isNotEmpty())
                .andExpect(jsonPath("$.duLieu.nganhNghe.length()").value(12))
                .andExpect(jsonPath("$.duLieu.nganhNghe[0].sTenNganhNghe").isNotEmpty())
                .andExpect(jsonPath("$.duLieu.tongQuan.soTinCongKhai").isNumber());

        MvcResult searchRes = mockMvc.perform(get("/api/tim-viec")
                        .param("tuKhoa", "Kế toán")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thanhCong").value(true))
                .andReturn();

        JsonNode root = objectMapper.readTree(searchRes.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertThat(root.path("duLieu").path("totalItems").asInt()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(4)
    void r09_r17_r25_r28_xuatPdfVaExcelChuanDinhDang() throws Exception {
        String canBoToken = dangNhapLayToken("canbo.lan@dvvlhanoi.gov.vn", "MatKhau@123");
        String adminToken = dangNhapLayToken("admin.quang@dvvlhanoi.gov.vn", "MatKhau@123");

        // Lấy mã ứng viên thực tế từ CSDL
        MvcResult uvRes = mockMvc.perform(get("/api/kiem-duyet/ung-vien")
                        .header("Authorization", "Bearer " + canBoToken))
                .andExpect(status().isOk())
                .andReturn();
        String maUngVien = objectMapper.readTree(uvRes.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("duLieu").get(0).path("pkMaUngVien").asText();
        assertThat(maUngVien).startsWith("SK");

        // Lấy mã tin đang hiển thị công khai từ CSDL
        MvcResult tinRes = mockMvc.perform(get("/api/tim-viec").param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();
        String maTin = objectMapper.readTree(tinRes.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("duLieu").path("items").get(0).path("pkMaTin").asText();
        assertThat(maTin).startsWith("JB");

        // R09: Phiếu đăng ký tìm việc làm PDF A4 kèm QR
        MvcResult r09 = mockMvc.perform(get("/api/nguoi-tim-viec/phieu-dang-ky-pdf/{ma}", maUngVien)
                        .header("Authorization", "Bearer " + canBoToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andReturn();
        byte[] r09Bytes = r09.getResponse().getContentAsByteArray();
        assertThat(new String(r09Bytes, 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");

        // R17: Giấy giới thiệu việc làm PDF A4 kèm số công văn & QR
        MvcResult r17 = mockMvc.perform(get("/api/kiem-duyet/giay-gioi-thieu-pdf")
                        .param("maUngVien", maUngVien)
                        .param("maTin", maTin)
                        .param("ngayHen", "2026-12-01 09:00")
                        .header("Authorization", "Bearer " + canBoToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andReturn();
        byte[] r17Bytes = r17.getResponse().getContentAsByteArray();
        assertThat(new String(r17Bytes, 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");

        // R25 PDF: Báo cáo cung-cầu lao động Hà Nội
        MvcResult r25Pdf = mockMvc.perform(get("/api/thong-ke/xuat-pdf")
                        .header("Authorization", "Bearer " + canBoToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andReturn();
        assertThat(new String(r25Pdf.getResponse().getContentAsByteArray(), 0, 5, StandardCharsets.US_ASCII))
                .isEqualTo("%PDF-");

        // R25 Excel: Báo cáo cung-cầu lao động (.xlsx zip header 'PK')
        MvcResult r25Xlsx = mockMvc.perform(get("/api/thong-ke/xuat-excel")
                        .header("Authorization", "Bearer " + canBoToken))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(new String(r25Xlsx.getResponse().getContentAsByteArray(), 0, 2, StandardCharsets.US_ASCII))
                .isEqualTo("PK");

        // R28 Excel: Kết xuất Nhật ký hệ thống (.xlsx zip header 'PK')
        MvcResult r28Xlsx = mockMvc.perform(get("/api/quan-tri/nhat-ky/xuat-excel")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(new String(r28Xlsx.getResponse().getContentAsByteArray(), 0, 2, StandardCharsets.US_ASCII))
                .isEqualTo("PK");
    }
}
