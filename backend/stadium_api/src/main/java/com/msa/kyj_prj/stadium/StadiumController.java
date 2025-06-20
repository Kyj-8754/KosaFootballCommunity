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
		// 데이터 담아줄 List
		List<Map<String, Object>> result = new ArrayList<>();
		// 데이터를 n개 나누어서 반복
		int totalSteps = (int) Math.ceil((double) TOTAL_COUNT / STEP);
		
		
		// api 1차 요청, 1분 간격, 500개씩 호출시키려고함
		for(int i = 0; i < totalSteps; i++) {
			
			int start = i * STEP + 1;
	        int end = Math.min(start + STEP - 1, TOTAL_COUNT);
	        
	        // 현재 어디부분 하는지 체크
	        System.out.printf("[%d/%d] 요청: %d ~ %d\n", i + 1, totalSteps, start, end);
	        
	        // 패치 시작
	        List<Map<String, Object>> futsalList = fetchData(start, end);
	        
	        // 결과값 담아줌
	        result.addAll(futsalList);
	        
	        // 다음 요청까지 1분 대기
	        if (i + 1 < totalSteps) {
	            Thread.sleep(DELAY_MS); 
	        }
		}
		
		// 몇개 가져왔는지 확인용
		System.out.println("모든 요청 완료. 풋살장 데이터 총 갯수: " + result.size());
		
		// 구장 상세 찾아오기
		for(int i = 0;  i < result.size(); i ++) {
			
			String svcId = (String) result.get(i).get("SVCID");
		    System.out.printf("[%d]번째 요청: \n", i);
		    try {
		    List<Map<String, Object>> details = fetchDetailData(svcId);
		    // 
		    if (!details.isEmpty()) {
			    Map<String, Object> detail = details.get(0);
		  
			    int result1 = stadiumDAO.regist(detail);
			    System.out.println("등록 결과: " + result1);
			    if (i + 1 < result.size()) {
			    	Thread.sleep(DELAY_MS); 
			    }
		  
		    }
		    // 다음 요청까지 1분 대기
		    }catch (Exception e) {
		    	System.out.println("❌ 에러 발생: " + e.getMessage());
			}
		}
	}
	
	// 구장 500개 데이터 가져오는 api
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
	
	
	// 상세 정보 
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
		Stadium stadiumDB = stadiumService.getStadium(SVCID);
		if (stadiumDB == null) {
			return result;
		}
		result.put("stadiumDB", stadiumDB);
		return result;
	}

	// 구장 업데이트
	@PostMapping("update")
	public void syncAll() throws IOException {
	    List<String> svcIds = stadiumDAO.findAllSvcIds(); // SVCID 목록 조회
	    System.out.println("데이터 업데이트 시작");
	    for (String svcId : svcIds) {
	    		System.out.println(svcId +  "업데이트 시작" );
		        compareAndUpdateIfChanged(svcId);
	    }
	}
	
	
	public void compareAndUpdateIfChanged(String svcId) throws IOException {
	    // 1. API에서 최신 데이터 가져오기
		 List<Map<String, Object>> newData = fetchDetailData(svcId);

	    // 2. DB에서 기존 데이터 가져오기
		 Map<String, Object> oldData = detailView(svcId);

	    if (oldData == null || newData.isEmpty() || newData == null) return;
	    
	    Map<String, Object> apiMap = newData.get(0); // API 데이터
	    
	    boolean changed = false;

	    // 비교 대상 컬럼들
	    String[] keysToCompare = { "SVCNM", "PLACENM", "TELNO", "V_MIN", "V_MAX", "NOTICE", "ADRES" };
	    
	    for (String key : keysToCompare) {
	        Object oldVal = oldData.get(key);
	        Object newVal = apiMap.get(key);

	        if (!Objects.equals(oldVal, newVal)) {
	            changed = true;
	            break;
	        }
	    }
	   
	    // 3. 변경된 경우만 UPDATE 실행
	    if (changed) {
	        stadiumDAO.update(oldData);
	    }
	    
	}
}
