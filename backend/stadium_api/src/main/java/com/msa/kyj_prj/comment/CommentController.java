package com.msa.kyj_prj.comment;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/comment")
public class CommentController {

	@Autowired
	CommentService commnetService;
	
		// 글 작성
		@PostMapping("regist")
		public ResponseEntity<Object> regist(@RequestBody Comment comment) {
			Map<String, Object> result = new HashMap<String, Object>();
			commnetService.registForm(comment);
			
			result.put("error", false);
			
			return ResponseEntity.ok(result);
		}
		
		//댓글 수정
		@PostMapping("update")
		public ResponseEntity<Object>  update(@RequestBody Comment comment){
			Map<String, Object> result = new HashMap<String, Object>();
			

			commnetService.update(comment);
			result.put("error", false);

			return ResponseEntity.ok(result);
		}
		
		// 댓글 삭제
		@PostMapping("delete")
		public ResponseEntity<Object> delete(@RequestBody Comment comment) {
			Map<String, Object> result = new HashMap<String, Object>();
			

		    boolean success = commnetService.delete(comment.getComment_no());

		    if (success) {
		        result.put("error", false);
		        result.put("message", "삭제 완료 되었습니다.");
		    } else {
		        result.put("error", true);
		        result.put("message", "삭제에 문제가 생겼습니다.");
		    }
			return ResponseEntity.ok(result);
		}
			
			
		// 댓글 리스트
		@GetMapping("list")
		public Map<String, Object> list(String SVCID) {
			Map<String, Object>result = new HashMap<>();
			
			result.put("commentDB",commnetService.list(SVCID));
			return result;
		}
			
}
