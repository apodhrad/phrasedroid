package org.apodhrad.phrasedroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuList {

	private static List<String> list = new ArrayList<String>(
			Arrays.asList(new String[] { "Item 1", "Item 2" }));

	public static String[] getMenuItems() {
		return list.toArray(new String[list.size()]);
	}

	public static void addItem(String item) {
		list.add(item);
	}

	public static String getItem(int index) {
		return list.get(index - 1);
	}

	public static String getContent(int index) {
		return "Selected " + getItem(index);
	}
}
