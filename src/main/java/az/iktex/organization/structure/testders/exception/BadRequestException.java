package az.iktex.organization.structure.testders.exception;

import az.iktex.organization.structure.testders.enums.ErrorCode;

public class BadRequestException extends BaseException {

    public BadRequestException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}