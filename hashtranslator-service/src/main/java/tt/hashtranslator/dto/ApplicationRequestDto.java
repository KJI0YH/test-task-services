package tt.hashtranslator.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApplicationRequestDto {
    private List<String> hashes;
}
