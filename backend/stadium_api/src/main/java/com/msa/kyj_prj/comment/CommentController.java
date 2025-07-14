package com.msa.kyj_prj.comment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.kyj_prj.stadium.Stadium;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/comment")
@RequiredArgsConstructor
@Tag(name = "stadium", description = "댓글과 별점 관련 API" )
public class CommentController {

	private final CommentService commnetService;
	
	// 글 작성
	@PostMapping("regist")
	@Operation(summary = "댓글 작성", description = "특정 댓글을 작성하는 API")
	@ApiResponse(responseCode = "200", description = "댓글 작성 성공",
    content = @Content(
    		 mediaType = "application/json",
    	        examples = @ExampleObject(
    	            name = "성공 예시",
    	            value = "{ \"error\": false }")))
	public ResponseEntity<Object> regist(@RequestBody Comment comment) {
		Map<String, Object> result = new HashMap<String, Object>();
		commnetService.registForm(comment);
		
		result.put("error", false);
		
		return ResponseEntity.ok(result);
	}
		
	//댓글 수정
	@PostMapping("update")
	@Operation(summary = "댓글 수정", description = "특정 댓글을 수정하는 API")
	@ApiResponse(responseCode = "200", description = "댓글 수정 성공",
    content = @Content( mediaType = "application/json",
	        		examples = @ExampleObject(
    	            name = "성공 예시",
    	            value = "{ \"error\": false }")))
	public ResponseEntity<Object>  update(@RequestBody Comment comment){
		Map<String, Object> result = new HashMap<String, Object>();
		

		commnetService.update(comment);
		result.put("error", false);

		return ResponseEntity.ok(result);
	}
	
	// 댓글 삭제
	@PostMapping("delete")
	@Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제하는 API")
	@ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
    content = @Content( mediaType = "application/json",
    		 examples = {
    		            @ExampleObject(name = "성공", value = "{ \"error\": false, \"message\": \"삭제 완료 되었습니다.\" }"),
    		            @ExampleObject(name = "실패", value = "{ \"error\": true, \"message\": \"삭제에 문제가 생겼습니다.\" }")
    		        }))
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
	@Operation(summary = "댓글 리스트", description = "상세 경기장에 달린 댓글을 가져오는 API")
	@ApiResponse(responseCode = "200", description = "성공적으로 조회됨",
    content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Comment.class)))
	public Map<String, Object> list(String SVCID) {
		Map<String, Object>result = new HashMap<>();
		
		result.put("commentDB",commnetService.list(SVCID));
		return result;
	}
			
}
