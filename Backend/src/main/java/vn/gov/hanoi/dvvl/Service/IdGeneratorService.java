package vn.gov.hanoi.dvvl.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sinh mã định danh CHAR(10) chuẩn Mục 4.6.3 (US00000001, SK00000001, DN00000001, JB00000001, AP00000001).
 */
@Service
public class IdGeneratorService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public synchronized String nextCode(String tableName, String pkColumn, String prefix) {
        String sql = "SELECT MAX(" + pkColumn + ") FROM dbo." + tableName + " WHERE " + pkColumn + " LIKE :pat";
        Object maxVal = entityManager.createNativeQuery(sql)
                .setParameter("pat", prefix + "%")
                .getSingleResult();
        long nextNum = 1L;
        if (maxVal != null) {
            String raw = maxVal.toString().trim();
            if (raw.startsWith(prefix) && raw.length() > prefix.length()) {
                try {
                    nextNum = Long.parseLong(raw.substring(prefix.length())) + 1L;
                } catch (NumberFormatException ignored) {
                    nextNum = 1L;
                }
            }
        }
        return String.format("%s%08d", prefix, nextNum);
    }
}
