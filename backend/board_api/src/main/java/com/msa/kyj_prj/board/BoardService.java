package com.msa.kyj_prj.board;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {
	@Autowired
    private BoardDAO boardDAO;
	
	public List<Board> findBoards(Map<String, String> params) {
        return boardDAO.findBoards(params);
    }

    public Board findBoardByBno(Long board_id) {
        return boardDAO.findBoardByBno(board_id);
    }

    public void insertBoard(Board board) {
        boardDAO.insertBoard(board);
    }

    public void updateBoard(Board board) {
        boardDAO.updateBoard(board);
    }

    public void deleteBoard(Long board_id) {
        boardDAO.deleteBoard(board_id);
    }
    
	public void increaseViewCount(Long board_id) {
		boardDAO.increaseViewCount(board_id);
	}
	
	@Transactional
	public void insertBoardLike(Long board_id, int user_no) {
	    boardDAO.insertBoardLike(board_id, user_no);
	    boardDAO.increaseLikeCount(board_id);
	}
	
	@Transactional
	public void deleteBoardLike(Long board_id, int user_no) {
	    boardDAO.deleteBoardLike(board_id, user_no);
	    boardDAO.decreaseLikeCount(board_id);
	}
	
	public int countBoardLike(Long board_id) {
		return boardDAO.countBoardLike(board_id);
	}

	public boolean hasUserLikedBoard(Long board_id, int user_no) {
	    return boardDAO.hasUserLikedBoard(board_id, user_no) == 1;
	}
}
