package com.msa.kyj_prj.recruit.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> list;
    private int total;
    private int page;
    private int size;
}
