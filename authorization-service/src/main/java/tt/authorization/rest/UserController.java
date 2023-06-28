package tt.authorization.rest;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tt.authorization.data.Role;
import tt.authorization.data.User;
import tt.authorization.dto.UserDto;
import tt.authorization.service.AuthorizationService;
import tt.authorization.service.PasswordService;
import tt.authorization.service.RoleService;
import tt.authorization.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final AuthorizationService authService;
    private final PasswordService passwordService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, AuthorizationService authService, PasswordService passwordService, RoleService roleService) {
        this.userService = userService;
        this.authService = authService;
        this.passwordService = passwordService;
        this.roleService = roleService;
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/admin/users")
    public ResponseEntity<User> createUser(@RequestHeader("Authorization") String authorization,
                                           @RequestBody UserDto userDto){
        if (authService.authorize(authorization, "ADMIN")){
            try {
                Role role = roleService.getRoleByName(userDto.getRole());

                if (role == null){
                    throw new NotFoundException("Role " + userDto.getRole() + " not found");
                }

                User user = new User();
                user.setEmail(userDto.getEmail());
                user.setPassword(passwordService.encode(userDto.getPassword()));
                user.setRole(role);

                Logger.getLogger(UserController.class.getName()).info("User: " + user.getEmail() + user.getPassword() + user.getRoleName() + user.getRole());

                User createdUser = userService.createUser(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            } catch (Exception e) {
                Logger.getLogger(UserController.class.getName()).info(e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String authorization,
                                           @PathVariable(value = "userId", required = true) Integer userId){
        if (authService.authorize(authorization, "ADMIN")){
            try {
                userService.deleteUser(userId);
                return ResponseEntity.noContent().build();
            } catch (Exception e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
