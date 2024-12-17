package org.yearup.security.limiter;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoleBasedLimitingFilter extends OncePerRequestFilter {

    private static final Map<String, Integer> ROLE_LIMITS = Map.of(
            "ADMIN", 50,
            "USER", 20,
            "GUEST", 10
    );
    private static final long TIME_WINDOW = 5*1000L;
    private final Map<String, ClientRequestInfo> requestCounts = new ConcurrentHashMap<>();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIp = request.getRemoteAddr();
        String userRole = getUserRole();

        int maxRequests = ROLE_LIMITS.getOrDefault(userRole, ROLE_LIMITS.get("GUEST"));

        String clientKey = clientIp + ":" + userRole;

        requestCounts.putIfAbsent(clientKey, new ClientRequestInfo());
        ClientRequestInfo clientInfo = requestCounts.get(clientKey);

        synchronized (clientInfo) {
            long currentTime = Instant.now().toEpochMilli();

            if (currentTime - clientInfo.getStartTime() > TIME_WINDOW) {
                clientInfo.reset(currentTime);
            }

            if (clientInfo.getRequestCount() >= maxRequests) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests - please try again later");
                return;
            }

            clientInfo.incrementRequestCount();
        }

        filterChain.doFilter(request, response);
    }

    private String getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "GUEST";
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            return authority.getAuthority();
        }

        return "USER";
    }
}
