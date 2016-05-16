package br.usp.each.saeg.podium.jaguar.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.usp.each.saeg.podium.jaguar.input.model.Project;

public class ProjectFactory {

	private final String projectsCvsFile;
	
	/**
	 * Initialize the ProjectFactory.
	 * 
	 * @param projectsCsvFile The Project CSV file path
	 */
	public ProjectFactory(String projectsCsvFile) {
		this.projectsCvsFile = projectsCsvFile;
	}
	
	/**
	 * Read the Project CSV file to extract the information of each project.
	 * Then, creates one object {@link Project} for each file line.
	 * 
	 * @return A list of {@link Project}
	 */
	public Collection<Project> createProjects() {
		
		List<String[]> projectList = CsvFileReader.readCsvFile(projectsCvsFile);
		Collection<Project> projects = new ArrayList<Project>();
		
		for (String[] params : projectList) {
			String projectName = StringUtils.trim(params[0]);
			String faultyClass = StringUtils.trim(params[1]);
			Integer faultLine = new Integer(StringUtils.trim(params[2]));
			Project project = new Project(projectName, faultyClass, faultLine);
			projects.add(project);
		}
		
		return projects;
	}

}
