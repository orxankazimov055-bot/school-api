package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.RegisterRequest;
import az.iktex.organization.structure.testders.response.AccountResponse;

import az.iktex.organization.structure.testders.dto.request.LoginRequest;
import az.iktex.organization.structure.testders.dto.request.RefreshRequest;
import az.iktex.organization.structure.testders.dto.request.ChangePasswordRequest;
import az.iktex.organization.structure.testders.dto.response.AuthResponse;
import az.iktex.organization.structure.testders.dto.response.SessionResponse;

import az.iktex.organization.structure.testders.security.AuthPrincipal;
import az.iktex.organization.structure.testders.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "Authentication ve sessiya emeliyyatlari")
public class AuthController {

    private final AuthService authService;

    // ================= REGISTER =================

    @PostMapping("/register")
    @SecurityRequirements()
    @Operation(summary = "Yeni hesab yarat")
    public ResponseEntity<AccountResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    @SecurityRequirements()
    @Operation(summary = "Sisteme daxil ol")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    // ================= REFRESH =================

    @PostMapping("/refresh")
    @SecurityRequirements()
    @Operation(summary = "Tokenleri yenile")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request
    ) {

        return ResponseEntity.ok(
                authService.refresh(request)
        );
    }

    // ================= LOGOUT =================

    @PostMapping("/logout")
    @Operation(summary = "Cari sessiyadan cixis")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {

        authService.logout(principal);

        return ResponseEntity.noContent().build();
    }

    // ================= LOGOUT ALL =================

    @PostMapping("/logout-all")
    @Operation(summary = "Butun sessiyalardan cixis")
    public ResponseEntity<Void> logoutAll(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {

        authService.logoutAll(principal);

        return ResponseEntity.noContent().build();
    }

    // ================= ME =================

    @GetMapping("/me")
    @Operation(summary = "Cari hesab melumatlari")
    public ResponseEntity<AccountResponse> me(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {

        return ResponseEntity.ok(
                authService.me(principal)
        );
    }

    // ================= SESSIONS =================

    @GetMapping("/sessions")
    @Operation(summary = "Aktiv sessiyalar")
    public ResponseEntity<List<SessionResponse>> sessions(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {

        return ResponseEntity.ok(
                authService.sessions(principal)
        );
    }

    // ================= REVOKE SESSION =================

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "Sessiyani legv et")
    public ResponseEntity<Void> revokeSession(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String sessionId
    ) {

        authService.revokeSession(principal, sessionId);

        return ResponseEntity.noContent().build();
    }

    // ================= CHANGE PASSWORD =================

    @PutMapping("/password")
    @Operation(summary = "Sifreni deyis")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        authService.changePassword(principal, request);

        return ResponseEntity.noContent().build();
    }
}