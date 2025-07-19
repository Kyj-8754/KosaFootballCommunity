package com.msa.kyj_prj.recruit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequestDTO {

    private int page = 1;
    private int size = 10;
    private String keyword;

    
    public int getStartRow() {
        return (Math.max(page, 1) - 1) * Math.max(size, 1);
    }

    public void setPage(int page) {
        this.page = Math.max(page, 1); // 1페이지보다 작은 값 방지
    }

    public void setSize(int size) {
        this.size = Math.max(size, 1); // 1개보다 작은 값 방지
    }
}
