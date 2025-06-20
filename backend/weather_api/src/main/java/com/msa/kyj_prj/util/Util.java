package com.msa.kyj_prj.util;

public class Util {
	// 입력값을 받아 값 변경하는 유틸
		public static int parseInt(String str, int defaultValue) {
			try {
				if (str != null && str.length() != 0) {
					return Integer.parseInt(str);
				}
			} catch (Exception e) {}
			return defaultValue;
		}
}
