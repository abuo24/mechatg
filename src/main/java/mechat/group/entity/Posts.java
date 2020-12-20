package mechat.group.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "posts")
@Data

public class Posts{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createAt;
    @UpdateTimestamp
    private Date updateAt;
}
