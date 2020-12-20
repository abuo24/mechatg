package mechat.group.service;

import mechat.group.entity.Posts;
import mechat.group.entity.User;
import mechat.group.model.Result;
import mechat.group.repository.PostRepo;
import mechat.group.repository.RoleRepository;
import mechat.group.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PostsServiceImp {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PostRepo postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Posts> getAll() {
        try {
            return postRepository.findAll();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Posts createPosts(Posts post, User user) {
        try {
            post.setUser(user);
            return postRepository.save(post);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Posts updatePosts(Posts post, User user) {
        try {
            post.setUser(user);
            return postRepository.save(post);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Result delete(Long id) {
        try {
            if (postRepository.findById(id).get().getId() != null) {
                postRepository.deleteById(id);
                return new Result(true, "post deleted");
            }
            return new Result(false, "post not deleting");
        } catch (Exception e) {
            return new Result(false, "post not deleting: \n");
        }
    }
}