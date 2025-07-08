package com.msa.kyj_prj.stadium;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msa.kyj_prj.util.StadiumDetailResponse;
import com.msa.kyj_prj.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/stadium")
@RequiredArgsConstructor
public class StadiumController {

	private final StadiumService stadiumService;
	

    
	// 공공 데이터 api 호출
	@PostMapping("test")
	public void stadiumapi() throws Exception{
		log.info("데이터 테스트 시작 시간 = {} ", LocalDateTime.now());
			stadiumService.stadiumapi();
		log.info("데이터 테스트 종료 시간 = {}", LocalDateTime.now());
	}
	

	// 게시판 목록 진입
	@GetMapping("list")
	public Map<String, Object> list(String pageNo, String searchType, String searchValue) {
		Map<String, Object> result = new HashMap<>();
		
			result.put("pageResponse",stadiumService.list(
					searchType,
					searchValue,
					Util.parseInt(pageNo, 1)
					));
			return result;
	}

	// 구장 상세 디테일
	@GetMapping("detailView")
	public Map<String, Object> detailView(@RequestParam String SVCID) {
		Map<String, Object> result = new HashMap<>();
		StadiumDetailResponse stadiumDB = stadiumService.getStadium(SVCID);
		if (stadiumDB == null) {
			return result;
		}
		result.put("stadiumDB", stadiumDB);
		return result;
	}

	// 구장 업데이트
	@PostMapping("update")
	public void syncAll() throws Exception{
		stadiumService.syncAll();
	}
	
}
