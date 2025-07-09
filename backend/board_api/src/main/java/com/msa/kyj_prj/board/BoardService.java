package com.msa.kyj_prj.board;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
    // ✅ 게시글 상태 삭제 처리 메서드
    @Transactional
    public void markBoardsAsDeleted() {
        boardDAO.markBoardsAsDeletedFromReservations();
    }

    // ✅ 2시간마다 실행되는 스케줄러
    @Scheduled(cron = "0 0 */2 * * *", zone = "Asia/Seoul")
    public void cleanUpBoardsWithCancelledOrExpiredReservations() {
        log.info("⏰ 예약 취소/만료된 게시글 상태 삭제 스케줄러 실행");

        try {
            markBoardsAsDeleted();
            log.info("✅ 게시글 상태 삭제 완료");
        } catch (Exception e) {
            log.error("❌ 게시글 상태 삭제 중 오류 발생: {}", e.getMessage());
        }
    }
}
