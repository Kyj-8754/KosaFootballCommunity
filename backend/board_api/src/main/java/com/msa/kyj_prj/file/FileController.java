package com.msa.kyj_prj.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    private final String uploadDir = "C:/upload/files/";

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file,
                                          @RequestParam("board_id") Long boardId) throws IOException {
    	File dir = new File(uploadDir);
    	if (!dir.exists()) dir.mkdirs();

        if (file.isEmpty()) return Map.of("error", "empty file");

        String originalName = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalName);
        String savedName = UUID.randomUUID() + "." + extension;

        File dest = new File(uploadDir + savedName);
        file.transferTo(dest);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setBoard_id(boardId);
        fileEntity.setFile_original_name(originalName);
        fileEntity.setFile_saved_name(savedName);
        fileEntity.setFile_path(uploadDir);
        fileEntity.setFile_size(file.getSize());
        fileEntity.setFile_type(file.getContentType());

        fileService.saveFile(fileEntity);

        return Map.of("result", "success", "file_id", fileEntity.getFile_id());
    }

    @GetMapping("/list/{board_id}")
    public List<FileEntity> getFiles(@PathVariable Long board_id) {
        return fileService.getFilesByBoardId(board_id);
    }
    
    @GetMapping("/download/{file_id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long file_id) throws IOException {
        FileEntity file = fileService.findFileById(file_id);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        // 실제 파일 경로
        String fullPath = file.getFile_path() + file.getFile_saved_name();
        FileSystemResource resource = new FileSystemResource(fullPath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String encodedName = UriUtils.encode(file.getFile_original_name(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, file.getFile_type())
                .body(resource);
    }
 
    @DeleteMapping("/delete/{file_id}")
    public Map<String, Object> deleteFile(@PathVariable Long file_id) {
        FileEntity file = fileService.findFileById(file_id);
        if (file == null) {
            return Map.of("error", "파일이 존재하지 않습니다.");
        }

        // 실제 파일 시스템에서 삭제
        File target = new File(file.getFile_path() + file.getFile_saved_name());
        if (target.exists()) {
            target.delete();
        }

        // DB에서 삭제
        fileService.deleteFilesByBoardId(file_id);

        return Map.of("result", "deleted", "file_id", file_id);
    }

}
