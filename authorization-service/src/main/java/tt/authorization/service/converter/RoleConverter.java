package tt.authorization.service.converter;

import lombok.extern.slf4j.Slf4j;
import tt.authorization.entity.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
@Slf4j
public class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role attribute) {
        log.info("Convert role: " + attribute + " to database column");  
        if (attribute == null)
            return null;
        return attribute.getCode();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        log.info("Convert database column: " + dbData + " to role");
        if (dbData == null)
            return null;
        return Stream.of(Role.values())
                .filter(role -> role.getCode().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
