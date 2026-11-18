package vn.gov.hanoi.dvvl.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadRoot.resolve("cv"));
            Files.createDirectories(uploadRoot.resolve("gp"));
            Files.createDirectories(uploadRoot.resolve("avatars"));
        } catch (IOException ex) {
            throw new BusinessException("Không thể khởi tạo thư mục lưu trữ tệp: " + ex.getMessage());
        }
    }

    public String savePdfCv(String maUngVien, MultipartFile file) {
        validatePdf(file, 5 * 1024 * 1024L, "Tệp CV");
        String fileName = "CV_" + maUngVien + "_" + Instant.now().toEpochMilli() + ".pdf";
        writeFile(uploadRoot.resolve("cv").resolve(fileName), file);
        return "cv/" + fileName;
    }

    public String saveGiayPhep(String maDoanhNghiep, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Vui lòng chọn tệp Giấy phép đăng ký kinh doanh.");
        }
        if (file.getSize() > 5 * 1024 * 1024L) {
            throw new BusinessException("Dung lượng tệp Giấy phép ĐKKD tối đa 5MB.");
        }
        String ext = detectPdfOrImageExt(file);
        String fileName = "GPKD_" + maDoanhNghiep + "_" + Instant.now().toEpochMilli() + ext;
        writeFile(uploadRoot.resolve("gp").resolve(fileName), file);
        return "gp/" + fileName;
    }

    public String saveAvatar(String maTaiKhoan, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Vui lòng chọn ảnh đại diện.");
        }
        if (file.getSize() > 2 * 1024 * 1024L) {
            throw new BusinessException("Ảnh đại diện phải có dung lượng tối đa 2MB.");
        }
        String ext = detectImageExt(file);
        String fileName = "AVT_" + maTaiKhoan + "_" + Instant.now().toEpochMilli() + ext;
        writeFile(uploadRoot.resolve("avatars").resolve(fileName), file);
        return "avatars/" + fileName;
    }

    public Resource loadAsResource(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new BusinessException("Không tìm thấy tệp đính kèm.");
        }
        try {
            Path target = uploadRoot.resolve(relativePath).normalize();
            if (!target.startsWith(uploadRoot) || !Files.exists(target)) {
                throw new BusinessException("Tệp đính kèm không tồn tại trên máy chủ.");
            }
            return new UrlResource(target.toUri());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Không thể đọc tệp đính kèm: " + ex.getMessage());
        }
    }

    private void validatePdf(MultipartFile file, long maxBytes, String label) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Vui lòng chọn " + label + " (định dạng PDF).");
        }
        if (file.getSize() > maxBytes) {
            throw new BusinessException(label + " vượt quá dung lượng tối đa 5MB.");
        }
        try {
            byte[] header = new byte[5];
            int read = file.getInputStream().read(header);
            if (read < 5 || header[0] != '%' || header[1] != 'P' || header[2] != 'D' || header[3] != 'F' || header[4] != '-') {
                throw new BusinessException(label + " bắt buộc phải là tệp PDF hợp lệ (kiểm tra Magic Bytes).");
            }
        } catch (IOException ex) {
            throw new BusinessException("Không thể kiểm tra tệp tải lên.");
        }
    }

    private String detectPdfOrImageExt(MultipartFile file) {
        try {
            byte[] h = new byte[8];
            int r = file.getInputStream().read(h);
            if (r >= 5 && h[0] == '%' && h[1] == 'P' && h[2] == 'D' && h[3] == 'F' && h[4] == '-') {
                return ".pdf";
            }
            return detectImageExtFromBytes(h, r);
        } catch (IOException ex) {
            throw new BusinessException("Lỗi đọc nội dung tệp Giấy phép ĐKKD.");
        }
    }

    private String detectImageExt(MultipartFile file) {
        try {
            byte[] h = new byte[8];
            int r = file.getInputStream().read(h);
            return detectImageExtFromBytes(h, r);
        } catch (IOException ex) {
            throw new BusinessException("Lỗi đọc nội dung ảnh tải lên.");
        }
    }

    private String detectImageExtFromBytes(byte[] h, int r) {
        if (r >= 8 && (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G') {
            return ".png";
        }
        if (r >= 3 && (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF) {
            return ".jpg";
        }
        throw new BusinessException("Định dạng tệp không hợp lệ. Chỉ chấp nhận PDF, PNG hoặc JPG.");
    }

    private void writeFile(Path destination, MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BusinessException("Lỗi lưu tệp vào máy chủ: " + ex.getMessage());
        }
    }
}
