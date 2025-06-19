package com.msa.kyj_prj.stadium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.kyj_prj.util.Util;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/stadium")
@RequiredArgsConstructor
public class StadiumController {

	private final StadiumService stadiumService;
	private final StadiumDAO stadiumDAO;
	
	private static final int TOTAL_COUNT = 2312;
    private static final int STEP = 500;
    private static final int DELAY_MS = 60000;
    
	// 공공 데이터 api 호출
	@PostMapping("test")
	public void stadiumapi() throws Exception {
		List<Map<String, Object>> result = new ArrayList<>();
//		// 공공 데이터 총 갯수
//		String totalCount = "2312";
//		// 500개씩 호출하도록 하기위해 설정
//		String step = "500";
//		// 1분 딜레이
//		int delayMs = 60000; 
		int totalSteps = (int) Math.ceil((double) TOTAL_COUNT / STEP);
		
		
		for(int i = 0; i < totalSteps; i++) {
			
			int start = i * STEP + 1;
            int end = Math.min(start + STEP - 1, TOTAL_COUNT);
            
            
            System.out.printf("[%d/%d] 요청: %d ~ %d\n", i + 1, totalSteps, start, end);
            List<Map<String, Object>> futsalList = fetchData(start, end);
            result.addAll(futsalList);
            
            if (i + 1 < totalSteps) {
                Thread.sleep(DELAY_MS); // 다음 요청까지 1분 대기
            }
		}
		
		
		  System.out.println("모든 요청 완료. 풋살장 데이터 총 갯수: " + result.size());
		  
		  for(int i = 0;  i < result.size(); i ++) {
			  String svcId = (String) result.get(i).get("SVCID");
			  System.out.printf("[%d]번째 요청: \n", i);
			  List<Map<String, Object>> details = fetchDetailData(svcId);
			  if (!details.isEmpty()) {
				  Map<String, Object> detail = details.get(0);
				  
				  int result1 = stadiumDAO.regist(detail);
				  System.out.println("등록 결과: " + result1);
				  
			  }
			  if (i + 1 < result.size()) {
	              Thread.sleep(DELAY_MS); // 다음 요청까지 1분 대기
	          }
		  }
	}
	
	private List<Map<String, Object>> fetchData(int start, int end) throws IOException {
        String apiKey = "6d657a6f546b796a36384d5665716e"; // 실제 인증키로 교체
        String urlStr = String.format(
                "http://openapi.seoul.go.kr:8088/%s/json/tvYeyakCOllect/%d/%d/",
                apiKey, start, end
        );

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 299) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();

        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(sb.toString());
        JsonNode rows = root.path("tvYeyakCOllect").path("row");

        List<Map<String, Object>> futsalOnly = new ArrayList<>();
        if (rows.isArray()) {
            for (JsonNode row : rows) {
                String minClassName = row.path("MINCLASSNM").asText("");
                if (minClassName.contains("풋살장")) {
                    Map<String, Object> item = mapper.convertValue(row, new TypeReference<>() {});
                    futsalOnly.add(item);
                }
            }
        }

        return futsalOnly;
    }
	
	
	
	private List<Map<String, Object>> fetchDetailData(String svcid) throws IOException {
        String apiKey = "6d657a6f546b796a36384d5665716e"; // 실제 인증키로 교체
        String urlStr = String.format(
                "http://openapi.seoul.go.kr:8088/%s/json/ListPublicReservationDetail/1/5/%s",
                apiKey, svcid
        );

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 299) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();

        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(sb.toString());
        JsonNode rows = root.path("ListPublicReservationDetail").path("row");

        List<Map<String, Object>> detailList = new ArrayList<>();
        if (rows.isArray()) {
            for (JsonNode row : rows) {
                Map<String, Object> item = mapper.convertValue(row, new TypeReference<>() {});
                detailList.add(item);
            }
        }

        return detailList;
    }

//	// 게시판 등록하기
//	@PostMapping("regist")
//	public ResponseEntity<Object> regist(@RequestBody Board board) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		boardService.registForm(board);
//
//		result.put("error", false);
//		result.put("message", "게시물 등록을 완료했습니다.");
//		return ResponseEntity.ok(result);
//	}
//
//	// 게시물 삭제
//	@PostMapping("delete")
//	public Map<String, Object> delete(@ModelAttribute Board board) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		
//		Board boardDB = boardService.getBoard(board.getBno());
//		
//		// 만약 회원일 경우 DB에 비밀번호가 누락되어 있기에 구분짓기위해 비교
//		String dbPasswd = boardDB.getPasswd();
//	    String inputPasswd = board.getPasswd();
//		
//		if (!Objects.equals(dbPasswd, inputPasswd)) {
//			result.put("error", true);
//			result.put("message", "게시물 비밀번호가 다름니다.");
//			return result;
//		}
//
//		boardService.delete(board.getBno());
//		result.put("error", false);
//		result.put("message", "게시물 삭제 완료했습니다.");
//
//		return result;
//	}
//
//	// 게시판 목록 진입
//	@GetMapping("list")
//	public Map<String, Object> list(String pageNo, String size, String searchValue,  @RequestHeader(value = "supervisor", defaultValue = "N") String supervisor) {
//		
//		Map<String, Object> result = new HashMap<>();
//		//로그인 되어있고, 관리자 일 경우
//		if(supervisor.equals("Y")) {
//			result.put("pageResponse",boardService.list_admin(supervisor,
//					searchValue,
//					Util.parseInt(pageNo, 1),
//					Util.parseInt(size, 10)
//					));
//			return result;
//		}
//		
//	
//	result.put("pageResponse",boardService.list(supervisor,
//			searchValue,
//			Util.parseInt(pageNo, 1),
//			Util.parseInt(size, 10)
//			));
//		
//		return result;
//	}
//
	// 구장 상세 디테일
	@GetMapping("detailView")
	public Map<String, Object> detailView(@RequestParam String SVCID) {
		Map<String, Object> result = new HashMap<>();
		Stadium stadiumDB = stadiumService.getStadium(SVCID);
		if (stadiumDB == null) {
			return result;
		}
		result.put("stadiumDB", stadiumDB);
		return result;
	}
//
//	// 게시판 수정하기폼
//	@RequestMapping("updateForm")
//	public  Map<String, Object>  updateForm(@RequestParam int bno) {
//		Map<String, Object> result = new HashMap<>();
//		Board boardDB = boardService.getBoard(bno);
//		if (boardDB == null) {
//			return result;
//		}
//		result.put("boardDB", boardDB);
//
//		return result;
//	}
//
//	// 게시판 수정
//	@PostMapping("update")
//	public Map<String, Object> update(@ModelAttribute Board board) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		Board boardDB = boardService.getBoard(board.getBno());
//
//		// 만약 회원일 경우 DB에 비밀번호가 누락되어 있기에 구분짓기위해 비교
//		String dbPasswd = boardDB.getPasswd();
//	    String inputPasswd = board.getPasswd();
//		
//		if (!Objects.equals(dbPasswd, inputPasswd)) {
//			result.put("error", true);
//			result.put("message", "게시물 비밀번호가 다름니다.");
//			return result;
//		}
//
//		boardService.update(board);
//		result.put("error", false);
//		result.put("message", "게시물 수정 완료했습니다.");
//
//		return result;
//	}
}
