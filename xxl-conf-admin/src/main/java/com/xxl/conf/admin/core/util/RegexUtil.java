package com.xxl.conf.admin.core.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * regex tool
 *
 * @author xuxueli 2015-5-12 23:57:48
 */
public class RegexUtil {

	// 字母和数字组成
	public static final String abc_ABC_number = "^[a-zA-Z0-9]+$";

	// 以小写字母开头，由小写字母、数字、中划线、点组成（校验配置 = 项目名前缀+配置后缀）
	public static final Pattern abc_number_line_point_pattern = Pattern.compile("^[a-z][a-z0-9-.]*$");

	/**
	 * regex match
	 *
	 * @param regex=	regex str
	 * @param str		desc str
	 * @return
	 */
	public static boolean matches(String regex, String str) {
		if (regex==null || str == null) {
			return false;
		}

		Pattern pattern = Pattern.compile(regex);
		return matches(pattern, str);
	}

	/**
	 * regex match
	 *
	 * @param pattern	regex pattern
	 * @param str		desc str
	 * @return
	 */
	public static boolean matches(Pattern pattern, String str) {
		if (pattern==null || str == null) {
			return false;
		}

		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}


	public static void main(String[] args) {
		System.out.println(matches(abc_number_line_point_pattern, null));
		System.out.println(matches(abc_number_line_point_pattern, ""));
		System.out.println(matches(abc_number_line_point_pattern, "./"));
		System.out.println(matches(abc_number_line_point_pattern, "EE"));
		System.out.println(matches(abc_number_line_point_pattern, "demo-project.test.conf"));

		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			matches(abc_number_line_point_pattern, "abc.123.abc"+i);
		}
		long end = System.currentTimeMillis();
		System.out.println("cost = " + (end-start));
	}


}