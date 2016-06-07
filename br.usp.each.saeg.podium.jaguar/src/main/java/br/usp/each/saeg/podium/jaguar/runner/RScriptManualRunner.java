package br.usp.each.saeg.podium.jaguar.runner;

import java.io.File;
import java.io.FileNotFoundException;

import br.usp.each.saeg.podium.jaguar.report.RScript;

public class RScriptManualRunner {

	public static void main(String[] args) throws FileNotFoundException{

		final File inputFolder = new File("C:\\Users\\unknown\\Dropbox\\Henrique\\POS\\Results\\jaguar-results\\AllOneDefect");
		final File outputFile = new File("C:\\Users\\unknown\\Dropbox\\Henrique\\POS\\Results\\jaguar-results\\AllOneDefect\\RScript.R");
		
		RScript rScript = new RScript(inputFolder, outputFile);
		rScript.build();

	}
	
}