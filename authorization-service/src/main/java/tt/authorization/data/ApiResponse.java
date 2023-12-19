package tt.authorization.data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ApiResponse {

    @JsonIgnore
    private Boolean result;

    @JsonIgnore
    private String message;

    public ApiResponse(Boolean result) {
        this.result = result;
    }

    public ApiResponse(Boolean result, String message) {
        this(result);
        this.message = message;
    }
    
    @JsonAnyGetter
    public Map<String, Object> any(){
        Map<String, Object> properties = new HashMap<>();
        properties.put("result", result);
        if (message != null && !message.isEmpty()) {
            properties.put("message", message);
        }
        return properties;
    }
}
