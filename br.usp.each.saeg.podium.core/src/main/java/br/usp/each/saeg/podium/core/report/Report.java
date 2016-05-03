package br.usp.each.saeg.podium.core.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import br.usp.each.saeg.jaguar.codeforest.model.Class;
import br.usp.each.saeg.jaguar.codeforest.model.FaultClassification;
import br.usp.each.saeg.jaguar.codeforest.model.FlatFaultClassification;
import br.usp.each.saeg.jaguar.codeforest.model.HierarchicalFaultClassification;
import br.usp.each.saeg.jaguar.codeforest.model.Method;
import br.usp.each.saeg.jaguar.codeforest.model.Package;
import br.usp.each.saeg.jaguar.codeforest.model.Requirement;
import br.usp.each.saeg.jaguar.codeforest.model.SuspiciousElement;
import br.usp.each.saeg.jaguar.core.utils.FileUtils;
import br.usp.each.saeg.podium.core.model.FaultLocalizationReport;
import br.usp.each.saeg.podium.core.output.CsvFileWriter;

@SuppressWarnings("restriction")
public class Report {

	private static final String XML_EXTENSION = ".xml";
	private static final String CSV_EXTENSION = ".csv";
	private static final String DEFUALT_RESULT_FILE_NAME = "JaguarReport";
	private static final String JAGUAR_FOLDER = "\\.jaguar\\";
	private static final String DEFAULT_FOLDER = "." + JAGUAR_FOLDER;
	private static final String DEFUALT_RESULT_FILE_CSV = DEFUALT_RESULT_FILE_NAME + CSV_EXTENSION;
	private static final String DEFUALT_RESULT_FILE_XML = DEFUALT_RESULT_FILE_NAME + XML_EXTENSION;

	public void createReport(final File folder, final File reportFileXml, final File reportFileCsv, String className, Integer line) throws FileNotFoundException {
		Map<String, FaultClassification> jaguarFileList = getJaguarFiles(folder, reportFileXml);

		Summarizer summarizer = new Summarizer(jaguarFileList, className, line);
		FaultLocalizationReport faultLocalizationReport = summarizer.summarizePerformResults();

		createXmlFile(reportFileXml, faultLocalizationReport);
		createCsvFile("", reportFileCsv, faultLocalizationReport);
	}

	public void createReport(final String rootFolder, String programFolder, String className, Integer line) throws FileNotFoundException {
		File folder = new File(rootFolder + programFolder + JAGUAR_FOLDER);
		File reportFileXml = new File(getFilePath(rootFolder, programFolder, className, line) + XML_EXTENSION);
		File reportFileCsv = new File(getFilePath(rootFolder, programFolder, className, line) + CSV_EXTENSION);
		
		Map<String, FaultClassification> jaguarFileList = getJaguarFiles(folder, reportFileXml);

		Summarizer summarizer = new Summarizer(jaguarFileList, className, line);
		FaultLocalizationReport faultLocalizationReport = summarizer.summarizePerformResults();

		String classNameNoPackage = className.substring(className.lastIndexOf('.') + 1);
		String programDescName = programFolder + "-" + classNameNoPackage + "-" + line;

		createXmlFile(reportFileXml, faultLocalizationReport);
		createCsvFile(programDescName, reportFileCsv, faultLocalizationReport);
	}

	private String getFilePath(final String rootFolder, String programFolder, String className, Integer line) {
		String classNameNoPackage = className.substring(className.lastIndexOf('.') + 1);
		return rootFolder + programFolder + JAGUAR_FOLDER + DEFUALT_RESULT_FILE_NAME + "-" + programFolder + "-" + classNameNoPackage + "-" + line;
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
	 * @param reportFile
	 *            The current report output file.
	 * @return A list of FaultLocalization objects.
	 */
	private Map<String, FaultClassification> getJaguarFiles(final File folder, final File reportFile) {

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

	public static List<SuspiciousElement> extractElementsFromPackages(Collection<Package> packages) {
		List<SuspiciousElement> elements = new ArrayList<SuspiciousElement>();
		for (Package currentPackage : packages) {
			for (Class currentClass : currentPackage.getClasses()) {
				for (Method currentMethod : currentClass.getMethods()) {
					for (Requirement requirement : currentMethod.getRequirements()) {
						requirement.setName(currentClass.getName());
						elements.add(requirement);
					}
				}
			}
		}
		return elements;
	}

	/**
	 * First arg (arg[0]) is the falty class name (including package) and the
	 * second arg (arg[1]) is the falty line number.
	 * 
	 * If the first arg is not present, it is used the default = .\jaguar\ If
	 * the second arg is not present, it is used the default =
	 * {arg0}\JaguarReport.xml
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException {
		File folder = new File(DEFAULT_FOLDER);
		File resultFileXml = new File(folder.getAbsolutePath() + DEFUALT_RESULT_FILE_XML);
		File resultFileCsv = new File(folder.getAbsolutePath() + DEFUALT_RESULT_FILE_CSV);
		if (args.length < 2) {
			throw new IllegalArgumentException("Not enougth information, please add the Falty class name and line number.");
		}
		String className = args[0];
		Integer line = Integer.valueOf(args[1]);
		new Report().createReport(folder, resultFileXml, resultFileCsv, className, line);
	}	

}