package mechat.group.service;

import mechat.group.entity.FileDB;
//import mechat.group.entity.FileStorage;
import mechat.group.entity.Posts;
import mechat.group.entity.User;
import mechat.group.model.Result;
import mechat.group.repository.PostRepo;
import mechat.group.repository.UserRepo;
import mechat.group.vm.PostPayload;
//import mechat.group.vm.PostRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Stream;


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

    public List<Posts> getAll() {
        try {
            return postRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }


    public Stream<Posts> getAllPosts() {
        return postRepository.findAll().stream();
    }

    public Posts getOne(String id) {
        return postRepository.findById(id).get();
    }

    public Posts createPosts(PostPayload post, User user, MultipartFile multipartFile) {
        try {
            Posts posts = new Posts();
            posts.setMessage(post.getMessage());
            if (post.getReplyPost() != null) {
                posts.setReplyPost(postRepository.findById(post.getReplyPost()).get());
            }
            FileDB fileStorage = null;
            if (multipartFile != null) {
                fileStorage = fileStorageService.store(multipartFile);
                if (fileStorage != null) {
                    posts.setFileDB(fileStorage);
                }
            }
            posts.setUser(user);
            Posts posts1 = postRepository.save(posts);
            return postRepository.save(posts1);
        } catch (Exception e) {
            return null;
        }
    }

    public Posts updatePosts(Posts post, User user) {
        try {
            Posts posts = postRepository.findById(post.getId()).get();
            post.setCreateAt(posts.getCreateAt());
            post.setReplyPost(posts.getReplyPost());
            post.setFileDB(posts.getFileDB());
            post.setUser(user);
            post.setId(posts.getId());
            return postRepository.save(post);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Result delete(String id) {
        try {
            Posts posts = postRepository.findById(id).get();
            if (posts.getFileDB() != null) {
                fileStorageService.delete(posts.getFileDB().getId());
            }
            List<Posts> postsList = postRepository.findAllByReplyPost(postRepository.findById(id).get());
            if (posts != null) {
                for (int i = 0; i < postsList.size(); i++) {
                    postsList.get(i).setReplyPost(null);
                }
            }
            if (postRepository.findById(id).get().getId() != null) {
                postRepository.deleteById(postRepository.findById(id).get().getId());
                return new Result(true, "post deleted");
            }
            return new Result(false, "post not deleting");
        } catch (Exception e) {
            return new Result(false, "post not deleting: " + e.getMessage());
        }
    }
}