package mechat.group.service;

import mechat.group.entity.FileStorage;
import mechat.group.entity.FileStorageStatus;
import mechat.group.model.Result;
import mechat.group.repository.FileStorageRepo;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class FileStorageService {
    private final FileStorageRepo fileStorageRepository;

    private final Hashids hashids;


    @Value("${upload.folder}")
    private String uploadFolder;

    public FileStorageService(FileStorageRepo fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
        this.hashids = new Hashids(getClass().getName(), 12);
    }

    public FileStorage save(MultipartFile multipartFile) {
        FileStorage file = new FileStorage();
        file.setName(multipartFile.getOriginalFilename());
        file.setExtension(getExt(multipartFile.getOriginalFilename()));
        file.setFileSize(multipartFile.getSize());
        file.setContentType(multipartFile.getContentType());
        file.setFileStorageStatus(FileStorageStatus.DRAFT);
        fileStorageRepository.save(file);

        Date now = new Date();
        File uploadFolder = new File(String.format("%s/upload_files/%d/%d/", this.uploadFolder, 1900 + now.getYear(), 1 + now.getMonth()));
        if (!uploadFolder.exists() && uploadFolder.mkdirs()) {
            System.out.println("Create folders");
        }
        file.setHashId(hashids.encode(file.getId()));
        file.setUploadPath(String.format("upload_files/%d/%d/%s.%s",
                1900 + now.getYear(),
                1 + now.getMonth(),
                file.getHashId(),
                file.getExtension()));
        fileStorageRepository.save(file);
        uploadFolder = uploadFolder.getAbsoluteFile();
        File file1 = new File(uploadFolder, String.format("%s.%s", file.getHashId(), file.getExtension()));

        try {
            multipartFile.transferTo(file1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getFileSize() > 0 ? file : null;
    }

    public FileStorage findByHashId(String hashId) {
        return fileStorageRepository.findByHashId(hashId);
    }

    public Result delete(String hashId) {
        try {
            FileStorage fileStorage = findByHashId(hashId);
            File file = new File(String.format("%s/%s", this.uploadFolder, fileStorage.getUploadPath()));
            if (file.delete()) {
                fileStorageRepository.delete(fileStorage);
            }

        } catch (Exception e) {
            return new Result(false,e.getMessage());
        }
        return new Result(true, "deleted");

    }

    @Scheduled(cron = " 0 0 0 * * * ")
    public void deleteAllDraft() {
        List<FileStorage> fileStorages = fileStorageRepository.findAllByFileStorageStatus(FileStorageStatus.DRAFT);

        for (int i = 0; i < fileStorages.size(); i++) {
            delete(fileStorages.get(i).getHashId());
        }
        fileStorages.forEach(fileStorage -> {
            delete(fileStorage.getHashId());
        });

    }

    private String getExt(String fileName) {
        String ext = null;
        if (fileName != null && !fileName.isEmpty()) {
            int dot = fileName.lastIndexOf(".");
            if (dot > 0 && dot <= fileName.length()) {
                ext = fileName.substring(dot + 1);
            }
        }
        return ext;
    }


}
