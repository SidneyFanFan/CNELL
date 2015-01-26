package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class FileUtil {

	public FileUtil() {
	}

	/**
	 * Read sheet at certain index in an excel
	 * 
	 * @param filePath
	 *            -excel path
	 * @param sheetInex
	 *            -sheet index
	 * @return
	 */
	public String[][] readExcelSheet(String filePath, int sheetInex) {
		String[][] sheet = null;
		try {
			File file = new File(filePath);
			InputStream is = new FileInputStream(file);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(sheetInex);
			int nrow = rs.getRows();
			int ncol = rs.getColumns();
			sheet = new String[ncol][nrow];
			for (int i = 0; i < ncol; i++) {
				for (int j = 0; j < nrow; j++) {
					sheet[i][j] = rs.getCell(i, j).getContents();
				}
			}
			rwb.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sheet;
	}

	public static String readFile(String filePath) {
		String content = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			while (br.ready()) {
				content += br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static void exportStringArray(Object[] output, String filepath) {
		try {
			FileWriter fw = new FileWriter(filepath);
			for (int i = 0; i < output.length; i++) {
				fw.write(output[i].toString());
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String[]> readExcelSheetAsRow(String filePath,
			int sheetInex) {
		ArrayList<String[]> rows = new ArrayList<String[]>();
		try {
			File file = new File(filePath);
			InputStream is = new FileInputStream(file);
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet rs = rwb.getSheet(sheetInex);
			int nrow = rs.getRows();
			for (int i = 0; i < nrow; i++) {
				Cell[] cell = rs.getRow(i);
				String[] row = new String[cell.length];
				for (int j = 0; j < cell.length; j++) {
					row[j] = cell[j].getContents().trim();
				}
				rows.add(row);
			}
			rwb.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rows;
	}

	public static void outputPrologFactBase(String baseName,
			Collection<String> list, String filepath) {
		String str = String.format("%s(%s).", baseName, list.toString());
		try {
			FileWriter fw = new FileWriter(filepath);
			fw.write(str);
			fw.write("\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<String> readFileByLine(String filePath) {
		ArrayList<String> content = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			while (br.ready()) {
				content.add(br.readLine());
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static void exportHashMapByline(HashMap<String, String> map,
			String exportPath) {
		try {
			FileWriter fw = new FileWriter(exportPath);
			for (Entry<String, String> en : map.entrySet()) {
				fw.write(en.getKey());
				fw.write("\n");
				fw.write(en.getValue());
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void exportHashMapByEquation(HashMap<?,?> map,
			String exportPath) {
		try {
			FileWriter fw = new FileWriter(exportPath);
			for (Entry<?, ?> en : map.entrySet()) {
				fw.write(en.toString());
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
