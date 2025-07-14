package com.msa.kyj_prj.util;

import java.util.List;

import com.msa.kyj_prj.dto.Slot;
import com.msa.kyj_prj.dto.Stadium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StadiumDetailResponse {
	private Stadium stadium;
	private List<Slot> slot; 
}
