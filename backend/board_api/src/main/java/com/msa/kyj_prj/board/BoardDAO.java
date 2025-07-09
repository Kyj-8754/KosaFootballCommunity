package com.msa.kyj_prj.board;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BoardDAO {
    // 게시글 목록 조회 (검색 조건 포함)
    List<Board> findBoards(Map<String, String> params);
    // 게시글 상세 조회
    Board findBoardByBno(Long board_id);
    // 게시글 작성
    void insertBoard(Board board);
    // 게시글 수정
    void updateBoard(Board board);
    // 게시글 삭제
    void deleteBoard(Long board_id);
    // 게시글 상태 변경, 이후에 구현
    // void updateBoardStatus(@Param("bno") int bno, @Param("status") String status);
	// 게시글 조회수 증가
	void increaseViewCount(Long board_id);
	// 게시글 좋아요
	void insertBoardLike(@Param("board_id") Long board_id, @Param("user_no") int user_no);
	// 게시글 좋아요 증가
	void increaseLikeCount(@Param("board_id") Long board_id);
	// 게시글 좋아요 취소
	void deleteBoardLike(@Param("board_id") Long board_id, @Param("user_no") int user_no);
	// 게시글 좋아요 수 감소
	void decreaseLikeCount(@Param("board_id") Long board_id);
	// 게시글 좋아요 개수
	int countBoardLike(Long board_id);
	// 게시글 좋아요 여부 확인
	int hasUserLikedBoard(@Param("board_id") Long board_id, @Param("user_no") int user_no);
	// 예약 취소/만료된 게시글 상태 삭제 처리
	void markBoardsAsDeletedFromReservations();
	// 모집게시판 전용 게시글 목록 조회 (Map 사용하지 않음)
	List<Board> findRecruitBoards(
	    @Param("userNo") int userNo,
	    @Param("keyword") String keyword,
	    @Param("sortDirection") String sortDirection
	);
}
