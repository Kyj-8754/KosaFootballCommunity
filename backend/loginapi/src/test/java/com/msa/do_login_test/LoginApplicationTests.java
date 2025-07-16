package com.msa.do_login_test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class LoginApplicationTests {

//	@Autowired
//    private MockMvc mockMvc;

//    @Autowired
//    private ObjectMapper objectMapper;

//    @Test
//    void friend_req_succes() throws Exception {
//        Map<String, Object> request = new HashMap<>();
//        request.put("requesterNo", 21);
//        request.put("requestedNo", 22);
//
//        mockMvc.perform(post("/login_api/mypage/request")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.res_code").value("200"));
//    }

//    @Test
//    void friend_req_fail() throws Exception {
//        Map<String, Object> request = new HashMap<>();
//        request.put("requesterNo", 21);
//        // requestedNo 누락
//
//        mockMvc.perform(post("/login_api/mypage/request")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.res_code").value("400"));
//    }

//    @Test
//    void friend_req_again_fail() throws Exception {
//        Map<String, Object> request = new HashMap<>();
//        request.put("requesterNo", 21);
//        request.put("requestedNo", 22);
//
//        // 먼저 한 번 성공적으로 요청
//        mockMvc.perform(post("/login_api/mypage/request")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        // 중복 요청 시도
//        mockMvc.perform(post("/login_api/mypage/request")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().is5xxServerError())
//                .andExpect(jsonPath("$.res_code").value("500"));
//    }

}
