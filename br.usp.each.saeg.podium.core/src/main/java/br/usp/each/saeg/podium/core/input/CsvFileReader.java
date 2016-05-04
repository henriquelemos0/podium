package br.usp.each.saeg.podium.core.input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvFileReader {
	
	//Delimiter used in CSV file
	private static final String CVS_SPLIT_BY = ",";
	
	public static List<String[]> readCsvFile(String csvFile) {
		
		BufferedReader bufferedReader = null;
		String line = "";
		List<String[]> programsArray = new ArrayList<String[]>();

		try {
			
			bufferedReader = new BufferedReader(new FileReader(csvFile));
			while ((line = bufferedReader.readLine()) != null) {

				programsArray.add(line.split(CVS_SPLIT_BY));

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return programsArray;
	}
}