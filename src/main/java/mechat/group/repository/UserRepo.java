package mechat.group.repository;

import mechat.group.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByHashId(String hashId);
    Boolean existsByUsername(String username);
    User findByPhoneNumber(String phoneNumber);

}
