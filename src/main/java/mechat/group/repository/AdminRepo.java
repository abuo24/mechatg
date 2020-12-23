package mechat.group.repository;

import mechat.group.entity.Admins;
import mechat.group.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<Admins, Long> {
    Admins findByUsername(String username);
    Optional<Admins> findById(Long id);
    Optional<Admins> findByHashId(String hashId);
//    Boolean existsByUsername(String username);
}
