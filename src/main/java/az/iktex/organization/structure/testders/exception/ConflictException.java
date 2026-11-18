package az.iktex.organization.structure.testders.exception;

import az.iktex.organization.structure.testders.enums.ErrorCode;

public class ConflictException extends BaseException {

    public ConflictException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}