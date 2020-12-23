package mechat.group.service;

import mechat.group.entity.FileStorage;
import mechat.group.entity.Posts;
import mechat.group.entity.User;
import mechat.group.model.Result;
import mechat.group.repository.PostRepo;
import mechat.group.repository.UserRepo;
import mechat.group.vm.PostPayload;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class PostsServiceImp {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PostRepo postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    protected FileStorageService fileStorageService;

    private final Hashids hashids;

    public PostsServiceImp() {
        this.hashids = new Hashids(getClass().getName(), 12);
        ;
    }

    public List<Posts> getAll() {
        try {
            List<Posts> postsList = postRepository.findAll();
            return postsList;
        } catch (Exception e) {
            return null;
        }
    }

    public Posts createPosts(PostPayload post, User user, MultipartFile multipartFile) {
        try {
            Posts posts = new Posts();
            posts.setMessage(post.getMessage());
            if (post.getReplyPost() != null) {
                posts.setReplyPost(postRepository.findById(post.getReplyPost()).get());
            }
            FileStorage fileStorage = null;
            if (multipartFile != null) {
                fileStorage = fileStorageService.save(multipartFile);
                if (fileStorage != null) {
                    posts.setFileStorage(fileStorage);
                }
            }
            posts.setUser(user);
            Posts posts1 = postRepository.save(posts);
            posts1.setHashId(hashids.encode(posts1.getId()));
            return postRepository.save(posts1);
        } catch (Exception e) {
            return null;
        }
    }

    public Posts updatePosts(Posts post, User user) {
        try {
            Posts posts = postRepository.findByHashId(post.getHashId()).get();
            post.setCreateAt(posts.getCreateAt());
            post.setReplyPost(posts.getReplyPost());
            post.setFileStorage(posts.getFileStorage());
            post.setUser(user);
            post.setHashId(posts.getHashId());
            post.setId(posts.getId());
            return postRepository.save(post);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Result delete(String id) {
        try {
            Posts posts = postRepository.findByHashId(id).get();
            if (posts.getFileStorage() != null) {
                fileStorageService.delete(posts.getFileStorage().getHashId());
            }
            List<Posts> postsList = postRepository.findAllByReplyPost(postRepository.findByHashId(id).get());
            if (posts != null) {
                for (int i = 0; i < postsList.size(); i++) {
                    postsList.get(i).setReplyPost(null);
                }
            }
            if (postRepository.findByHashId(id).get().getId() != null) {
                postRepository.deleteById(postRepository.findByHashId(id).get().getId());
                return new Result(true, "post deleted");
            }
            return new Result(false, "post not deleting");
        } catch (Exception e) {
            return new Result(false, "post not deleting: " + e.getMessage());
        }
    }
}