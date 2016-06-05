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
		
		appendWilcoxonScript();

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

	private void appendWilcoxonScript() {
		String wilcoxonGiantString = "sink(file='C:\\\\Users\\\\unknown\\\\Dropbox\\\\Henrique\\\\POS\\\\Results\\\\jaguar-results\\\\AllOneDefect\\\\Routput.txt')\n" + 
			"# DF X CF\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	LINE_DRT_N0             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	LINE_DRT_N1             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	LINE_DRT_N3             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	LINE_DRT_N5             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10             	,	LINE_DRT_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	LINE_JACCARD_N0         	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	LINE_JACCARD_N1         	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	LINE_JACCARD_N3         	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	LINE_JACCARD_N5         	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10         	,	LINE_JACCARD_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	LINE_KULCZYNSKI2_N0     	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	LINE_KULCZYNSKI2_N1     	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	LINE_KULCZYNSKI2_N3     	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	LINE_KULCZYNSKI2_N5     	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10     	,	LINE_KULCZYNSKI2_N10    	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	LINE_MCCON_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	LINE_MCCON_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	LINE_MCCON_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	LINE_MCCON_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10           	,	LINE_MCCON_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	LINE_MINUS_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	LINE_MINUS_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	LINE_MINUS_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	LINE_MINUS_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10           	,	LINE_MINUS_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	LINE_OCHIAI_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	LINE_OCHIAI_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	LINE_OCHIAI_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	LINE_OCHIAI_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10          	,	LINE_OCHIAI_N10         	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	LINE_OP_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	LINE_OP_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	LINE_OP_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	LINE_OP_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10              	,	LINE_OP_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	LINE_TARANTULA_N0       	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	LINE_TARANTULA_N1       	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	LINE_TARANTULA_N3       	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	LINE_TARANTULA_N5       	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10       	,	LINE_TARANTULA_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	LINE_WONG3_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	LINE_WONG3_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	LINE_WONG3_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	LINE_WONG3_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10           	,	LINE_WONG3_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	LINE_ZOLTAR_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	LINE_ZOLTAR_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	LINE_ZOLTAR_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	LINE_ZOLTAR_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10          	,	LINE_ZOLTAR_N10         	,paired=TRUE,alternative=\"greater\")\n" + 
			"# CF X DF\n" + 
			"wilcox.test(	LINE_DRT_N0             	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1             	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3             	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5             	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10            	,	DUA_DRT_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0         	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1         	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3         	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5         	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10        	,	DUA_JACCARD_N10         	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0     	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1     	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3     	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5     	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10    	,	DUA_KULCZYNSKI2_N10     	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0           	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1           	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3           	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5           	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10          	,	DUA_MCCON_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0           	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1           	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3           	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5           	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10          	,	DUA_MINUS_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0          	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1          	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3          	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5          	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10         	,	DUA_OCHIAI_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0              	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1              	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3              	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5              	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10             	,	DUA_OP_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0       	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1       	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3       	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5       	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10      	,	DUA_TARANTULA_N10       	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0           	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1           	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3           	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5           	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10          	,	DUA_WONG3_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0          	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1          	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3          	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5          	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10         	,	DUA_ZOLTAR_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N0 DF\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0          	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0      	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0            	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0            	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0           	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0               	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0        	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0            	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0           	,	DUA_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N0 CF\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0              	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0          	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0      	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0            	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0            	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0           	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0        	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0            	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_JACCARD_N0          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_KULCZYNSKI2_N0      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_MCCON_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_MINUS_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_OCHIAI_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_TARANTULA_N0        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_WONG3_N0            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0           	,	LINE_ZOLTAR_N0           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N1 DF\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1          	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1      	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1            	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1            	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1           	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1               	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1        	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1            	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1           	,	DUA_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N1 CF\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1              	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1          	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1      	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1            	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1            	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1           	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1        	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1            	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_JACCARD_N1          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_KULCZYNSKI2_N1      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_MCCON_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_MINUS_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_OCHIAI_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_TARANTULA_N1        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_WONG3_N1            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1           	,	LINE_ZOLTAR_N1           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N3 DF\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3          	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3      	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3            	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3            	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3           	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3               	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3        	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3            	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3           	,	DUA_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N3 CF\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3              	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3          	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3      	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3            	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3            	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3           	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3        	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3            	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_JACCARD_N3          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_KULCZYNSKI2_N3      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_MCCON_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_MINUS_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_OCHIAI_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_TARANTULA_N3        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_WONG3_N3            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3           	,	LINE_ZOLTAR_N3           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N5 DF\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5          	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5      	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5            	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5            	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5           	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5               	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5        	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5            	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5           	,	DUA_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N5 CF\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5              	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5          	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5      	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5            	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5            	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5           	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5        	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5            	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_JACCARD_N5          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_KULCZYNSKI2_N5      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_MCCON_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_MINUS_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_OCHIAI_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_TARANTULA_N5        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_WONG3_N5            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5           	,	LINE_ZOLTAR_N5           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N10 DF\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10              	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10          	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10      	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10            	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10            	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10           	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10               	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10        	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10            	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10           	,	DUA_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# H X H N10 CF\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10          	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10      	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10            	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10            	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10           	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10               	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10        	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10            	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_JACCARD_N10          	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_KULCZYNSKI2_N10      	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_MCCON_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_MINUS_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_OCHIAI_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_OP_N10               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_TARANTULA_N10        	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_WONG3_N10            	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10           	,	LINE_ZOLTAR_N10           	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_DRT 				\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N0              	,	DUA_DRT_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N1              	,	DUA_DRT_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N3              	,	DUA_DRT_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N5              	,	DUA_DRT_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10             	,	DUA_DRT_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10             	,	DUA_DRT_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10             	,	DUA_DRT_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10             	,	DUA_DRT_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_DRT_N10             	,	DUA_DRT_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_DRT				\n" + 
			"wilcox.test(	LINE_DRT_N0               	,	LINE_DRT_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0               	,	LINE_DRT_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0               	,	LINE_DRT_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0               	,	LINE_DRT_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N0               	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1               	,	LINE_DRT_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1               	,	LINE_DRT_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1               	,	LINE_DRT_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1               	,	LINE_DRT_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N1               	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3               	,	LINE_DRT_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3               	,	LINE_DRT_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3               	,	LINE_DRT_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3               	,	LINE_DRT_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N3               	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5               	,	LINE_DRT_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5               	,	LINE_DRT_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5               	,	LINE_DRT_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5               	,	LINE_DRT_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N5               	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_DRT_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_DRT_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_DRT_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_DRT_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_DRT_N10              	,	LINE_DRT_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_JACCARD 				\n" + 
			"wilcox.test(	DUA_JACCARD_N0              	,	DUA_JACCARD_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0              	,	DUA_JACCARD_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0              	,	DUA_JACCARD_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0              	,	DUA_JACCARD_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N0              	,	DUA_JACCARD_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1              	,	DUA_JACCARD_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1              	,	DUA_JACCARD_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1              	,	DUA_JACCARD_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1              	,	DUA_JACCARD_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N1              	,	DUA_JACCARD_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3              	,	DUA_JACCARD_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3              	,	DUA_JACCARD_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3              	,	DUA_JACCARD_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3              	,	DUA_JACCARD_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N3              	,	DUA_JACCARD_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5              	,	DUA_JACCARD_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5              	,	DUA_JACCARD_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5              	,	DUA_JACCARD_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5              	,	DUA_JACCARD_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N5              	,	DUA_JACCARD_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10             	,	DUA_JACCARD_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10             	,	DUA_JACCARD_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10             	,	DUA_JACCARD_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10             	,	DUA_JACCARD_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_JACCARD_N10             	,	DUA_JACCARD_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_JACCARD				\n" + 
			"wilcox.test(	LINE_JACCARD_N0               	,	LINE_JACCARD_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0               	,	LINE_JACCARD_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0               	,	LINE_JACCARD_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0               	,	LINE_JACCARD_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N0               	,	LINE_JACCARD_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1               	,	LINE_JACCARD_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1               	,	LINE_JACCARD_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1               	,	LINE_JACCARD_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1               	,	LINE_JACCARD_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N1               	,	LINE_JACCARD_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3               	,	LINE_JACCARD_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3               	,	LINE_JACCARD_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3               	,	LINE_JACCARD_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3               	,	LINE_JACCARD_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N3               	,	LINE_JACCARD_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5               	,	LINE_JACCARD_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5               	,	LINE_JACCARD_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5               	,	LINE_JACCARD_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5               	,	LINE_JACCARD_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N5               	,	LINE_JACCARD_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10              	,	LINE_JACCARD_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10              	,	LINE_JACCARD_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10              	,	LINE_JACCARD_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10              	,	LINE_JACCARD_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_JACCARD_N10              	,	LINE_JACCARD_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_KULCZYNSKI2 				\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0              	,	DUA_KULCZYNSKI2_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0              	,	DUA_KULCZYNSKI2_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0              	,	DUA_KULCZYNSKI2_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0              	,	DUA_KULCZYNSKI2_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N0              	,	DUA_KULCZYNSKI2_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1              	,	DUA_KULCZYNSKI2_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1              	,	DUA_KULCZYNSKI2_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1              	,	DUA_KULCZYNSKI2_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1              	,	DUA_KULCZYNSKI2_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N1              	,	DUA_KULCZYNSKI2_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3              	,	DUA_KULCZYNSKI2_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3              	,	DUA_KULCZYNSKI2_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3              	,	DUA_KULCZYNSKI2_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3              	,	DUA_KULCZYNSKI2_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N3              	,	DUA_KULCZYNSKI2_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5              	,	DUA_KULCZYNSKI2_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5              	,	DUA_KULCZYNSKI2_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5              	,	DUA_KULCZYNSKI2_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5              	,	DUA_KULCZYNSKI2_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N5              	,	DUA_KULCZYNSKI2_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10             	,	DUA_KULCZYNSKI2_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10             	,	DUA_KULCZYNSKI2_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10             	,	DUA_KULCZYNSKI2_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10             	,	DUA_KULCZYNSKI2_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_KULCZYNSKI2_N10             	,	DUA_KULCZYNSKI2_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_KULCZYNSKI2				\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0               	,	LINE_KULCZYNSKI2_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0               	,	LINE_KULCZYNSKI2_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0               	,	LINE_KULCZYNSKI2_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0               	,	LINE_KULCZYNSKI2_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N0               	,	LINE_KULCZYNSKI2_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1               	,	LINE_KULCZYNSKI2_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1               	,	LINE_KULCZYNSKI2_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1               	,	LINE_KULCZYNSKI2_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1               	,	LINE_KULCZYNSKI2_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N1               	,	LINE_KULCZYNSKI2_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3               	,	LINE_KULCZYNSKI2_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3               	,	LINE_KULCZYNSKI2_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3               	,	LINE_KULCZYNSKI2_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3               	,	LINE_KULCZYNSKI2_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N3               	,	LINE_KULCZYNSKI2_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5               	,	LINE_KULCZYNSKI2_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5               	,	LINE_KULCZYNSKI2_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5               	,	LINE_KULCZYNSKI2_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5               	,	LINE_KULCZYNSKI2_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N5               	,	LINE_KULCZYNSKI2_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10              	,	LINE_KULCZYNSKI2_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10              	,	LINE_KULCZYNSKI2_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10              	,	LINE_KULCZYNSKI2_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10              	,	LINE_KULCZYNSKI2_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_KULCZYNSKI2_N10              	,	LINE_KULCZYNSKI2_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_MCCON 				\n" + 
			"wilcox.test(	DUA_MCCON_N0              	,	DUA_MCCON_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0              	,	DUA_MCCON_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0              	,	DUA_MCCON_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0              	,	DUA_MCCON_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N0              	,	DUA_MCCON_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1              	,	DUA_MCCON_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1              	,	DUA_MCCON_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1              	,	DUA_MCCON_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1              	,	DUA_MCCON_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N1              	,	DUA_MCCON_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3              	,	DUA_MCCON_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3              	,	DUA_MCCON_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3              	,	DUA_MCCON_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3              	,	DUA_MCCON_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N3              	,	DUA_MCCON_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5              	,	DUA_MCCON_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5              	,	DUA_MCCON_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5              	,	DUA_MCCON_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5              	,	DUA_MCCON_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N5              	,	DUA_MCCON_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10             	,	DUA_MCCON_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10             	,	DUA_MCCON_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10             	,	DUA_MCCON_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10             	,	DUA_MCCON_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MCCON_N10             	,	DUA_MCCON_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_MCCON				\n" + 
			"wilcox.test(	LINE_MCCON_N0               	,	LINE_MCCON_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0               	,	LINE_MCCON_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0               	,	LINE_MCCON_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0               	,	LINE_MCCON_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N0               	,	LINE_MCCON_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1               	,	LINE_MCCON_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1               	,	LINE_MCCON_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1               	,	LINE_MCCON_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1               	,	LINE_MCCON_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N1               	,	LINE_MCCON_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3               	,	LINE_MCCON_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3               	,	LINE_MCCON_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3               	,	LINE_MCCON_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3               	,	LINE_MCCON_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N3               	,	LINE_MCCON_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5               	,	LINE_MCCON_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5               	,	LINE_MCCON_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5               	,	LINE_MCCON_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5               	,	LINE_MCCON_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N5               	,	LINE_MCCON_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10              	,	LINE_MCCON_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10              	,	LINE_MCCON_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10              	,	LINE_MCCON_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10              	,	LINE_MCCON_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MCCON_N10              	,	LINE_MCCON_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_MINUS 				\n" + 
			"wilcox.test(	DUA_MINUS_N0              	,	DUA_MINUS_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0              	,	DUA_MINUS_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0              	,	DUA_MINUS_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0              	,	DUA_MINUS_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N0              	,	DUA_MINUS_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1              	,	DUA_MINUS_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1              	,	DUA_MINUS_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1              	,	DUA_MINUS_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1              	,	DUA_MINUS_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N1              	,	DUA_MINUS_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3              	,	DUA_MINUS_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3              	,	DUA_MINUS_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3              	,	DUA_MINUS_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3              	,	DUA_MINUS_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N3              	,	DUA_MINUS_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5              	,	DUA_MINUS_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5              	,	DUA_MINUS_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5              	,	DUA_MINUS_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5              	,	DUA_MINUS_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N5              	,	DUA_MINUS_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10             	,	DUA_MINUS_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10             	,	DUA_MINUS_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10             	,	DUA_MINUS_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10             	,	DUA_MINUS_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_MINUS_N10             	,	DUA_MINUS_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_MINUS				\n" + 
			"wilcox.test(	LINE_MINUS_N0               	,	LINE_MINUS_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0               	,	LINE_MINUS_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0               	,	LINE_MINUS_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0               	,	LINE_MINUS_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N0               	,	LINE_MINUS_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1               	,	LINE_MINUS_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1               	,	LINE_MINUS_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1               	,	LINE_MINUS_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1               	,	LINE_MINUS_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N1               	,	LINE_MINUS_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3               	,	LINE_MINUS_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3               	,	LINE_MINUS_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3               	,	LINE_MINUS_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3               	,	LINE_MINUS_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N3               	,	LINE_MINUS_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5               	,	LINE_MINUS_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5               	,	LINE_MINUS_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5               	,	LINE_MINUS_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5               	,	LINE_MINUS_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N5               	,	LINE_MINUS_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10              	,	LINE_MINUS_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10              	,	LINE_MINUS_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10              	,	LINE_MINUS_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10              	,	LINE_MINUS_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_MINUS_N10              	,	LINE_MINUS_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_OCHIAI 				\n" + 
			"wilcox.test(	DUA_OCHIAI_N0              	,	DUA_OCHIAI_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0              	,	DUA_OCHIAI_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0              	,	DUA_OCHIAI_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0              	,	DUA_OCHIAI_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N0              	,	DUA_OCHIAI_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1              	,	DUA_OCHIAI_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1              	,	DUA_OCHIAI_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1              	,	DUA_OCHIAI_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1              	,	DUA_OCHIAI_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N1              	,	DUA_OCHIAI_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3              	,	DUA_OCHIAI_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3              	,	DUA_OCHIAI_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3              	,	DUA_OCHIAI_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3              	,	DUA_OCHIAI_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N3              	,	DUA_OCHIAI_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5              	,	DUA_OCHIAI_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5              	,	DUA_OCHIAI_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5              	,	DUA_OCHIAI_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5              	,	DUA_OCHIAI_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N5              	,	DUA_OCHIAI_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10             	,	DUA_OCHIAI_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10             	,	DUA_OCHIAI_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10             	,	DUA_OCHIAI_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10             	,	DUA_OCHIAI_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OCHIAI_N10             	,	DUA_OCHIAI_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_OCHIAI				\n" + 
			"wilcox.test(	LINE_OCHIAI_N0               	,	LINE_OCHIAI_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0               	,	LINE_OCHIAI_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0               	,	LINE_OCHIAI_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0               	,	LINE_OCHIAI_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N0               	,	LINE_OCHIAI_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1               	,	LINE_OCHIAI_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1               	,	LINE_OCHIAI_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1               	,	LINE_OCHIAI_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1               	,	LINE_OCHIAI_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N1               	,	LINE_OCHIAI_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3               	,	LINE_OCHIAI_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3               	,	LINE_OCHIAI_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3               	,	LINE_OCHIAI_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3               	,	LINE_OCHIAI_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N3               	,	LINE_OCHIAI_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5               	,	LINE_OCHIAI_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5               	,	LINE_OCHIAI_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5               	,	LINE_OCHIAI_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5               	,	LINE_OCHIAI_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N5               	,	LINE_OCHIAI_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10              	,	LINE_OCHIAI_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10              	,	LINE_OCHIAI_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10              	,	LINE_OCHIAI_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10              	,	LINE_OCHIAI_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OCHIAI_N10              	,	LINE_OCHIAI_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_OP 				\n" + 
			"wilcox.test(	DUA_OP_N0              	,	DUA_OP_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0              	,	DUA_OP_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0              	,	DUA_OP_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0              	,	DUA_OP_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N0              	,	DUA_OP_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1              	,	DUA_OP_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1              	,	DUA_OP_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1              	,	DUA_OP_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1              	,	DUA_OP_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N1              	,	DUA_OP_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3              	,	DUA_OP_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3              	,	DUA_OP_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3              	,	DUA_OP_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3              	,	DUA_OP_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N3              	,	DUA_OP_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5              	,	DUA_OP_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5              	,	DUA_OP_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5              	,	DUA_OP_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5              	,	DUA_OP_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N5              	,	DUA_OP_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10             	,	DUA_OP_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10             	,	DUA_OP_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10             	,	DUA_OP_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10             	,	DUA_OP_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_OP_N10             	,	DUA_OP_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_OP				\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N0               	,	LINE_OP_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N1               	,	LINE_OP_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N3               	,	LINE_OP_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N5               	,	LINE_OP_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10              	,	LINE_OP_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10              	,	LINE_OP_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10              	,	LINE_OP_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10              	,	LINE_OP_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_OP_N10              	,	LINE_OP_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_TARANTULA 				\n" + 
			"wilcox.test(	DUA_TARANTULA_N0              	,	DUA_TARANTULA_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0              	,	DUA_TARANTULA_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0              	,	DUA_TARANTULA_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0              	,	DUA_TARANTULA_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N0              	,	DUA_TARANTULA_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1              	,	DUA_TARANTULA_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1              	,	DUA_TARANTULA_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1              	,	DUA_TARANTULA_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1              	,	DUA_TARANTULA_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N1              	,	DUA_TARANTULA_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3              	,	DUA_TARANTULA_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3              	,	DUA_TARANTULA_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3              	,	DUA_TARANTULA_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3              	,	DUA_TARANTULA_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N3              	,	DUA_TARANTULA_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5              	,	DUA_TARANTULA_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5              	,	DUA_TARANTULA_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5              	,	DUA_TARANTULA_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5              	,	DUA_TARANTULA_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N5              	,	DUA_TARANTULA_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10             	,	DUA_TARANTULA_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10             	,	DUA_TARANTULA_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10             	,	DUA_TARANTULA_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10             	,	DUA_TARANTULA_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_TARANTULA_N10             	,	DUA_TARANTULA_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_TARANTULA				\n" + 
			"wilcox.test(	LINE_TARANTULA_N0               	,	LINE_TARANTULA_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0               	,	LINE_TARANTULA_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0               	,	LINE_TARANTULA_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0               	,	LINE_TARANTULA_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N0               	,	LINE_TARANTULA_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1               	,	LINE_TARANTULA_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1               	,	LINE_TARANTULA_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1               	,	LINE_TARANTULA_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1               	,	LINE_TARANTULA_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N1               	,	LINE_TARANTULA_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3               	,	LINE_TARANTULA_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3               	,	LINE_TARANTULA_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3               	,	LINE_TARANTULA_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3               	,	LINE_TARANTULA_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N3               	,	LINE_TARANTULA_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5               	,	LINE_TARANTULA_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5               	,	LINE_TARANTULA_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5               	,	LINE_TARANTULA_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5               	,	LINE_TARANTULA_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N5               	,	LINE_TARANTULA_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10              	,	LINE_TARANTULA_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10              	,	LINE_TARANTULA_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10              	,	LINE_TARANTULA_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10              	,	LINE_TARANTULA_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_TARANTULA_N10              	,	LINE_TARANTULA_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_WONG3 				\n" + 
			"wilcox.test(	DUA_WONG3_N0              	,	DUA_WONG3_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0              	,	DUA_WONG3_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0              	,	DUA_WONG3_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0              	,	DUA_WONG3_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N0              	,	DUA_WONG3_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1              	,	DUA_WONG3_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1              	,	DUA_WONG3_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1              	,	DUA_WONG3_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1              	,	DUA_WONG3_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N1              	,	DUA_WONG3_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3              	,	DUA_WONG3_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3              	,	DUA_WONG3_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3              	,	DUA_WONG3_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3              	,	DUA_WONG3_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N3              	,	DUA_WONG3_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5              	,	DUA_WONG3_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5              	,	DUA_WONG3_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5              	,	DUA_WONG3_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5              	,	DUA_WONG3_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N5              	,	DUA_WONG3_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10             	,	DUA_WONG3_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10             	,	DUA_WONG3_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10             	,	DUA_WONG3_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10             	,	DUA_WONG3_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_WONG3_N10             	,	DUA_WONG3_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_WONG3				\n" + 
			"wilcox.test(	LINE_WONG3_N0               	,	LINE_WONG3_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0               	,	LINE_WONG3_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0               	,	LINE_WONG3_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0               	,	LINE_WONG3_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N0               	,	LINE_WONG3_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1               	,	LINE_WONG3_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1               	,	LINE_WONG3_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1               	,	LINE_WONG3_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1               	,	LINE_WONG3_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N1               	,	LINE_WONG3_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3               	,	LINE_WONG3_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3               	,	LINE_WONG3_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3               	,	LINE_WONG3_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3               	,	LINE_WONG3_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N3               	,	LINE_WONG3_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5               	,	LINE_WONG3_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5               	,	LINE_WONG3_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5               	,	LINE_WONG3_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5               	,	LINE_WONG3_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N5               	,	LINE_WONG3_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10              	,	LINE_WONG3_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10              	,	LINE_WONG3_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10              	,	LINE_WONG3_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10              	,	LINE_WONG3_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_WONG3_N10              	,	LINE_WONG3_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N DUA_ZOLTAR 				\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0              	,	DUA_ZOLTAR_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0              	,	DUA_ZOLTAR_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0              	,	DUA_ZOLTAR_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0              	,	DUA_ZOLTAR_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N0              	,	DUA_ZOLTAR_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1              	,	DUA_ZOLTAR_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1              	,	DUA_ZOLTAR_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1              	,	DUA_ZOLTAR_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1              	,	DUA_ZOLTAR_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N1              	,	DUA_ZOLTAR_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3              	,	DUA_ZOLTAR_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3              	,	DUA_ZOLTAR_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3              	,	DUA_ZOLTAR_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3              	,	DUA_ZOLTAR_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N3              	,	DUA_ZOLTAR_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5              	,	DUA_ZOLTAR_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5              	,	DUA_ZOLTAR_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5              	,	DUA_ZOLTAR_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5              	,	DUA_ZOLTAR_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N5              	,	DUA_ZOLTAR_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10             	,	DUA_ZOLTAR_N0              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10             	,	DUA_ZOLTAR_N1              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10             	,	DUA_ZOLTAR_N3              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10             	,	DUA_ZOLTAR_N5              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	DUA_ZOLTAR_N10             	,	DUA_ZOLTAR_N10             	,paired=TRUE,alternative=\"greater\")\n" + 
			"# N x N LINE_ZOLTAR				\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0               	,	LINE_ZOLTAR_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0               	,	LINE_ZOLTAR_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0               	,	LINE_ZOLTAR_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0               	,	LINE_ZOLTAR_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N0               	,	LINE_ZOLTAR_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1               	,	LINE_ZOLTAR_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1               	,	LINE_ZOLTAR_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1               	,	LINE_ZOLTAR_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1               	,	LINE_ZOLTAR_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N1               	,	LINE_ZOLTAR_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3               	,	LINE_ZOLTAR_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3               	,	LINE_ZOLTAR_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3               	,	LINE_ZOLTAR_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3               	,	LINE_ZOLTAR_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N3               	,	LINE_ZOLTAR_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5               	,	LINE_ZOLTAR_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5               	,	LINE_ZOLTAR_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5               	,	LINE_ZOLTAR_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5               	,	LINE_ZOLTAR_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N5               	,	LINE_ZOLTAR_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10              	,	LINE_ZOLTAR_N0               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10              	,	LINE_ZOLTAR_N1               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10              	,	LINE_ZOLTAR_N3               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10              	,	LINE_ZOLTAR_N5               	,paired=TRUE,alternative=\"greater\")\n" + 
			"wilcox.test(	LINE_ZOLTAR_N10              	,	LINE_ZOLTAR_N10              	,paired=TRUE,alternative=\"greater\")\n" + 
			"sink()";
		scriptBuffer.append(wilcoxonGiantString);
	}
	
}
