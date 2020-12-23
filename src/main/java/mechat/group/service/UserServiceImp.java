package mechat.group.service;

import mechat.group.entity.Posts;
import mechat.group.entity.User;
import mechat.group.model.Result;
import mechat.group.repository.PostRepo;
import mechat.group.repository.RoleRepository;
import mechat.group.repository.UserRepo;
import mechat.group.security.JwtTokenProvider;
import org.hashids.Hashids;
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

    private final Hashids hashids;


    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public UserServiceImp() {
        this.hashids = new Hashids(getClass().getName(), 12);
    }

//    public User getUser(Long id) {
//        try {
//            return userRepo.findById(id).get();
//        } catch (Exception e) {
//            System.out.println(e);
//            return null;
//        }
//    }

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
            User user1 = userRepo.save(user);
            user1.setHashId(hashids.encode(user1.getId()));
            return userRepo.save(user1);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public User update(String hashId, User user) {
        try {
            User user1 = userRepo.findByHashId(hashId).get();
            user1.setFullname(user.getFullname());
            user1.setUsername(user.getUsername());
            user1.setHashId(hashId);
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

    public Result delete(String hashId) {
        try {
            List<Posts> posts = postRepository.findAllByUser(userRepo.findByHashId(hashId).get());
            for (Posts p : posts) {
                p.setUser(null);
            }
            userRepo.deleteById(userRepo.findByHashId(hashId).get().getId());
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