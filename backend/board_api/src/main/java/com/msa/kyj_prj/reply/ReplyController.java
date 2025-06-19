package com.msa.kyj_prj.reply;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reply")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    // 1. 게시글에 달린 댓글 목록 조회
    @GetMapping("/list/{board_id}")
    public List<Reply> getReplies(@PathVariable Long board_id) {
        return replyService.findRepliesByBno(board_id);
    }

    // 2. 댓글 등록
    @PostMapping
    public Map<String, Object> createReply(@RequestBody Reply reply) {
        replyService.insertReply(reply);
        return Map.of("result", "created", "reply_id", reply.getReply_id());
    }

    // 3. 댓글 수정
    @PutMapping("/{reply_id}")
    public Map<String, Object> updateReply(@PathVariable Long reply_id, @RequestBody Reply reply) {
        reply.setReply_id(reply_id);
        replyService.updateReply(reply);
        return Map.of("result", "updated");
    }

    // 4. 댓글 삭제 (논리 삭제)
    @DeleteMapping("/{reply_id}")
    public Map<String, Object> deleteReply(@PathVariable Long reply_id) {
        replyService.deleteReply(reply_id);
        return Map.of("result", "deleted");
    }
}
