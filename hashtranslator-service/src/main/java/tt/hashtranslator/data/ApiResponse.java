package tt.hashtranslator.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiResponse {
    private Boolean result;
    private String message;

    public ApiResponse(Boolean result) {
        this.result = result;
    }

    public ApiResponse(@JsonProperty("result") Boolean result,
                       @JsonProperty("message") String message) {
        this(result);
        this.message = message;
    }
}