package com.msa.kyj_prj.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileDAO {
    void insertFile(FileEntity file); // 파일 삽입
    List<FileEntity> findFilesByBoardId(Long board_id); // 파일 리스트 검색
    void deleteFilesByBoardId(Long board_id); // 게시글 삭제 시 같이 처리
    public FileEntity findFileById(Long file_id); // 파일 상세정보 호출
}
