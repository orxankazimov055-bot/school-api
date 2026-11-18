package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.response.AccountResponse;
import az.iktex.organization.structure.testders.entity.Account;

import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.enums.Role;
import az.iktex.organization.structure.testders.enums.RevokeReason;

import az.iktex.organization.structure.testders.exception.ConflictException;
import az.iktex.organization.structure.testders.exception.NotFoundException;

import az.iktex.organization.structure.testders.repository.AccountRepository;
import az.iktex.organization.structure.testders.security.AuthPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TokenBlacklistService blacklistService;

    // ================= GET ALL =================

    @Transactional(readOnly = true)
    public List<AccountResponse> getAll(Role role) {

        return accountRepository.findAll()
                .stream()
                .filter(account ->
                        role == null || account.getRole() == role
                )
                .map(this::toResponse)
                .toList();
    }

    // ================= CHANGE ROLE =================

    @Transactional
    public AccountResponse changeRole(
            Long accountId,
            Role role,
            AuthPrincipal principal
    ) {

        checkSelfModification(accountId, principal);

        Account account = findAccountForUpdate(accountId);

        account.setRole(role);

        accountRepository.save(account);

        blacklistService.revokeAll(
                accountId,
                RevokeReason.ADMIN_REVOKE
        );

        log.info(
                "Account role changed. AccountId: {}, Role: {}",
                accountId,
                role
        );

        return toResponse(account);
    }

    // ================= DISABLE =================

    @Transactional
    public AccountResponse disable(
            Long accountId,
            AuthPrincipal principal
    ) {

        checkSelfModification(accountId, principal);

        Account account = findAccountForUpdate(accountId);

        account.setEnabled(false);

        accountRepository.save(account);

        blacklistService.revokeAll(
                accountId,
                RevokeReason.ACCOUNT_DISABLED
        );

        log.info(
                "Account disabled. AccountId: {}",
                accountId
        );

        return toResponse(account);
    }

    // ================= ENABLE =================

    @Transactional
    public AccountResponse enable(Long accountId) {

        Account account = findAccountForUpdate(accountId);

        account.setEnabled(true);

        accountRepository.save(account);

        log.info(
                "Account enabled. AccountId: {}",
                accountId
        );

        return toResponse(account);
    }

    // ================= REVOKE TOKENS =================

    @Transactional
    public void revokeTokens(Long accountId) {

        findAccountForUpdate(accountId);

        blacklistService.revokeAll(
                accountId,
                RevokeReason.ADMIN_REVOKE
        );

        log.info(
                "Account tokens revoked by admin. AccountId: {}",
                accountId
        );
    }

    // ================= SELF MODIFICATION =================

    private void checkSelfModification(
            Long accountId,
            AuthPrincipal principal
    ) {

        if (accountId.equals(principal.accountId())) {

            throw new ConflictException(
                    ErrorCode.ACCOUNT_SELF_MODIFICATION
            );
        }
    }

    // ================= FIND ACCOUNT WITH LOCK =================

    private Account findAccountForUpdate(Long accountId) {

        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.ACCOUNT_NOT_FOUND
                ));
    }

    // ================= RESPONSE =================

    private AccountResponse toResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getRole(),
                account.isEnabled(),
                account.getCreatedAt()
        );
    }
}