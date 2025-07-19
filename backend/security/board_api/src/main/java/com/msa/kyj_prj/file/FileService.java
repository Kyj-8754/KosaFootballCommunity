package com.msa.kyj_prj.file;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    @Autowired
    private FileDAO fileDAO;

    public void saveFile(FileEntity file) {
    	fileDAO.insertFile(file);
    }

    public List<FileEntity> getFilesByBoardId(Long boardId) {
        return fileDAO.findFilesByBoardId(boardId);
    }

    public void deleteFilesByBoardId(Long file_id) {
    	fileDAO.deleteFilesByBoardId(file_id);
    }
    
    public FileEntity findFileById(Long fileId) {
        return fileDAO.findFileById(fileId);
    }
}
