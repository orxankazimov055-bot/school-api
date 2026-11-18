package az.iktex.organization.structure.testders.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN(1),
    MANAGER(2),
    USER(3);

    private final Integer dbCode;

    public static Role findByDbCode(Integer dbCode) {
        for (Role role : values()) {
            if (role.getDbCode().equals(dbCode)) {
                return role;
            }
        }

        throw new IllegalArgumentException(
                "Unknown role dbCode: " + dbCode
        );
    }
}