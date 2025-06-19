package com.msa.kyj_prj;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.msa.kyj_prj.board.BoardService;
import com.msa.kyj_prj.util.Util;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


/**
 * Handles requests for the application home page.
 */
@RestController 
@Slf4j
public class HomeController {
	
	@Autowired
	BoardService boardService;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	
	// 홈
	@GetMapping("/")
	@ResponseBody
	public Map<String, Object> home(Locale locale) {
		logger.info("Welcome home! The client locale is {}.", locale);
		Map<String, Object> result = new HashMap<>();
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		result.put("serverTime", formattedDate );
		
		result.put("pageResponse",boardService.list("N",
				null,
				Util.parseInt(null, 1),
				Util.parseInt(null, 10)
				));
		
		return result;
	}
}
