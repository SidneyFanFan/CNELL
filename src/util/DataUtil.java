package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

public class DataUtil {

	public static void printMatirx(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.printf("%s ", matrix[i][j]);
			}
			if (i != matrix.length)
				System.out.println();
		}
	}

	public static int[][] initMatirx(int m, int n, int value) {
		int[][] matrix = new int[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				matrix[i][j] = value;
			}
		}
		return matrix;
	}

	public static void discRangeString(String[] list, int range,
			ArrayList<int[]> discMatrix,
			HashMap<String, HashMap<Integer, String>> codeMap, String name) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		int len = list.length;
		int[] numList = new int[list.length - 1];
		for (int j = 1; j < len; j++) {
			int size = Integer.valueOf(list[j].trim());
			numList[j - 1] = size / range;
			map.put(numList[j - 1], String.format("%d~%d", numList[j - 1]
					* range, numList[j - 1] * range + range));
		}
		discMatrix.add(numList);
		codeMap.put(name, map);
	}

	public static void discRangeStringWithStartValue(String[] list, int range,
			ArrayList<int[]> discMatrix,
			HashMap<String, HashMap<Integer, String>> codeMap, String name) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		int len = list.length;
		int[] numList = new int[list.length - 1];
		for (int j = 1; j < len; j++) {
			int size = Integer.valueOf(list[j].trim());
			numList[j - 1] = (size - 1500) / range;
			map.put(numList[j - 1], String.format("%d~%d", numList[j - 1]
					* range, numList[j - 1] * range + range));
		}
		discMatrix.add(numList);
		codeMap.put(name, map);
	}

	public static void discBiString(String[] list, String reg,
			ArrayList<int[]> discMatrix,
			HashMap<String, HashMap<Integer, String>> codeMap, String name1,
			String name2) {
		HashMap<Integer, String> map1 = new HashMap<Integer, String>();
		HashMap<Integer, String> map2 = new HashMap<Integer, String>();
		int len = list.length;
		int[] list1 = new int[len - 1];
		int[] list2 = new int[len - 1];
		for (int j = 1; j < len; j++) {
			String value = list[j];
			int regIndex = value.indexOf(reg);
			String value1, value2;
			if (regIndex == -1) {
				value1 = value;
				value2 = "nil";
			} else {
				value1 = value.substring(0, regIndex);
				value2 = value.substring(regIndex + 1);
			}
			list1[j - 1] = value1.hashCode();
			list2[j - 1] = value2.hashCode();
			map1.put(list1[j - 1], value1);
			map2.put(list2[j - 1], value2);
		}
		discMatrix.add(list1);
		// discMatrix.add(list2);
		codeMap.put(name1, map1);
		// codeMap.put(name2, map2);
	}

	public static void discUniString(String[] list,
			ArrayList<int[]> discMatrix,
			HashMap<String, HashMap<Integer, String>> codeMap, String name) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		int len = list.length;
		int[] uniList = new int[list.length - 1];
		for (int j = 1; j < len; j++) {
			uniList[j - 1] = list[j].hashCode();
			map.put(uniList[j - 1], list[j]);
		}
		discMatrix.add(uniList);
		codeMap.put(name, map);
	}

	public static void discNumber(String[] list, ArrayList<int[]> discMatrix,
			HashMap<String, HashMap<Integer, String>> codeMap, String name) {
		int len = list.length;
		int[] numList = new int[list.length - 1];
		for (int j = 1; j < len; j++) {
			numList[j - 1] = Integer.valueOf(list[j]);
		}
		discMatrix.add(numList);
	}

	public static void jointList(ArrayList<Integer> list1,
			ArrayList<Integer> list2, ArrayList<Integer> jointList) {
		for (int integer : list1) {
			if (list2.contains(integer)) {
				jointList.add(integer);
			}
		}
	}

	public static int arraySum(int[] entity) {
		int sum = 0;
		for (int i : entity) {
			sum += i;
		}
		return sum;
	}

	public static ArrayList<HashSet<Integer>> arrayListWithout(
			ArrayList<HashSet<Integer>> conditionSetList, int j) {
		ArrayList<HashSet<Integer>> removedList = new ArrayList<HashSet<Integer>>();
		for (int i = 0; i < conditionSetList.size(); i++) {
			if (i != j) {
				removedList.add(conditionSetList.get(i));
			}
		}
		return removedList;
	}

	public static HashSet<Integer> intersectSets(
			ArrayList<HashSet<Integer>> setList) {
		HashSet<Integer> intersection = new HashSet<Integer>();
		HashSet<Integer> first = setList.get(0);
		for (Integer integer : first) {
			boolean allContains = true;
			for (int i = 0; i < setList.size(); i++) {
				if (!setList.get(i).contains(integer)) {
					allContains = false;
					break;
				}
			}
			if (allContains) {
				intersection.add(integer);
			}
		}
		return intersection;
	}

	public static ArrayList<HashSet<Integer>> arrayListWithCombination(
			ArrayList<HashSet<Integer>> conditionSetList, int[] combination) {
		ArrayList<HashSet<Integer>> removedList = new ArrayList<HashSet<Integer>>();
		for (int i = 0; i < combination.length; i++) {
			if (combination[i] == 1) {
				removedList.add(conditionSetList.get(i));
			}
		}
		return removedList;
	}

	public static boolean subArray(int[] localCombination, int[] combination) {
		int[] joint = intersectArray(localCombination, combination);
		boolean compare = compareArray(localCombination, joint);
		return compare;
	}

	public static boolean compareArray(int[] a1, int[] a2) {
		for (int i = 0; i < a1.length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

	public static int[] intersectArray(int[] a1, int[] a2) {
		int[] joint = new int[a1.length];
		for (int i = 0; i < joint.length; i++) {
			if (a1[i] == 1 && a1[i] == a2[i]) {
				joint[i] = 1;
			} else {
				joint[i] = 0;
			}
		}
		return joint;
	}

	public static String printArray(String[] array) {
		String str = "";
		ArrayList<String> politicsList = new ArrayList<String>();
		for (String string : array) {
			string = string.trim();
			if (!string.isEmpty()) {
				politicsList.add(string.replace('\'', '_').replace('.', '_')
						.replace('"', '_').replace(' ', '_').replace('-', '_'));
			}
		}
		str = politicsList.toString();
		return str;
	}

	public static List<String> parseArrayString(String string) {
		ArrayList<String> list = new ArrayList<String>();
		int leftParr = string.indexOf('[');
		int rightParr = string.lastIndexOf(']');
		String inner = string.substring(leftParr + 1, rightParr);
		String[] eles = inner.split(",");
		for (String ele : eles) {
			list.add(ele.trim());
		}
		return list;
	}

	public static List<Entry<String, Integer>> convertMapToSortedList(
			HashMap<String, Integer> map) {
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>();
		for (Entry<String, Integer> en : map.entrySet()) {
			int pos = list.size();
			for (int i = 0; i < list.size(); i++) {
				if (en.getValue().intValue() > list.get(i).getValue().intValue()) {
					pos = i;
					break;
				}
			}
			list.add(pos, en);
		}
		return list;
	}

	public static void main(String[] args) {
		// test convertMapToSortedList
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 1);
		map.put("b", 3);
		map.put("c", 2);
		map.put("d", 4);
		List<Entry<String, Integer>> list = convertMapToSortedList(map);
		System.out.println(list);
	}
}
