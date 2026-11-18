package az.iktex.organization.structure.testders.validation;

public final class ValidationMessage {

    private ValidationMessage() {
    }

    public static final class Keys {

        private Keys() {
        }

        public static final String USERNAME_PATTERN =
                "{validation.username.pattern}";

        public static final String PASSWORD_STRONG =
                "{validation.password.strong}";
    }
}