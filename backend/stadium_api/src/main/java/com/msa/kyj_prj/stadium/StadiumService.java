package com.msa.kyj_prj.stadium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.kyj_prj.page.PageResponseVO_board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StadiumService{
	private final StadiumDAO stadiumDAO;

	
    private static final int STEP = 1000;
    private static final int DELAY_MS = 20000;
	
	

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
	public Stadium getStadium(String SVCID) {
		return stadiumDAO.getStadium(SVCID);
	}
	
	
	// 비교하는 함수
	public void compareAndUpdateIfChanged(String svcId) throws IOException, InterruptedException {
	    // 1. API에서 최신 데이터 가져오기
		Map<String, Object> newData = fetchDetailData(svcId);

	    // 2. DB에서 기존 데이터 가져오기
		Stadium oldData = getStadium(svcId);

	    if (oldData == null || newData == null || newData.isEmpty()) return;
	    
	    
	    boolean changed = false;

	    if (!Objects.equals(oldData.getSVCNM(), newData.get("SVCNM"))) {
	        oldData.setSVCNM((String) newData.get("SVCNM"));
	        changed = true;
	    }
	    if (!Objects.equals(oldData.getPLACENM(), newData.get("PLACENM"))) {
	        oldData.setPLACENM((String) newData.get("PLACENM"));
	        changed = true;
	    }
	    if (!Objects.equals(oldData.getTELNO(), newData.get("TELNO"))) {
	        oldData.setTELNO((String) newData.get("TELNO"));
	        changed = true;
	    }
	    if (!Objects.equals(oldData.getV_MIN(), newData.get("V_MIN"))) {
	        oldData.setV_MIN((String) newData.get("V_MIN"));
	        changed = true;
	    }
	    if (!Objects.equals(oldData.getV_MAX(), newData.get("V_MAX"))) {
	        oldData.setV_MAX((String) newData.get("V_MAX"));
	        changed = true;
	    }
	    if (!Objects.equals(oldData.getNOTICE(), newData.get("NOTICE"))) {
	        oldData.setNOTICE((String) newData.get("NOTICE"));
	        changed = true;
	    }
	    if (!Objects.equals(oldData.getADRES(), newData.get("ADRES"))) {
	        oldData.setADRES((String) newData.get("ADRES"));
	        changed = true;
	    }
	   
	    // 3. 변경된 경우만 UPDATE 실행
	    if (changed) {
	        stadiumDAO.update(oldData);
	    }
	    
	
            Thread.sleep(DELAY_MS); 

	    
	}
	
		
		
		// 구장 1000개 데이터 가져오는 API
		private List<String> fetchData(int start, int end) throws IOException {
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

	        return svcidList;
	    }
		
		
		// 상세 정보 API
				private Map<String, Object> fetchDetailData(String svcid) throws IOException {
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
			        response.put("code", code);
			        response.put("message", message);
			        response.put("data", detailList);
			        
			        return response;
			    }
	
		
				// 초기에 DB를 담기위한 API
		public void stadiumapi() throws Exception {
			// 데이터 담아줄 List
			List<String> result = new ArrayList<>();
			
			// 데이터를 n개 나누어서 반복
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
		        
		        // 다음 요청까지 1분 대기
		        if (i + 1 < totalSteps) {
		            Thread.sleep(DELAY_MS); 
		        }
			}
			
			// 몇개 가져왔는지 확인용
			System.out.println("모든 요청 완료. 풋살장 데이터 총 갯수: " + result.size());
			
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
				            String code = (String) results.get("CODE");
				            String message = (String) results.get("MESSAGE");
					  
						    int result1 = stadiumDAO.regist(detail);
						    System.out.println("등록 결과: " + result1);
						    
						    // 로그 DB에 기록
				            Map<String, Object> log = new HashMap<>();
				            log.put("svcid", svcId);
				            log.put("code", code);
				            log.put("message", message);
				            stadiumDAO.insertApiDetailLog(log); // MyBatis 또는 JPA DAO
						    
						    
						    if (i + 1 < result.size()) {
						    	Thread.sleep(DELAY_MS); 
						    }
				    	 }
				  
				    }
			    // 다음 요청까지 1분 대기
			    }catch (Exception e) {
			    	System.out.println("❌ 에러 발생: " + e.getMessage());

			        // 실패도 로그에 기록
			        Map<String, Object> errorLog = new HashMap<>();
			        errorLog.put("svcid", svcId);
			        errorLog.put("code", "ERROR-999");
			        errorLog.put("message", e.getMessage());
//			        stadiumDAO.insertApiDetailLog(errorLog);
				}
			}
		}
		
		// API 데이터 카운트
		public int fetchTotalCount() throws Exception {
		    String apiKey = "6d657a6f546b796a36384d5665716e";
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
		
		
		
		// 구장 업데이트
		public void syncAll() throws IOException, InterruptedException {
		    List<String> svcIds = stadiumDAO.findAllSvcIds(); // SVCID 목록 조회
		    System.out.println("데이터 업데이트 시작");
		    for (String svcId : svcIds) {
		    		System.out.println(svcId +  "업데이트 시작" );
			        compareAndUpdateIfChanged(svcId);
		    }
		}
}


