package com.msa.kyj_prj;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.msa.kyj_prj.match.MatchService;

@SpringBootTest
class KyjPrjApplicationTests {
	@Autowired
	private MatchService matchService;

	@Test
	void testActivatePastMatches() {
	    matchService.cancelMatchesWithInvalidReservation();
	}

	@Test
	void contextLoads() {
	}

}
