package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeUtil {

	public static void main(String[] args) {
		tests2("[进行/VV, 准确的/AD](参数/NN, 设置/NN)=1");
	}

	public static void tests(String test) {
		// [pattern](T1,T2)
		String regex = "^(.*)\\((.*), (.*)\\)(.*)$";
		// 正则式
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(test);
		// 使用正则式匹配字符串
		boolean ret = matcher.find();
		// 查找上一个匹配式不是存在
		if (ret) {
			// 查找相应的分组，也就是()对应的字符串
			// 分组0是整个字符串
			// 1 ((//d+)[Yy])对应内容
			// 2 (//d+)对应内容
			// ....
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
			System.out.println(matcher.group(4));
		}
	}

	public static void tests2(String test) {
		// [pattern](T1,T2)
		String regex = "^\\[(.*)\\]\\((((.*)\\/(.*)),((.*)\\/(.*)))\\)=(.*)$";
		// 正则式
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(test);
		// 使用正则式匹配字符串
		boolean ret = matcher.find();
		// 查找上一个匹配式不是存在
		if (ret) {
			// 查找相应的分组，也就是()对应的字符串
			// 分组0是整个字符串
			// 1 ((//d+)[Yy])对应内容
			// 2 (//d+)对应内容
			// ....
			for (int i = 1; i < 10; i++) {
				System.out.println(matcher.group(i));
			}
		}
	}

}