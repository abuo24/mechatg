package mechat.group.service;

import java.io.IOException;
import java.util.stream.Stream;

import mechat.group.entity.FileDB;
import mechat.group.model.Result;
import mechat.group.repository.FileDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  @Autowired
  private FileDBRepository fileDBRepository;

  public FileDB store(MultipartFile file) throws IOException {
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    FileDB FileDB = new FileDB(fileName, file.getContentType(), file.getBytes(), file.getSize());

    return fileDBRepository.save(FileDB);
  }

  public FileDB getFile(String id) {
    return fileDBRepository.findById(id).get();
  }
  
  public Stream<FileDB> getAllFiles() {
    return fileDBRepository.findAll().stream();
  }

  public Result delete(String id) {
        try {
            FileDB fileStorage = fileDBRepository.findById(id).get();
            if (fileStorage!=null) {
                fileDBRepository.delete(fileStorage);
            }
        } catch (Exception e) {
            return new Result(false,e.getMessage());
        }
        return new Result(true, "deleted");
    }

}
