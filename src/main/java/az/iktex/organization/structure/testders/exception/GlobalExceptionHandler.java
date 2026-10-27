
package az.iktex.organization.structure.testders.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // C3 - MANDATORY
    // Transaction olmadiginda 409 Conflict qaytarir
    @ExceptionHandler(IllegalTransactionStateException.class)
    public ResponseEntity<Map<String, String>> handleTransactionException(
            IllegalTransactionStateException exception) {

        Map<String, String> response = Map.of(
                "error", "CONFLICT",
                "message", exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
}
