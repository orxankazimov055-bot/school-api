package az.iktex.organization.structure.testders.security;

import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.enums.Role;
import az.iktex.organization.structure.testders.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin.username}")
    private String username;

    @Value("${app.security.admin.email}")
    private String email;

    @Value("${app.security.admin.password}")
    private String password;

    @Override
    public void run(String... args) {

        if (accountRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        Account admin = Account.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .enabled(true)
                .passwordChangedAt(Instant.now())
                .build();

        accountRepository.save(admin);

        log.info("Initial ADMIN account created");
    }
}