package mechat.group.controller;

import io.jsonwebtoken.Jwt;
import mechat.group.entity.Posts;
import mechat.group.entity.User;
import mechat.group.model.Result;
import mechat.group.model.ResultSucces;
import mechat.group.repository.AdminRepo;
import mechat.group.repository.PostRepo;
import mechat.group.repository.UserRepo;
import mechat.group.security.JwtTokenProvider;
import mechat.group.service.PostsServiceImp;
import mechat.group.service.UserServiceImp;
import mechat.group.vm.PostPayload;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    private final UserServiceImp userServiceImp;
    private final UserRepo userRepository;
    private final PostsServiceImp postsServiceImp;
    private final PostRepo postRepository;
    private final AdminRepo adminRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public PostsController(UserServiceImp userServiceImp, UserRepo userRepository, PostsServiceImp postsServiceImp, PostRepo postRepository, AdminRepo adminRepository, JwtTokenProvider jwtTokenProvider) {
        this.userServiceImp = userServiceImp;
        this.userRepository = userRepository;
        this.postsServiceImp = postsServiceImp;
        this.postRepository = postRepository;
        this.adminRepository = adminRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/all")
    public ResponseEntity getAllPosts() {
        return ResponseEntity.ok(new ResultSucces(true, postsServiceImp.getAll()));
    }

    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity createPost(@ModelAttribute PostPayload post, HttpServletRequest request) {
        User user = userServiceImp.WhoAmI(request);
        Posts posts = postsServiceImp.createPosts(post, user, post.getFile());
        if(posts==null){
            return new ResponseEntity(new Result(false,"post not create"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ResultSucces(true, posts));
    }

    @PutMapping("/update")
    public ResponseEntity editPost(@RequestBody Posts post, HttpServletRequest request) {
        User user = userServiceImp.WhoAmI(request);
        if (user.getId().equals(postRepository.findById(post.getId()).get().getUser().getId())) {
            return ResponseEntity.ok(postsServiceImp.updatePosts(post, user));
        }
        return ResponseEntity.ok(new Result(false, "post not update"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deletePost(@PathVariable String id, HttpServletRequest request) {
        try {
            User user = userServiceImp.WhoAmI(request);
            if (user.getId().equals(postRepository.findById(id).get().getUser().getId()) || user.getRoles().size() == 2) {
                return ResponseEntity.ok(postsServiceImp.delete(id));
            }
            return ResponseEntity.ok(new Result(false, "post not deleted"));
        } catch (Exception e) {
            return new ResponseEntity(new Result(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
