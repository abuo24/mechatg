package mechat.group.repository;

import mechat.group.entity.User;
import mechat.group.security.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String> {
	ConfirmationToken findByUser(User user);
}
