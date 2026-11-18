package az.iktex.organization.structure.testders.exception;

import az.iktex.organization.structure.testders.dto.response.ErrorResponse;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.config.LanguageLocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;
    private final MessageSource messageSource;
    private final LanguageLocaleResolver localeResolver;

    // ================= BASE EXCEPTION =================

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ex.getErrorCode(),
                request,
                ex.getArgs()
        );
    }

    // ================= VALIDATION ERROR =================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Locale locale = localeResolver.resolveLocale(request);

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {

                    String translated = messageSource.getMessage(
                            error,
                            locale
                    );

                    return error.getField() + ": " + translated;
                })
                .collect(Collectors.joining("; "));

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        ErrorResponse response = new ErrorResponse(
                errorCode.getCode(),
                message,
                errorCode.getStatus().value(),
                request.getRequestURI(),
                MDC.get("traceId"),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    // ================= MALFORMED REQUEST =================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ErrorCode.MALFORMED_REQUEST,
                request
        );
    }

    // ================= TYPE MISMATCH =================

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ErrorCode.TYPE_MISMATCH,
                request
        );
    }

    // ================= MISSING PARAMETER =================

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ErrorCode.MISSING_PARAMETER,
                request
        );
    }

    // ================= METHOD NOT SUPPORTED =================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ErrorCode.METHOD_NOT_SUPPORTED,
                request
        );
    }

    // ================= URL NOT FOUND =================

    @ExceptionHandler({
            NoHandlerFoundException.class,
            NoResourceFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleUrlNotFound(
            Exception ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ErrorCode.URL_NOT_FOUND,
                request
        );
    }

    // ================= DATA INTEGRITY =================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Database integrity violation. TraceId: {}, Error: {}",
                MDC.get("traceId"),
                ex.getClass().getSimpleName()
        );

        return buildResponse(
                ErrorCode.DATA_INTEGRITY_VIOLATION,
                request
        );
    }

    // ================= SPRING SECURITY ERRORS =================

    // ERR-2701 - AUTH_BAD_CREDENTIALS

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ErrorCode.AUTH_BAD_CREDENTIALS,
                request
        );
    }

    // ERR-2707 - AUTH_ACCESS_DENIED

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ErrorCode.AUTH_ACCESS_DENIED,
                request
        );
    }

    // ERR-2708 - AUTH_ACCOUNT_DISABLED

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledAccount(
            DisabledException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ErrorCode.AUTH_ACCOUNT_DISABLED,
                request
        );
    }

    // ERR-2709 - AUTH_ACCOUNT_LOCKED

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedAccount(
            LockedException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                ErrorCode.AUTH_ACCOUNT_LOCKED,
                request
        );
    }

    // ================= INTERNAL ERROR =================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalError(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error(
                "Unexpected server error. TraceId: {}, Error: {}",
                MDC.get("traceId"),
                ex.getClass().getSimpleName()
        );

        return buildResponse(
                ErrorCode.INTERNAL_ERROR,
                request
        );
    }

    // ================= BUILD ERROR RESPONSE =================

    private ResponseEntity<ErrorResponse> buildResponse(
            ErrorCode errorCode,
            HttpServletRequest request,
            Object... args
    ) {

        Locale locale = localeResolver.resolveLocale(request);

        String message = messageService.getMessage(
                errorCode,
                locale,
                args
        );

        ErrorResponse response = new ErrorResponse(
                errorCode.getCode(),
                message,
                errorCode.getStatus().value(),
                request.getRequestURI(),
                MDC.get("traceId"),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
}