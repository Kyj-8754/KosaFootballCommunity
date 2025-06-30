package com.msa.kyj_prj.reply;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReplyDAO {
    // 1. 게시글에 달린 댓글 목록 조회
    List<Reply> findRepliesByBno(Long board_id);

    // 2. 댓글 등록
    void insertReply(Reply reply);

    // 3. 댓글 수정
    void updateReply(Reply reply);

    // 4. 댓글 삭제
    void deleteReply(Long reply_id);

}
