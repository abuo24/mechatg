package mechat.group.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mechat.group.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ConfirmationToken {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String code;
    @OneToOne(fetch = FetchType.EAGER)
    private User user;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
}
