package mechat.group.repository;

import mechat.group.entity.Posts;
import mechat.group.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepo extends JpaRepository<Posts, Long> {
    List<Posts> findAllByUser(User user);
    Optional<Posts> findById(Long id);
    Optional<Posts> findByHashId(String hashId);
    List<Posts> findAll();
    List<Posts> findAllByReplyPost(Posts posts);
}
