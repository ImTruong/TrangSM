package com.example.demo.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class UserHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
            .filter(principal -> principal instanceof JwtAuthenticationToken)
            .cast(JwtAuthenticationToken.class)
            .map(jwtToken -> {
                // 1. Lấy User ID (sub)
                String userId = jwtToken.getToken().getClaimAsString("sub");

                // 2. Lấy Username/Số điện thoại (preferred_username)
                String username = jwtToken.getToken().getClaimAsString("preferred_username");

                // 3. Lấy danh sách Roles từ realm_access.roles
                Map<String, Object> realmAccess = jwtToken.getToken().getClaimAsMap("realm_access");
                String rolesString = "";
                if (realmAccess != null && realmAccess.get("roles") instanceof List<?> rolesList) {
                    // Chuyển danh sách [USER] thành chuỗi "USER"
                    rolesString = String.join(",", rolesList.stream().map(Object::toString).toList());
                }

                // 4. Inject vào Header và chuyển tiếp exchange mới
                return exchange.mutate()
                    .request(exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Name", username)
                        .header("X-User-Roles", rolesString)
                        .build())
                    .build();
            })
            .defaultIfEmpty(exchange)
            .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        // Chạy sau khi Security đã xác thực (Security filter thường có order thấp hơn)
        return 10;
    }
}
