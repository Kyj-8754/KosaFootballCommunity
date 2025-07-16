package com.msa.kyj_prj.reply;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    // 1. 게시글에 달린 댓글 목록 조회
    @GetMapping("/list/{board_id}")
    public ResponseEntity<?> getReplies(@PathVariable Long board_id) {
        try {
            List<Reply> replies = replyService.findRepliesByBno(board_id);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "댓글 목록 조회 실패", "message", e.getMessage()));
        }
    }

    // 2. 댓글 등록
    @PostMapping
    public ResponseEntity<?> createReply(@RequestBody Reply reply) {
        try {
            replyService.insertReply(reply);
            return ResponseEntity.ok(Map.of("result", "created", "reply_id", reply.getReply_id()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "댓글 등록 실패", "message", e.getMessage()));
        }
    }

    // 3. 댓글 수정
    @PutMapping("/{reply_id}")
    public ResponseEntity<?> updateReply(@PathVariable Long reply_id, @RequestBody Reply reply) {
        try {
            reply.setReply_id(reply_id);
            replyService.updateReply(reply);
            return ResponseEntity.ok(Map.of("result", "updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "댓글 수정 실패", "message", e.getMessage()));
        }
    }

    // 4. 댓글 삭제 (논리 삭제)
    @DeleteMapping("/{reply_id}")
    public ResponseEntity<?> deleteReply(@PathVariable Long reply_id) {
        try {
            replyService.deleteReply(reply_id);
            return ResponseEntity.ok(Map.of("result", "deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "댓글 삭제 실패", "message", e.getMessage()));
        }
    }
}
