package az.iktex.organization.structure.testders.enums;
import java.util.Arrays;
public enum Gender {
    MALE(1), FEMALE(2);
    private final Integer dbCode;
    Gender(Integer dbCode) { this.dbCode = dbCode; }
    public Integer getDbCode() { return dbCode; }
    public static Gender findByDbCode(Integer dbCode) {
        return Arrays.stream(values()).filter(g -> g.dbCode.equals(dbCode)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Naməlum gender kodu: " + dbCode));
    }
}
