package com.vcutils;

import java.util.LinkedList;
import java.util.regex.Pattern;

public class TextUtils {

	public static String normalizeFileName(String filename){
		// | \ ? * < " : > + [ ] / '
		Pattern validFileNamePattern = Pattern.compile("[|\\\\?\\*<\":>\\+\\[\\]\\/\\']");
		return validFileNamePattern.matcher(filename).replaceAll("_");
	}
	
	public static String[] splitBy(String text, String separator){
		if (separator == null || text == null)
		return null;
		
		LinkedList<String> strings = new LinkedList<String>();
		
		while (text.indexOf(separator) != -1) {
			String s = text.substring(0, text.indexOf(separator));
			if (s.length()>0)
				strings.add(s);
			text = text.substring(text.indexOf(separator) + separator.length());
		}
		
		return strings.toArray(new String[]{});
	}
	
}
