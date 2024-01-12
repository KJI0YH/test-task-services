package tt.authorization.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tt.authorization.dto.UserDto;
import tt.authorization.entity.Role;
import tt.authorization.entity.User;
import tt.authorization.exception.AuthServiceException;
import tt.authorization.exception.MapperException;
import tt.authorization.exception.PermissionException;
import tt.authorization.exception.UserServiceException;
import tt.authorization.service.AuthService;
import tt.authorization.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") String authString) throws AuthServiceException, PermissionException {
        User user = authService.authentication(authString);
        authService.authorization(user, Role.USER);
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/admin/users")
    public ResponseEntity<User> createUser(@RequestHeader("Authorization") String authString,
                                           @Valid @RequestBody UserDto userDto) throws AuthServiceException, UserServiceException, PermissionException, MapperException {
        User user = authService.authentication(authString);
        authService.authorization(user, Role.ADMIN);
        User savedUser = userService.saveUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String authString,
                                           @PathVariable(value = "userId") Integer userId) throws AuthServiceException, UserServiceException, PermissionException {
        User user = authService.authentication(authString);
        authService.authorization(user, Role.ADMIN);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
