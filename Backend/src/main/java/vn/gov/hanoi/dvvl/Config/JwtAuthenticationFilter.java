package vn.gov.hanoi.dvvl.Config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.gov.hanoi.dvvl.Model.MTaiKhoan;
import vn.gov.hanoi.dvvl.Repository.TaiKhoanRepository;
import vn.gov.hanoi.dvvl.Service.JwtService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TaiKhoanRepository taiKhoanRepo;

    public JwtAuthenticationFilter(JwtService jwtService, TaiKhoanRepository taiKhoanRepo) {
        this.jwtService = jwtService;
        this.taiKhoanRepo = taiKhoanRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null && !token.isBlank()) {
            try {
                Claims claims = jwtService.parseClaims(token);
                String userId = claims.getSubject();
                Integer tokenRoleId = claims.get("roleId", Integer.class);
                Optional<MTaiKhoan> opt = taiKhoanRepo.findActiveById(userId);
                if (opt.isPresent()) {
                    MTaiKhoan tk = opt.get();
                    boolean tempLocked = Boolean.TRUE.equals(tk.getBBiKhoa())
                            && (tk.getDKhoaDenLuc() == null || LocalDateTime.now().isBefore(tk.getDKhoaDenLuc()));
                    // R05: Nếu bị khóa hoặc vai trò bị Admin thay đổi -> hủy hiệu lực token cũ ngay
                    if (!tempLocked && tokenRoleId != null && tokenRoleId.equals(tk.getFkMaVaiTro())) {
                        String authority = switch (tk.getFkMaVaiTro()) {
                            case 1 -> "ROLE_NTV";
                            case 2 -> "ROLE_DN";
                            case 3 -> "ROLE_CANBO";
                            case 4 -> "ROLE_ADMIN";
                            default -> "ROLE_USER";
                        };
                        UserPrincipal principal = new UserPrincipal(
                                tk.getPkMaTaiKhoan(),
                                tk.getFkMaVaiTro(),
                                tk.getSEmail(),
                                tk.getSHoTen()
                        );
                        var auth = new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                List.of(new SimpleGrantedAuthority(authority))
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("DVVL_TOKEN".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
