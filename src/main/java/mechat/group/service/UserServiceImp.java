package mechat.group.service;

import mechat.group.entity.Posts;
import mechat.group.entity.User;
import mechat.group.model.Result;
import mechat.group.repository.PostRepo;
import mechat.group.repository.RoleRepository;
import mechat.group.repository.UserRepo;
import mechat.group.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Service
public class UserServiceImp {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PostRepo postRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public User getUser(Long id) {
        try {
            return userRepo.findById(id).get();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public List<User> getAll() {
        try {
            return userRepo.findAll();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public User create(User user) {
        try {
            user.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );
            user.setRoles(roleRepository.findAllByName("ROLE_USER"));
            return userRepo.save(user);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public User update(Long id, User user) {
        try {
            User user1 = userRepo.findById(id).get();
            user1.setFullname(user.getFullname());
            user1.setUsername(user.getUsername());
            user1.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );
            return userRepo.save(user1);
        } catch (Exception e) {
            return null;
        }
    }

    public Result delete(Long id) {
        try {
            List<Posts> posts = postRepository.findAllByUser(userRepo.findById(id).get());
            for (Posts p : posts) {
                postRepository.delete(p);
            }
            userRepo.deleteById(id);
            return new Result(true, "user deleted");
        } catch (Exception e) {
            System.out.println(e);
            return new Result(false, "user not deleting");
        }
    }

    public User WhoAmI(HttpServletRequest req) {
        try {
            return userRepo.findByUsername(jwtTokenProvider.getUser(jwtTokenProvider.resolveToken(req)));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean checkUsername(String username) {
        return userRepo.existsByUsername(username);
    }

}