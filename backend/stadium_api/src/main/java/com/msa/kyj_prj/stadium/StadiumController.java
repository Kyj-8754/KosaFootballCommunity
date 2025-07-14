package com.msa.kyj_prj.stadium;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.kyj_prj.dto.Stadium;
import com.msa.kyj_prj.page.PageResponseVO_board;
import com.msa.kyj_prj.util.StadiumDetailResponse;
import com.msa.kyj_prj.util.Util;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/stadium")
@RequiredArgsConstructor
@Tag(name = "stadium", description = "경기장 리스트, 상세, 자동업데이트 관련 API" )
public class StadiumController {

	private final StadiumService stadiumService;
	

    
	// 공공 데이터 api 호출
	@Operation(summary = "초기 경기장 데이터 파싱용", description = "경기장 데이터 베이스를 넣기 위한 API")
	@PostMapping("test")
	public void stadiumapi() throws Exception{
		log.info("데이터 테스트 시작 시간 = {} ", LocalDateTime.now());
			stadiumService.stadiumapi();
		log.info("데이터 테스트 종료 시간 = {}", LocalDateTime.now());
	}
	

	// 경기장 목록 진입
	@Operation(summary = "경기장 목록 조회", description = "검색 조건과 페이지 번호에 따라 게시판 목록을 조회하는 API")
	@ApiResponse(responseCode = "200", description = "성공적으로 조회됨",
    content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = PageResponseVO_board.class)))
	@GetMapping("list")
	public Map<String, Object> list(
			@Parameter(description = "페이지 번호", example = "1") @RequestParam(required = false) String pageNo, 
			@Parameter(description = "검색 타입", example = "지역") @RequestParam(required = false) String searchType, 
			@Parameter(description = "검색 값", example = "마루") @RequestParam(required = false) String searchValue) {
		Map<String, Object> result = new HashMap<>();
		
			result.put("pageResponse",stadiumService.list(
					searchType,
					searchValue,
					Util.parseInt(pageNo, 1)
					));
			return result;
	}

	// 구장 상세 디테일
	@Operation(summary = "경기장 상세 api", description = "경기장 서비스ID를 통해 구장의 상세정보를 보여주는 API")
	@ApiResponse(responseCode = "200", description = "성공적으로 조회됨",
    content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Stadium.class)))
	@GetMapping("detailView")
	public Map<String, Object> detailView(@Parameter(description = "구장 서비스ID", example = "1") @RequestParam String SVCID) {
		Map<String, Object> result = new HashMap<>();
		StadiumDetailResponse stadiumDB = stadiumService.getStadium(SVCID);
		if (stadiumDB == null) {
			return result;
		}
		result.put("stadiumDB", stadiumDB);
		return result;
	}

	// 구장 업데이트
	@Operation(summary = "경기장 업데이트", description = "스케쥴러를 통해 구장 공공데이터를 DB에 자동으로 업데이트 하는 API")
	@PostMapping("update")
	public void syncAll() throws Exception{
		stadiumService.syncAll();
	}
	
}
