package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeUtil {

	public static void main(String[] args) {
		tests("中国/NR, 国民党/NR, 主席/NN, 朱立伦/NR, 当日/NT");
	}

	private static void tests(String test) {
		// [pattern](T1,T2)
		String regex = "^(.*)(([^,]*)\\/NR), 主席/NN, ((.*)\\/NR)(.*)$";
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
			System.out.println(matcher.group(5));
			System.out.println(matcher.group(6));
		}
	}

}