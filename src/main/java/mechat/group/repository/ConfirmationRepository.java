package mechat.group.repository;

import mechat.group.entity.User;
import mechat.group.security.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationRepository extends JpaRepository<ConfirmationToken, String> {
	ConfirmationToken findByUser(User user);
}
