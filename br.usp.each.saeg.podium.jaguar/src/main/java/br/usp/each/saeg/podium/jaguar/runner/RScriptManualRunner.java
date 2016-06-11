package br.usp.each.saeg.podium.jaguar.runner;

import java.io.File;
import java.io.FileNotFoundException;

import br.usp.each.saeg.podium.jaguar.report.RScript;

public class RScriptManualRunner {

	public static void main(String[] args) throws FileNotFoundException{

		String pathname = "C:\\Users\\unknown\\Dropbox\\Henrique\\POS\\Results\\jaguar-results\\All-b51";
		final File inputFolder = new File(pathname);
		final File outputFile = new File(pathname + "\\RScript.R");
		
		RScript rScript = new RScript(inputFolder, outputFile);
		rScript.build();

	}
	
}