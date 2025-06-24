package com.msa.kyj_prj.file;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileEntity {
    private Long file_id;
    private Long board_id;
    private String file_original_name;
    private String file_saved_name;
    private String file_path;
    private Long file_size;
    private String file_type;
    private String file_status;
    private LocalDateTime file_created_at;
}
