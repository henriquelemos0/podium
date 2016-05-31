package br.usp.each.saeg.podium.jaguar.runner;

import java.io.File;
import java.io.FileNotFoundException;

import br.usp.each.saeg.podium.jaguar.report.RScript;

public class RScriptManualRunner {

	public static void main(String[] args) throws FileNotFoundException{

		final File inputFolder = new File("C:\\Users\\unknown\\workspace\\luna\\runtime-New_configuration\\.podium\\2016-05-25_21-28-03");
		final File outputFile = new File("C:\\Users\\unknown\\workspace\\luna\\runtime-New_configuration\\.podium\\2016-05-25_21-28-03\\RScript.R");
		
		RScript rScript = new RScript(inputFolder, outputFile);
		rScript.build();

	}
	
}