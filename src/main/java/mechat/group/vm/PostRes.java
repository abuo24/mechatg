package mechat.group.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRes {

    private String message;
    private ResponseFile file;
    private String replyPostId;
    private String createAt;
    private String updateAt;
}
