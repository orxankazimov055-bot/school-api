package az.iktex.organization.structure.testders.entity.converter;

import az.iktex.organization.structure.testders.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RoleConverter
        implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role role) {
        return role == null ? null : role.getDbCode();
    }

    @Override
    public Role convertToEntityAttribute(Integer dbCode) {
        return dbCode == null ? null : Role.findByDbCode(dbCode);
    }
}