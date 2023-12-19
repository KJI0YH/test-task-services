package tt.authorization.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tt.authorization.data.Role;
import tt.authorization.data.User;
import tt.authorization.dto.UserDto;
import tt.authorization.exception.AuthServiceException;
import tt.authorization.exception.NotEnoughPermissions;
import tt.authorization.exception.UserServiceException;
import tt.authorization.service.AuthService;
import tt.authorization.service.RoleService;
import tt.authorization.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, AuthService authService, RoleService roleService) {
        this.userService = userService;
        this.authService = authService;
        this.roleService = roleService;
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/admin/users")
    public ResponseEntity<User> createUser(@RequestHeader("Authorization") String authorization,
                                           @RequestBody UserDto userDto) throws AuthServiceException, UserServiceException, NotEnoughPermissions {
        User user = authService.authentication(authorization);
        if (!user.isAdmin())
            throw new NotEnoughPermissions("Only admin can create users");

        Role role = roleService.getRoleByName(userDto.getRole());
        if (role == null)
            throw new UserServiceException("Role " + userDto.getRole() + " does not exists");

        User savedUser = userService.saveUser(userService.createUser(userDto.getEmail(), userDto.getPassword(), role));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String authorization,
                                           @PathVariable(value = "userId") Integer userId) throws AuthServiceException, UserServiceException, NotEnoughPermissions {
        User user = authService.authentication(authorization);
        if (!user.isAdmin())
            throw new NotEnoughPermissions("Only admin can delete users");
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
