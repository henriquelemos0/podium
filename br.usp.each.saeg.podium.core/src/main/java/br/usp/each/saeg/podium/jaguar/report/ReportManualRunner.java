package br.usp.each.saeg.podium.jaguar.report;

import java.io.FileNotFoundException;
import java.util.Collection;

import br.usp.each.saeg.podium.jaguar.input.ProjectFactory;
import br.usp.each.saeg.podium.jaguar.input.model.Project;

public class ReportManualRunner {

	public static void main(String[] args) throws FileNotFoundException{
			
		final String rootFolder = "C:\\Users\\unknown\\workspace\\luna\\runtime-New_configuration\\";
		String projectsCsvFile = "./src/main/resources/MathList.csv";
	
		ProjectFactory projectFactory = new ProjectFactory(projectsCsvFile);
		Collection<Project> projects = projectFactory.createProjects();
		
		for (Project project : projects) {
			String programFolder = project.getName();
			String faultyClass = project.getFaultyClass();
			Integer faultyLine = project.getFaultyLine();
			new Report().createReport(rootFolder, programFolder, faultyClass, faultyLine);			
		}
		
	}
	
}