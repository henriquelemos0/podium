package br.usp.each.saeg.podium.jaguar.report;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.usp.each.saeg.podium.jaguar.model.FaultLocalizationEntry;

public class RScriptTest {

	@Test
	public void getScriptObjects() throws ClassNotFoundException {
		File folder = new File("./src/test/resources/scripts/ant");
		RScript rScript = new RScript();
		List<FaultLocalizationEntry> scriptObjectList = rScript.getJaguarFiles(folder);

		Assert.assertEquals(100, scriptObjectList.size());
	}
	
	@Test
	public void scriptObjectsOrder() throws ClassNotFoundException {
		File folder = new File("./src/test/resources/scripts/ant");
		RScript rScript = new RScript();
		List<FaultLocalizationEntry> scriptObjectList = rScript.getJaguarFiles(folder);

		System.out.println(scriptObjectList);
		Assert.assertEquals(100, scriptObjectList.size());
		
		FaultLocalizationEntry obj0 = scriptObjectList.get(0);
		Assert.assertEquals("DUA", obj0.getCoverageType());
		Assert.assertEquals("DRT", obj0.getHeuristic());
		Assert.assertEquals(new Integer(0), obj0.getNeighborhoodLimit());
		
		FaultLocalizationEntry obj4 = scriptObjectList.get(4);
		Assert.assertEquals("DUA", obj4.getCoverageType());
		Assert.assertEquals("DRT", obj4.getHeuristic());
		Assert.assertEquals(new Integer(10), obj4.getNeighborhoodLimit());
		
		FaultLocalizationEntry obj50 = scriptObjectList.get(50);
		Assert.assertEquals("LINE", obj50.getCoverageType());
		Assert.assertEquals("DRT", obj50.getHeuristic());
		Assert.assertEquals(new Integer(0), obj50.getNeighborhoodLimit());
		
		FaultLocalizationEntry obj99 = scriptObjectList.get(99);
		Assert.assertEquals("LINE", obj99.getCoverageType());
		Assert.assertEquals("ZOLTAR", obj99.getHeuristic());
		Assert.assertEquals(new Integer(10), obj99.getNeighborhoodLimit());
	}

}