package com.msa.kyj_prj.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/file")
@Tag(name = "파일 API", description = "게시글에 첨부된 파일을 다루는 API")
public class FileController {

    @Autowired
    private FileService fileService;

    private final String uploadDir = "/home/ubuntu/images";

    // 파일 업로드
    @PostMapping("/upload")
    @Operation(summary = "파일 업로드", description = "파일을 업로드합니다.")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
    		@Parameter(description = "게시글 id", example = "234") @RequestParam("board_id") Long boardId) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "파일이 비어있습니다."));
            }

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

            return ResponseEntity.ok(Map.of("result", "success", "file_id", fileEntity.getFile_id()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "파일 업로드 실패", "message", e.getMessage()));
        }
    }

    // 게시글 첨부파일 목록 조회
    @Operation(summary = "파일 목록 조회", description = "해당 게시글의 첨부파일 목록을 조회합니다.")
    @GetMapping("/list/{board_id}")
    public ResponseEntity<?> getFiles(@Parameter(description = "게시글 id", example = "234") @PathVariable Long board_id) {
        try {
            List<FileEntity> files = fileService.getFilesByBoardId(board_id);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "파일 목록 조회 실패", "message", e.getMessage()));
        }
    }

    // 파일 다운로드
    @Operation(summary = "파일 다운로드", description = "해당 파일을 다운로드합니다.")
    @GetMapping("/download/{file_id}")
    public ResponseEntity<?> downloadFile(@Parameter(description = "파일 id", example = "234") @PathVariable Long file_id) {
        try {
            FileEntity file = fileService.findFileById(file_id);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "파일 정보를 찾을 수 없습니다."));
            }

            String fullPath = file.getFile_path() + file.getFile_saved_name();
            FileSystemResource resource = new FileSystemResource(fullPath);

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "파일이 존재하지 않습니다."));
            }

            String encodedName = UriUtils.encode(file.getFile_original_name(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.getFile_type())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "파일 다운로드 실패", "message", e.getMessage()));
        }
    }

    // 파일 삭제
    @Operation(summary = "파일 삭제", description = "해당 파일을 삭제합니다.")
    @DeleteMapping("/delete/{file_id}")
    public ResponseEntity<?> deleteFile(@Parameter(description = "파일 id", example = "234") @PathVariable Long file_id) {
        try {
            FileEntity file = fileService.findFileById(file_id);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "파일이 존재하지 않습니다."));
            }

            File target = new File(file.getFile_path() + file.getFile_saved_name());
            if (target.exists()) {
                target.delete();
            }

            fileService.deleteFilesByBoardId(file_id);
            return ResponseEntity.ok(Map.of("result", "deleted", "file_id", file_id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "파일 삭제 실패", "message", e.getMessage()));
        }
    }
}
