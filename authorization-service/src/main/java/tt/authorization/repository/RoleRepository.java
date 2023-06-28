package tt.authorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tt.authorization.data.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findRoleByNameContainingIgnoreCase(String name);
}
