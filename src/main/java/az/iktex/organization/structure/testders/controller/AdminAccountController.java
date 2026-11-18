package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.dto.request.ChangeRoleRequest;
import az.iktex.organization.structure.testders.response.AccountResponse;

import az.iktex.organization.structure.testders.enums.Role;
import az.iktex.organization.structure.testders.security.AuthPrincipal;
import az.iktex.organization.structure.testders.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Admin Account API",
        description = "Hesablarin idare olunmasi"
)
public class AdminAccountController {

    private final AccountService accountService;

    // ================= GET ALL =================

    @GetMapping
    @Operation(summary = "Hesablarin siyahisi")
    public ResponseEntity<List<AccountResponse>> getAll(
            @RequestParam(required = false) Role role
    ) {

        return ResponseEntity.ok(
                accountService.getAll(role)
        );
    }

    // ================= CHANGE ROLE =================

    @PatchMapping("/{id}/role")
    @Operation(summary = "Hesabin rolunu deyis")
    public ResponseEntity<AccountResponse> changeRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRoleRequest request,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {

        return ResponseEntity.ok(
                accountService.changeRole(
                        id,
                        request.role(),
                        principal
                )
        );
    }

    // ================= DISABLE =================

    @PatchMapping("/{id}/disable")
    @Operation(summary = "Hesabi deaktiv et")
    public ResponseEntity<AccountResponse> disable(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {

        return ResponseEntity.ok(
                accountService.disable(id, principal)
        );
    }

    // ================= ENABLE =================

    @PatchMapping("/{id}/enable")
    @Operation(summary = "Hesabi aktiv et")
    public ResponseEntity<AccountResponse> enable(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                accountService.enable(id)
        );
    }

    // ================= REVOKE TOKENS =================

    @PostMapping("/{id}/revoke-tokens")
    @Operation(summary = "Hesabin butun tokenlerini legv et")
    public ResponseEntity<Void> revokeTokens(
            @PathVariable Long id
    ) {

        accountService.revokeTokens(id);

        return ResponseEntity.noContent().build();
    }
}