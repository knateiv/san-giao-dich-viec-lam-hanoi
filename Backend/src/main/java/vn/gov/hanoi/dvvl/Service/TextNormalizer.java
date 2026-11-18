package vn.gov.hanoi.dvvl.Service;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Chuẩn hóa chuỗi Unicode tiếng Việt (UTF-8 NFC), chống lỗi dính chữ (ISS-04 / R06),
 * chuẩn hóa tên riêng và che thông tin nhạy cảm theo Nghị định 13/2023/NĐ-CP (R19).
 */
public final class TextNormalizer {

    private static final Locale VI_LOCALE = Locale.forLanguageTag("vi-VN");

    private TextNormalizer() {}

    public static String normalizeText(String input) {
        if (input == null) return null;
        String nfc = Normalizer.normalize(input, Normalizer.Form.NFC);
        // Xóa ký tự điều khiển và chuẩn hóa khoảng trắng tránh dính chữ
        String cleaned = nfc.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return cleaned;
    }

    public static String normalizeMultiline(String input) {
        if (input == null) return null;
        String nfc = Normalizer.normalize(input, Normalizer.Form.NFC);
        return nfc.replaceAll("[ \\t]+", " ").trim();
    }

    public static String normalizeProperName(String input) {
        String cleaned = normalizeText(input);
        if (cleaned == null || cleaned.isEmpty()) return cleaned;
        String[] words = cleaned.toLowerCase(VI_LOCALE).split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (w.isEmpty()) continue;
            if (i > 0) sb.append(' ');
            sb.append(w.substring(0, 1).toUpperCase(VI_LOCALE)).append(w.substring(1));
        }
        return sb.toString();
    }

    public static String maskCccd(String cccd) {
        if (cccd == null || cccd.length() < 4) return "************";
        return "********" + cccd.substring(cccd.length() - 4);
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "**********";
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 3);
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***@***";
        int at = email.indexOf('@');
        String local = email.substring(0, at);
        String domain = email.substring(at);
        String prefix = local.length() <= 2 ? local.substring(0, 1) : local.substring(0, 2);
        return prefix + "****" + domain;
    }
}
