package com.msa.kyj_prj.reply;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplyService {

    @Autowired
    private ReplyDAO replyDAO;

    // 1. 게시글에 달린 댓글 목록 조회
    public List<Reply> findRepliesByBno(Long board_id) {
        return replyDAO.findRepliesByBno(board_id);
    }

    // 2. 댓글 등록
    public void insertReply(Reply reply) {
        replyDAO.insertReply(reply);
    }

    // 3. 댓글 수정
    public void updateReply(Reply reply) {
        replyDAO.updateReply(reply);
    }

    // 4. 댓글 삭제
    public void deleteReply(Long reply_id) {
        replyDAO.deleteReply(reply_id);
    }
}
