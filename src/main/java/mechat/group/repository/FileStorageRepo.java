package mechat.group.repository;

import mechat.group.entity.FileStorage;
import mechat.group.entity.FileStorageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileStorageRepo extends JpaRepository<FileStorage, Long> {
    FileStorage findByHashId(String hashId);

    List<FileStorage> findAllByFileStorageStatus(FileStorageStatus fileStorageStatus);
}
