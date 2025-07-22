package com.msa.kyj_prj.stadium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.kyj_prj.dto.Slot;
import com.msa.kyj_prj.dto.Stadium;
import com.msa.kyj_prj.page.PageResponseVO_board;
import com.msa.kyj_prj.util.StadiumDetailResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StadiumService{
	private final StadiumDAO stadiumDAO;
	
	@Value("apikey")
	String apiKey;

	// 1초마다 값 불러내도록 하기위해 설정
    private static final int STEP = 1000;
    private static final int DELAY_MS = 1000;
	
	

	// 구장 리스트 가져오기	
	public PageResponseVO_board list(String searchType, String searchValue, int pageNumber) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start", (pageNumber-1)*9);
		map.put("end", 9);
		map.put("searchValue", searchValue);
		map.put("searchType", searchType);
		return new PageResponseVO_board(
				stadiumDAO.list(map),
				stadiumDAO.getTotalCount(map),
				pageNumber,
				searchType,
				searchValue);
	}
	
	// 구장 상세정보
	public StadiumDetailResponse getStadium(String SVCID) {
		Stadium stadium = stadiumDAO.getStadium(SVCID);
		List<Slot> slot = stadiumDAO.getAvailableDate(SVCID);
		
		return new StadiumDetailResponse(stadium, slot);
	}
	
		
		
	// 구장 1000개 데이터 가져오는 API
	private List<String> fetchData(int start, int end) throws IOException {
	String urlStr = String.format("http://openapi.seoul.go.kr:8088/%s/json/tvYeyakCOllect/%d/%d/",
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

        List<String> svcidList = new ArrayList<>();
        if (rows.isArray()) {
            for (JsonNode row : rows) {
                String minClassName = row.path("MINCLASSNM").asText("");
                if (minClassName.contains("풋살장")) {
                	String svcId = row.path("SVCID").asText();
                    if (!svcId.isEmpty()) {
                    	svcidList.add(svcId);
                    }
                }
            }
        }
        // RAW데이터에서 풋살장만 필터로 걸어서 SVCID, 서비스 ID만 반환
        return svcidList;
    }
		
		
	// 상세 정보 API
	private Map<String, Object> fetchDetailData(String svcid) throws IOException {
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
        JsonNode data = root.path("ListPublicReservationDetail");

        String code = data.path("RESULT").path("CODE").asText();
        String message = data.path("RESULT").path("MESSAGE").asText();
        
        
        // row 부분 담아두기
        List<Map<String, Object>> detailList = new ArrayList<>();
        JsonNode rows = data.path("row");
        if (rows.isArray()) {
            for (JsonNode row : rows) {
                Map<String, Object> item = mapper.convertValue(row, new TypeReference<>() {});
                detailList.add(item);
            }
        }

        
        // 결과 반환
        Map<String, Object> response = new HashMap<>();
        response.put("svcid",svcid);
        response.put("code", code);
        response.put("message", message);
        response.put("data", detailList);
        
        stadiumDAO.insertApiDetailLog(response);
        return response;
    }

	
	// DB를 담기위한 API
	public void stadiumapi() throws Exception {
		// 데이터 담아줄 List
		List<String> result = new ArrayList<>();
			
		int totalCount = fetchTotalCount();
		int totalSteps = (int) Math.ceil((double) totalCount / STEP);
			
		for(int i = 0; i < totalSteps; i++) {
			
			int start = i * STEP + 1;
	        int end = Math.min(start + STEP - 1, totalCount);
	        
	        // 현재 어디부분 하는지 체크
	        System.out.printf("[%d/%d] 요청: %d ~ %d\n", i + 1, totalSteps, start, end);
	        
	        // 1차 API 실행
	        List<String> futsalList = fetchData(start, end);
	        
	        // 결과값 담아줌
	        result.addAll(futsalList);
	        
	        // 트래픽 관리를 위한 딜레이요청
	        if (i + 1 < totalSteps) {
	            Thread.sleep(DELAY_MS); 
	        }
		}
		
		// 구장 상세 찾아오기
		for(int i = 0;  i < result.size(); i ++) {
			// 콘솔에 나옴
			String svcId = result.get(i);
		    System.out.printf("[%d]번째 요청: \n", i);
		    
		    
		    try {
			    Map<String, Object> results = fetchDetailData(svcId);
			    
			    if (!results.isEmpty()) {
			    	List<Map<String, Object>> details = (List<Map<String, Object>>) results.get("data");
				    
			    	 if (!details.isEmpty()) {
				    	Map<String, Object> detail = details.get(0);
				  
					    int result1 = stadiumDAO.registPatch(detail);
					    System.out.println("등록 결과: " + result1);
					    
					    // 딜레이 요청
					    if (i + 1 < result.size()) {
					    	Thread.sleep(DELAY_MS); 
					    }
			    	 }
			  
			    }
		    }catch (Exception e) {
		    	System.out.println("❌ 에러 발생: " + e.getMessage());
			}
		}
	}
	
	// API RAW 데이터 갯수 확인용
	public int fetchTotalCount() throws Exception {
	    String urlStr = String.format(
	        "http://openapi.seoul.go.kr:8088/%s/json/tvYeyakCOllect/1/1/",
	        apiKey
	    );

	    URL url = new URL(urlStr);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
	    conn.setRequestProperty("Content-type", "application/json");

	    BufferedReader rd = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 299)
	        ? new BufferedReader(new InputStreamReader(conn.getInputStream()))
	        : new BufferedReader(new InputStreamReader(conn.getErrorStream()));

	    StringBuilder sb = new StringBuilder();
	    String line;
	    while ((line = rd.readLine()) != null) {
	        sb.append(line);
	    }

	    rd.close();
	    conn.disconnect();

	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode root = mapper.readTree(sb.toString());
	    JsonNode countNode = root
	        .path("tvYeyakCOllect")
	        .path("list_total_count");

	    if (countNode.isMissingNode()) {
	        throw new RuntimeException("list_total_count 값을 찾을 수 없습니다.");
	    }

	    return countNode.asInt();
	}
	
		
		
		// 구장 업데이트 일요일 00시 기준으로 업데이트 시작
		@Scheduled(cron = "0 0 0 ? * SUN", zone = "Asia/Seoul")
		public void syncAll() throws Exception {
			log.info("데이터 업데이트 시작");
		    LocalDateTime start = LocalDateTime.now();
			stadiumapi();
			stadiumDAO.callSyncStadiumProcedure();
			LocalDateTime end = LocalDateTime.now();

	        Duration duration = Duration.between(start, end);
	        
	        log.info("업데이트 걸린 시간: " + duration.getSeconds());
		}
		

}


