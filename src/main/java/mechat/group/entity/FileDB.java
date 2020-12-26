package mechat.group.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class FileDB {
    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column()
    private String name;
    @Column
    private String type;
    @Column
    private long fileSize;

    @Lob
    private byte[] data;
    @Column(nullable = false, updatable = false)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date createAt;

    public FileDB(String name, String type, byte[] data, Long fileSize) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.fileSize = fileSize;
    }
}
