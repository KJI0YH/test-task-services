package tt.authorization.service.converter;

import tt.authorization.entity.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role attribute) {
        if (attribute == null)
            return null;
        return attribute.getCode();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return Stream.of(Role.values())
                .filter(role -> role.getCode().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
