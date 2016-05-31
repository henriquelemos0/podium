package br.usp.each.saeg.podium.jaguar.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBException;

import br.usp.each.saeg.jaguar.core.utils.FileUtils;
import br.usp.each.saeg.podium.jaguar.model.FaultLocalizationEntry;
import br.usp.each.saeg.podium.jaguar.model.FaultLocalizationReport;

@SuppressWarnings("restriction")
public class RScript {

	private static final String XML_EXTENSION = ".xml";
	private File inputFolder;
	private File outputFile;
	StringBuffer scriptBuffer = new StringBuffer();
	Map<String, LinkedList<Integer>> varMap = new LinkedHashMap<String, LinkedList<Integer>>();
	
	public RScript(File inputFolder, File outputFile) {
		this();
		this.outputFile = outputFile;
		this.inputFolder = inputFolder;
	}
	
	public RScript() {
		super();
	}

	public void build() {
		List<FaultLocalizationEntry> scriptObjectList = getJaguarFiles(inputFolder);
		createScript(scriptObjectList, outputFile);
	}

	public void createScript(final List<FaultLocalizationEntry> scriptObjectList, final File outputFile) {	
		appendScriptln("library(\"BSDA\")");
		appendScriptln("library(\"nortest\")");
		
		for (FaultLocalizationEntry faultLocalizationEntry : scriptObjectList) {
			String key = getKey(faultLocalizationEntry);
			appendMapValue(key, faultLocalizationEntry.getMaxCost());
		}
		
		// print vectors
		for (String key : varMap.keySet()) {
			appendScript(key);
			appendScript(" <- c(");
			String listToString = varMap.get(key).toString();
			listToString = listToString.substring(1, listToString.length()-1);
			appendScript(listToString);
			appendScriptln(")");
		}
		
		// print normal test
		for (String key : varMap.keySet()) {
			appendScript("ad.test(");
			appendScript(key);
			appendScriptln(")");
		}

		createFinalFile(outputFile, scriptBuffer.toString());
	}

	private void appendMapValue(String key, Integer maxCost) {
		LinkedList<Integer> value = varMap.get(key);
		
		if (value == null){
			value = new LinkedList<Integer>();
			varMap.put(key, value);
		}
		
		value.add(maxCost);
	}

	private String getKey(FaultLocalizationEntry faultLocalizationEntry) {
		return faultLocalizationEntry.getCoverageType() + "_" + faultLocalizationEntry.getHeuristic() + "_N" + faultLocalizationEntry.getNeighborhoodLimit();
	}

	private void appendScript(String s) {
		scriptBuffer.append(s);
	}
	
	private void appendScriptln(String s) {
		scriptBuffer.append(s);
		scriptBuffer.append("\n");
	}

	private void createFinalFile(final File outputFile, final String fileContent) {
		FileWriter writer;
		try {
			outputFile.createNewFile();
			writer = new FileWriter(outputFile);
			writer.write(fileContent); 
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Extract the FaultLocalizationEntry objects from each Xml file inside the given folder.
	 * 
	 * @param folder The fodler to be searched.
	 * @return A sorted list of FaultLocalizationEntry objects.
	 */
	public List<FaultLocalizationEntry> getJaguarFiles(final File folder) {

		List<File> resultFiles = FileUtils.findFilesEndingWith(folder, new String[] { XML_EXTENSION });
		List<FaultLocalizationEntry> reportEntryList = new ArrayList<FaultLocalizationEntry>();

		for (File file : resultFiles) {		
			FaultLocalizationReport report = null;
			try{
		    
				javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(FaultLocalizationReport.class);
		        
				javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
				report = (FaultLocalizationReport) unmarshaller.unmarshal(file);
				reportEntryList.addAll(report.getEntries());
				
			}catch(DataBindingException e){				
				continue;
			} catch (JAXBException e) {
				continue;
			}
			
		}

		Collections.sort(reportEntryList);

		return reportEntryList;
	}

}
