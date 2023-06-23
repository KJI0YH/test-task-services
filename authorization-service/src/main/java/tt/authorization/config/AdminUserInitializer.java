package tt.authorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import tt.authorization.data.Role;
import tt.authorization.data.User;
import tt.authorization.service.PasswordService;
import tt.authorization.service.RoleService;
import tt.authorization.service.UserService;

@Component
public class AdminUserInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${config.admin.email}")
    private String adminEmail;

    @Value("${config.admin.password}")
    private String adminPassword;

    private final UserService userService;
    private final PasswordService passwordService;
    private final RoleService roleService;
    private final ConfigurableApplicationContext applicationContext;

    @Autowired
    public AdminUserInitializer(UserService userService, PasswordService passwordService, RoleService roleService, ConfigurableApplicationContext applicationContext) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.roleService = roleService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        // Check the existence of at least one admin
        Role adminRole = roleService.getRoleByName("ADMIN");
        if (adminRole != null && userService.getNumberOfUsersByRole(adminRole) == 0){
            try{

                // Creating admin user
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordService.encode(adminPassword));
                admin.setRole(adminRole);
                userService.createUser(admin);
            } catch (Exception e){

                // Error creating admin user
                SpringApplication.exit(applicationContext, (ExitCodeGenerator) () -> 1);
            }
        }
    }
}
