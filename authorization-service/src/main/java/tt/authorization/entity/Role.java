package tt.authorization.entity;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ADMIN", "A", 100),
    USER("USER", "U", 1);

    private final String name;
    private final String code;
    private final Integer priority;

    Role(String name, String code, Integer priority) {
        this.name = name;
        this.code = code;
        this.priority = priority;
    }
}