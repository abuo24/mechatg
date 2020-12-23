package mechat.group.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mechat.group.entity.FileStorage;
import mechat.group.entity.Posts;
import mechat.group.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPayload {

    private String message;
    private MultipartFile file;
    private Long replyPost;
}
