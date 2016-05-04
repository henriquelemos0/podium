package br.usp.each.saeg.podium.core.report;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import br.usp.each.saeg.jaguar.codeforest.model.DuaRequirement;
import br.usp.each.saeg.jaguar.codeforest.model.FaultClassification;
import br.usp.each.saeg.jaguar.codeforest.model.LineRequirement;
import br.usp.each.saeg.jaguar.codeforest.model.SuspiciousElement;
import br.usp.each.saeg.podium.core.model.FaultLocalizationEntry;
import br.usp.each.saeg.podium.core.model.FaultLocalizationReport;

public class Summarizer {

	private Collection<FaultLocalizationEntry> reportEntries = new ArrayList<FaultLocalizationEntry>();
	private final Map<String, FaultClassification> jaguarFiles;
	private final String faultyClassName;
	private final Integer faltyLineNumber;
	
	public Summarizer(Map<String, FaultClassification> jaguarFiles, String faultyClassName, Integer faltyLineNumber) {
		super();
		this.jaguarFiles = jaguarFiles;
		this.faultyClassName = faultyClassName;
		this.faltyLineNumber = faltyLineNumber;
	}

	/**
	 * Iterate throw each FileClassification file to find the faulty line rank. 
	 * 
	 * @return The {@link FaultLocalizationReport} containing one entry for each
	 * input FileClassification and the respective rank result.
	 * 
	 * @throws FileNotFoundException
	 */
	public FaultLocalizationReport rankResults() throws FileNotFoundException {
		for (String fileName : jaguarFiles.keySet()) {
			
			// Get and sort the elements from the file
			FaultClassification faultClassification = jaguarFiles.get(fileName);
			List<? extends SuspiciousElement> elements = faultClassification.getSuspiciousElementList();
			Collections.sort(elements);
			
			// Create the result entry using the input file properties
			FaultLocalizationEntry reportEntry = new FaultLocalizationEntry();
			reportEntry.setFileName(fileName);
			reportEntry.setCoverageType(faultClassification.getRequirementType().name());
			reportEntry.setHeuristic(faultClassification.getHeuristic());
			reportEntry.setTotalTime(faultClassification.getTimeSpent());
			reportEntry.setFaultSuspiciousValue(0D);
			
			// Iterate over each element, until the faulty line is found.
			boolean faultFound = false;
			for (SuspiciousElement suspiciousElement : elements) {
				
				if (!faultFound) {
				
					if (containTheFault(suspiciousElement)) {
					
						// The faulty line was found
						faultFound = true;
						reportEntry.setFaultSuspiciousValue(suspiciousElement.getSuspiciousValue());
					
					}

					// Add the elements lines to the list of lines to be searched until the faulty line is reached. 
					addLines(reportEntry, suspiciousElement);

				}else{
					
					if (suspiciousElement.getSuspiciousValue().equals(reportEntry.getFaultSuspiciousValue())) {
						
						// Still have to add the elements with the same suspicious value
						addLines(reportEntry, suspiciousElement);
					
					} else {
						
						// The rank is done for this file, all the following elements have a lower suspicious value than the fautly one.						
						break;
					
					}
					
				}
				
			}
			
			reportEntries.add(reportEntry);
		}
		FaultLocalizationReport finalReport = new FaultLocalizationReport();
		finalReport.setEntries(reportEntries);
		return finalReport;
	}

	/**
	 * Add the element lines to the report list of lines.
	 * If it is a Dua, it is added the def, use and target lines.
	 * If it is a Line, it is added just the line.
	 *   
	 * @param reportEntry the reportEntry to be added the lines
	 * @param suspiciousElement the element (dua or line)
	 */
	private void addLines(FaultLocalizationEntry reportEntry, SuspiciousElement suspiciousElement) {
		
		String className = suspiciousElement.getName();
		if (suspiciousElement instanceof DuaRequirement){
		
			DuaRequirement dua = (DuaRequirement) suspiciousElement;
			reportEntry.addLine(className + ":" + dua.getDef(), dua.getSuspiciousValue());
			reportEntry.addLine(className + ":" + dua.getUse(), dua.getSuspiciousValue());
			if (dua.getTarget() != -1){
				reportEntry.addLine(className + ":" + dua.getTarget(), dua.getSuspiciousValue());
			}
		
		} else if (suspiciousElement instanceof LineRequirement) {
			
			reportEntry.addLine(className + ":" + suspiciousElement.getLocation(), suspiciousElement.getSuspiciousValue());
		}
		
	}
	
	/**
	 * Check if the given suspiciousElement has the Fault.
	 * 
	 * First check if the className is the same as the faultyClassName.
	 * Then, check if one of the suspiciousElement lines is the faltyLineNumber.
	 * 
	 * For {@link LineRequirement} only the line location is used.
	 * For {@link DuaRequirement} the Def, Use and Target are used.
	 * 
	 * @param suspiciousElement the element that might have the faulty line, either {@link DuaRequirement} or {@link LineRequirement}
	 * 
	 * @return true if one of the elements line has the faultyLine, false otherwise.
	 */
	private boolean containTheFault(SuspiciousElement suspiciousElement) {
		
		if (suspiciousElement.getName().equals(faultyClassName)) {
		
			if (suspiciousElement instanceof DuaRequirement){
			
				DuaRequirement dua = (DuaRequirement) suspiciousElement;
				if (faltyLineNumber.equals(dua.getDef()))
					return true;
				if (faltyLineNumber.equals(dua.getUse()))
					return true;
				if (faltyLineNumber.equals(dua.getTarget()))
					return true;
			
			} else if (suspiciousElement instanceof LineRequirement) {
			
				if (faltyLineNumber.equals(suspiciousElement.getLocation()))
					return true;
				
			}
		}
		return false;
	}

}