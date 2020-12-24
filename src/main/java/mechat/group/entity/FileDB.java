package mechat.group.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class FileDB {
    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid",strategy = GenerationType.IDENTITY)
    private String id;

    private String name;

    private String type;

    private Long fileSize;

    @Lob
    private byte[] data;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createAt;

    public FileDB(String name, String type, byte[] data, Long fileSize) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.fileSize = fileSize;
    }
}
