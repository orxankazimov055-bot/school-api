
package az.iktex.organization.structure.testders.exception;

import az.iktex.organization.structure.testders.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String getMessage(ErrorCode errorCode, Locale locale, Object... args) {

        String message = messageSource.getMessage(
                errorCode.getMessageKey(),
                args,
                null,
                locale
        );

        if (message == null) {
            message = messageSource.getMessage(
                    errorCode.getMessageKey(),
                    args,
                    errorCode.getMessageKey(),
                    Locale.ENGLISH
            );
        }

        return message;
    }
}
