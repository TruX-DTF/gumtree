package edu.lu.uni.serval.gumtree.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp {
	
	public static boolean containASTNodeType(String string) {
		String regularExp = ".+\\d+@@.+";
		Pattern pattern = Pattern.compile(regularExp);

		Matcher res = pattern.matcher(string.trim());
		if (res.matches()) {
			return true;
		}
		return false;
	}
	
}
