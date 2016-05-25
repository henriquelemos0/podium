package br.usp.each.saeg.podium.jaguar.input;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import br.usp.each.saeg.podium.jaguar.input.model.Project;

public class ProjectFactoryTest {

	private static final String PROGRAMS_CVS_FILE = "./src/test/resources/projects/ProgramsList.csv";
	
	@Test
	public void createProjectsObjects() throws ClassNotFoundException{	
		ProjectFactory projectFacotry = new ProjectFactory(PROGRAMS_CVS_FILE);
		Collection<Project> projectsActual = projectFacotry.createProjects();
		
		Collection<Project> projectsExpected = new ArrayList<Project>();
		projectsExpected.add(new Project("math_1_buggy", "org.apache.commons.math3.fraction.BigFraction", 306));
		projectsExpected.add(new Project("math_1_buggy", "org.apache.commons.math3.fraction.Fraction", 215));
		projectsExpected.add(new Project("math_3_buggy", "org.apache.commons.math3.util.MathArrays", 823));
		projectsExpected.add(new Project("math_4_buggy", "org.apache.commons.math3.geometry.euclidean.threed.SubLine", 116));
		projectsExpected.add(new Project("math_4_buggy", "org.apache.commons.math3.geometry.euclidean.twod.SubLine", 120));
		projectsExpected.add(new Project("math_7_buggy", "org.apache.commons.math3.ode.AbstractIntegrator", 346));
		projectsExpected.add(new Project("math_7_buggy", "org.apache.commons.math3.ode.AbstractIntegrator", 357));
		projectsExpected.add(new Project("math_7_buggy", "org.apache.commons.math3.ode.AbstractIntegrator", 363));
		projectsExpected.add(new Project("math_7_buggy", "org.apache.commons.math3.ode.AbstractIntegrator", 370));
		projectsExpected.add(new Project("math_10_buggy", "org.apache.commons.math3.analysis.differentiation.DSCompiler", 1394));
		
		Assert.assertEquals(projectsExpected, projectsActual);
	}
	
}