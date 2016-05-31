package br.usp.each.saeg.podium.jaguar.report;

import java.io.File;
import java.util.Map;

import org.junit.Assert;

import org.junit.Test;

import br.usp.each.saeg.jaguar.codeforest.model.FaultClassification;
import br.usp.each.saeg.podium.jaguar.model.FaultLocalizationEntry;
import br.usp.each.saeg.podium.jaguar.model.FaultLocalizationReport;

public class SummarizerTest {

	@Test
	public void CF_farAway() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_4_PH_HD_1_DF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_4_PH_HD_1_DF", "org.apache.tools.ant.ProjectHelper$TargetHandler", 436, 0, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(63), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectNotFound_line_CF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_CF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.CommandlineJava", 176, 0, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(100), entry.getMaxCost());
			Assert.assertEquals(new Integer(77), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_0n_1p_CF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_CF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline$Argument", 108, 0, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(8), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_1n_1p_CF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_CF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline$Argument", 108, 1, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(18), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_3n_1p_CF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_CF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline$Argument", 108, 3, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(35), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_5n_1p_CF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_CF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline$Argument", 108, 5, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(50), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_10n_1p_CF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_CF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline$Argument", 108, 10, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(80), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectNotFound_line_DF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_DF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.CommandlineJava", 176, 0, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(100), entry.getMaxCost());
			Assert.assertEquals(new Integer(9), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_0n_1p_DF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_DF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline", 208, 0, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(2), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_1n_1p_DF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_DF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline", 208, 1, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(4), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_3n_1p_DF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_DF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline", 208, 3, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(8), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_5n_1p_DF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_DF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline", 208, 5, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(12), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
	
	@Test
	public void defectFound_10n_1p_DF() throws ClassNotFoundException{
		File folder = new File("./src/test/resources/report/ant-1_3_b3_CLJ_DH_1_DF");
		Map<String, FaultClassification> jaguarFileList = Report.getJaguarFiles(folder);

		Summarizer summarizer = new Summarizer(jaguarFileList, "ant-1_3_b3_CLJ_DH_1_CF", "org.apache.tools.ant.types.Commandline", 208, 10, 100);
		FaultLocalizationReport faultLocalizationReport = summarizer.rankResults();
		for (FaultLocalizationEntry entry : faultLocalizationReport.getEntries()) {
			Assert.assertEquals(new Integer(22), entry.getMaxCost());
			Assert.assertEquals(new Integer(1), entry.getMinCost());
		}
	}
}