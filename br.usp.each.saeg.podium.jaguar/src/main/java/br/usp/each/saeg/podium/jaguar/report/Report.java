package br.usp.each.saeg.podium.jaguar.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import br.usp.each.saeg.jaguar.codeforest.model.FaultClassification;
import br.usp.each.saeg.jaguar.codeforest.model.FlatFaultClassification;
import br.usp.each.saeg.jaguar.codeforest.model.HierarchicalFaultClassification;
import br.usp.each.saeg.jaguar.core.utils.FileUtils;
import br.usp.each.saeg.podium.jaguar.model.FaultLocalizationReport;
import br.usp.each.saeg.podium.jaguar.output.CsvFileWriter;

@SuppressWarnings("restriction")
public class Report {

	private static final String XML_EXTENSION = ".xml";
	private static final String CSV_EXTENSION = ".csv";
	private static final String DEFUALT_RESULT_FILE_NAME = "JaguarReport";
	private static final String JAGUAR_FOLDER = "\\.jaguar\\";
	private static final String PODIUM_FOLDER = "\\.podium\\";
	
	private final String dateTime = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date (System.currentTimeMillis()));

	public void createReport(final String rootFolder, String programFolder, String className, Integer line, Integer neighborhoodLimit, Integer maxCostLimit) throws FileNotFoundException {
		
		//Get jaguar xml files list
		File folder = new File(rootFolder + programFolder + JAGUAR_FOLDER);
		Map<String, FaultClassification> jaguarFileList = getJaguarFiles(folder);

		//Calulate the rank
		Summarizer summarizer = new Summarizer(jaguarFileList, programFolder, className, line, neighborhoodLimit, maxCostLimit);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();

		//Write output files (XML and CVS, at projectName/.jaguar and at ../.podium
		File reportFileCsv = new File(getJaguarFilePath(rootFolder, programFolder, className, line, neighborhoodLimit) + CSV_EXTENSION);
		File reportFileCsvCopy = new File(getPodiumFilePath(rootFolder, programFolder, className, line, neighborhoodLimit) + CSV_EXTENSION);

		File reportFileXml = new File(getJaguarFilePath(rootFolder, programFolder, className, line, neighborhoodLimit) + XML_EXTENSION);
		File reportFileXmlCopy = new File(getPodiumFilePath(rootFolder, programFolder, className, line, neighborhoodLimit) + XML_EXTENSION);
		
		String classNameNoPackage = className.substring(className.lastIndexOf('.') + 1);
		String programDescName = programFolder + "-" + classNameNoPackage + "-" + line;

		createCsvFile(programDescName, reportFileCsv, faultLocalizationReport);
		createCsvFile(programDescName, reportFileCsvCopy, faultLocalizationReport);
		createXmlFile(reportFileXml, faultLocalizationReport);
		createXmlFile(reportFileXmlCopy, faultLocalizationReport);
	}

	private String getJaguarFilePath(final String rootFolder, String programFolder, String className, Integer line, Integer neighborhoodLimit) {
		String classNameNoPackage = className.substring(className.lastIndexOf('.') + 1);
		return rootFolder + programFolder + JAGUAR_FOLDER + DEFUALT_RESULT_FILE_NAME + "-" + programFolder + "-" + classNameNoPackage + "-" + line + "-n" + neighborhoodLimit;
	}
	
	private String getPodiumFilePath(final String rootFolder, String programFolder, String className, Integer line, Integer neighborhoodLimit) {
		String classNameNoPackage = className.substring(className.lastIndexOf('.') + 1);
		return rootFolder + PODIUM_FOLDER + "\\" + dateTime + "\\" + DEFUALT_RESULT_FILE_NAME + "-" + programFolder + "-" + classNameNoPackage + "-" + line + "-n" + neighborhoodLimit;
	}

	private void createXmlFile(final File reportFile, FaultLocalizationReport faultLocalizationReport) {
		JAXB.marshal(faultLocalizationReport, reportFile);
	}
	
	private void createCsvFile(String programFolder, final File reportFile, FaultLocalizationReport faultLocalizationReport) {
		CsvFileWriter.writeCsvFile(programFolder, faultLocalizationReport, reportFile);
	}

	/**
	 * Extract the FaultClassification object of each Xml file inside the given
	 * folder. Except for fault.xml and the file with the report output name.
	 * 
	 * @param folder
	 *            The fodler to be searched.
	 * @return A list of FaultLocalization objects.
	 */
	public static Map<String, FaultClassification> getJaguarFiles(final File folder) {

		List<File> resultFiles = FileUtils.findFilesEndingWith(folder, new String[] { XML_EXTENSION });
		Map<String, FaultClassification> jaguarFileMap = new HashMap<String, FaultClassification>();

		for (File file : resultFiles) {
			if ("JaguarReport.xml".equals(file.getName())){
				continue;
			}
			
			try{
		    
				javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(
		        		FlatFaultClassification.class,
		        		HierarchicalFaultClassification.class
		        );
		        
				javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
				jaguarFileMap.put(file.getName(), (FaultClassification) unmarshaller.unmarshal(file));
			
			}catch(DataBindingException e){				
				continue;
			} catch (JAXBException e) {
				continue;
			}
		}

		return jaguarFileMap;
	}

}