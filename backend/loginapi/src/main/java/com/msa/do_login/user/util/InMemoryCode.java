package com.msa.do_login.user.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class InMemoryCode {

    private final Map<String, String> codeStore = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 코드 저장 및 만료 설정
    public void setCodeWithExpiry(String code, String userPhone, long expireSeconds) {
        codeStore.put(code, userPhone);

        // expire 이후 삭제
        scheduler.schedule(() -> {
            codeStore.remove(code);
        }, expireSeconds, TimeUnit.SECONDS);
    }

    public String getPhoneByCode(String code) {
        return codeStore.get(code);
    }

    public void removeCode(String code) {
        codeStore.remove(code);
    }

    public boolean exists(String code) {
        return codeStore.containsKey(code);
    }
}

