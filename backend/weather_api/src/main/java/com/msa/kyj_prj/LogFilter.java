package com.msa.kyj_prj;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class LogFilter
 */
public class LogFilter implements Filter {

    /**
     * Default constructor. 
     */
    public LogFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 실행 시작 시간 
		long tick = System.nanoTime();
		// 로그 uri얻기 위해서 사용
		HttpServletRequest req = (HttpServletRequest)request;
		// 전처리 부분
		
		chain.doFilter(request, response); // -> 처리하는 부분
		// 후처리 부분
		
		// 완료 시간
		tick = System.nanoTime() - tick;
		System.out.println(req.getRequestURI() + " 실행 시간 : " + tick);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		String value = fConfig.getInitParameter("변수명");
		System.out.println("변수명 : " + value);
		
	}

}
