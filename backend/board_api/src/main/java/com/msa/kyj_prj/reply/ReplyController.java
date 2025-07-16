package com.msa.kyj_prj.reply;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/reply")
@Tag(name = "댓글 API", description = "댓글을 다루는 API")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    // 1. 게시글에 달린 댓글 목록 조회
    @GetMapping("/list/{board_id}")
    @Operation(summary = "댓글 리스트 조회", description = "해당 게시글의 댓글 리스트를 조회합니다.")
    public ResponseEntity<?> getReplies(@Parameter(description = "게시글 id", example = "234") @PathVariable Long board_id) {
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
    @Operation(summary = "댓글 등록", description = "댓글을 등록합니다.")
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
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    public ResponseEntity<?> updateReply(@Parameter(description = "댓글 id", example = "234") @PathVariable Long reply_id, @RequestBody Reply reply) {
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
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<?> deleteReply(@Parameter(description = "댓글 id", example = "234") @PathVariable Long reply_id) {
        try {
            replyService.deleteReply(reply_id);
            return ResponseEntity.ok(Map.of("result", "deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "댓글 삭제 실패", "message", e.getMessage()));
        }
    }
}
