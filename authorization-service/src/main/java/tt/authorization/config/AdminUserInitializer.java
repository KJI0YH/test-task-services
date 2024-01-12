package tt.authorization.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import tt.authorization.dto.UserDto;
import tt.authorization.entity.Role;
import tt.authorization.exception.AdminCreatingException;
import tt.authorization.service.UserService;

@Component
@Slf4j
public class AdminUserInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserService userService;
    @Value("${config.admin.email}")
    private String adminEmail;
    @Value("${config.admin.password}")
    private String adminPassword;

    @Autowired
    public AdminUserInitializer(UserService userService, ConfigurableApplicationContext applicationContext) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            // Check the existence of at least one admin
            if (userService.getNumberOfUsersByRole(Role.ADMIN) == 0) {
                // Creating admin user
                log.info("No admins found. Creating a new admin");
                userService.saveUser(new UserDto(adminEmail, adminPassword, Role.ADMIN.getName()));
            }
        } catch (Exception e) {
            // Error creating admin user
            log.error("Error when creating admin user: " + e.getMessage());
            throw new AdminCreatingException("Can not create an admin");
        }
    }
}
