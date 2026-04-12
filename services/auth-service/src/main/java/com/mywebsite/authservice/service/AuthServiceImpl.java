package com.mywebsite.authservice.service;

import com.mywebsite.authservice.exception.AccountNotFoundException;
import com.mywebsite.authservice.model.entity.Account;
import com.mywebsite.authservice.model.request.LoginRequest;
import com.mywebsite.authservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final BCryptPasswordEncoder passwordEncoder;
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final AccountRepository accountRepository;

    @Override
    public String login(LoginRequest req) {
        Account account = accountRepository.findByPhone(req.getPhone())
            .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!passwordEncoder.matches(req.getPassword(), account.getPassword())) {
            throw new AccountNotFoundException("Wrong password");
        }

        try (Keycloak keycloak = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm)
            .grantType(OAuth2Constants.PASSWORD)
            .clientId(clientId)
            .clientSecret(clientSecret)
            // Sử dụng tài khoản master/admin của Keycloak hoặc chính user đó nếu đã sync
            .username(req.getPhone())
            .password(req.getPassword())
            .build()) {

            return keycloak.tokenManager().getAccessToken().getToken();
        } catch (jakarta.ws.rs.NotAuthorizedException e) {
            try {
                String body = e.getResponse().readEntity(String.class);
                System.out.println("=== KEYCLOAK 401 DETAIL: " + body);
            } catch (Exception ignored) {}
            throw new AccountNotFoundException("Keycloak 401: check logs");
        } catch (Exception e) {
            System.out.println("=== KEYCLOAK ERROR CLASS: " + e.getClass().getName());
            System.out.println("=== KEYCLOAK ERROR MSG: " + e.getMessage());
            Throwable cause = e.getCause();
            while (cause != null) {
                System.out.println("=== CAUSED BY: " + cause.getClass().getName() + ": " + cause.getMessage());
                cause = cause.getCause();
            }
            throw new AccountNotFoundException("Keycloak failed");
        }
    }

    @Override
    public void reset(Long id) {
        String password = passwordEncoder.encode("123456");
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        account.setPassword(password);
        accountRepository.save(account);
    }
}
