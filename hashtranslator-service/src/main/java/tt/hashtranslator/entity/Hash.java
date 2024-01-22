package tt.hashtranslator.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Hash {
    private String hash;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String result;
    private HashStatus status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    public Hash(String hash) {
        this.hash = hash;
    }
}
