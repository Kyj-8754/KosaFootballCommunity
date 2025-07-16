package com.msa.kyj_prj.file;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "파일 DTO")
public class FileEntity {

    @Schema(description = "파일 ID (PK)", example = "301")
    private Long file_id;

    @Schema(description = "게시글 ID (FK)", example = "101")
    private Long board_id;

    @Schema(description = "업로드된 원본 파일명", example = "example_document.pdf")
    private String file_original_name;

    @Schema(description = "서버에 저장된 파일명", example = "84990423-2434-49c1-a735-c4bb0db4163c.pdf")
    private String file_saved_name;

    @Schema(description = "파일 저장 경로", example = "C:/upload/files/")
    private String file_path;

    @Schema(description = "파일 크기 (Byte)", example = "204800")
    private Long file_size;

    @Schema(description = "파일 MIME 타입", example = "application/pdf")
    private String file_type;

    @Schema(description = "파일 상태 (예: active, deleted)", example = "active")
    private String file_status;

    @Schema(description = "파일 등록일시", example = "2025-07-15T13:30:00")
    private LocalDateTime file_created_at;
}
