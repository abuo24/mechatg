package mechat.group.repository;

import mechat.group.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    User findByUsername(String username);
    Optional<User> findById(String id);
    Boolean existsByUsername(String username);
    User findByPhoneNumber(String phoneNumber);

}
