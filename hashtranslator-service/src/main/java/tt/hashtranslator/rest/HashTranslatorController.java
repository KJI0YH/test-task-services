package tt.hashtranslator.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HashTranslatorController {
    
    @PostMapping("/applications")
    public ResponseEntity<Void> createApplication(){
        
    }
    
    @GetMapping("/applications/{id}") 
    ResponseEntity<Void> getApplication(@RequestParam Integer id){
        
    }
}
