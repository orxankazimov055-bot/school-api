package az.iktex.organization.structure.testders.dto.response;

public record AuthResponse(

        String tokenType,

        String accessToken,

        long accessExpiresIn,

        String refreshToken,

        long refreshExpiresIn

) {

    @Override
    public String toString() {
        return "AuthResponse[tokenType=" + tokenType
                + ", accessToken=***"
                + ", accessExpiresIn=" + accessExpiresIn
                + ", refreshToken=***"
                + ", refreshExpiresIn=" + refreshExpiresIn
                + "]";
    }
}