package br.usp.each.saeg.podium.core.input;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CsvFileReaderTest {

	private static final String PROGRAMS_CVS_FILE = "./src/test/resources/ProgramsList.csv";
	
	@SuppressWarnings("deprecation")
	@Test
	public void readCvs() throws ClassNotFoundException{
		List<String[]> programsList = CsvFileReader.readCsvFile(PROGRAMS_CVS_FILE);
		
		Assert.assertTrue(new Integer("10").equals(programsList.size()));
		
		String[] program1 = {"math_1_buggy","org.apache.commons.math3.fraction.BigFraction","306"};
		Assert.assertEquals(program1, programsList.get(0));
		
		String[] program2 = {"math_1_buggy","org.apache.commons.math3.fraction.Fraction","215"};
		Assert.assertEquals(program2, programsList.get(1));
		
		String[] program3 = {"math_3_buggy","org.apache.commons.math3.util.MathArrays","823"};
		Assert.assertEquals(program3, programsList.get(2));
		
		String[] program4 = {"math_4_buggy","org.apache.commons.math3.geometry.euclidean.threed.SubLine","116"};
		Assert.assertEquals(program4, programsList.get(3));
		
		String[] program5 = {"math_4_buggy","org.apache.commons.math3.geometry.euclidean.twod.SubLine","120"};
		Assert.assertEquals(program5, programsList.get(4));
		
		String[] program6 = {"math_7_buggy","org.apache.commons.math3.ode.AbstractIntegrator","346"};
		Assert.assertEquals(program6, programsList.get(5));
		
		String[] program7 = {"math_7_buggy","org.apache.commons.math3.ode.AbstractIntegrator","357"};
		Assert.assertEquals(program7, programsList.get(6));
		
		String[] program8 = {"math_7_buggy","org.apache.commons.math3.ode.AbstractIntegrator","363"};
		Assert.assertEquals(program8, programsList.get(7));
		
		String[] program9 = {"math_7_buggy","org.apache.commons.math3.ode.AbstractIntegrator","370"};
		Assert.assertEquals(program9, programsList.get(8));
		
		String[] program10 = {"math_10_buggy","org.apache.commons.math3.analysis.differentiation.DSCompiler","1394"};
		Assert.assertEquals(program10, programsList.get(9));
	}
	
}