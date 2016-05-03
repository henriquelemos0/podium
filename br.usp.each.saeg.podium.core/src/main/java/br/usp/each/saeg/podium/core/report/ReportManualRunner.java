package br.usp.each.saeg.podium.core.report;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

public class ReportManualRunner {

	public static void main(String[] args) throws FileNotFoundException{
		
		class Program {
			final private String name;
			final private String faultyClass;
			final private Integer faultyLine;
			public Program(String name, String faultyClass, Integer faultyLine) {
				super();
				this.name = name;
				this.faultyClass = faultyClass;
				this.faultyLine = faultyLine;
			}
			/**
			 * @return the name
			 */
			public String getName() {
				return name;
			}

			/**
			 * @return the faultyClass
			 */
			public String getFaultyClass() {
				return faultyClass;
			}

			/**
			 * @return the faultyLine
			 */
			public Integer getFaultyLine() {
				return faultyLine;
			}
		}
		
		/*
		 * Commons-math info
		 */
		final String rootFolder = "C:\\Users\\unknown\\Dropbox\\Henrique\\POS\\Results\\jaguar-results\\math\\";
		Collection<Program> programs = new ArrayList<Program>();
		programs.add(new Program("math_1_buggy", "org.apache.commons.math3.fraction.Fraction", 306));
		programs.add(new Program("math_1_buggy", "org.apache.commons.math3.fraction.BigFraction", 215));
		programs.add(new Program("math_3_buggy", "org.apache.commons.math3.util.MathArrays", 823));
		programs.add(new Program("math_4_buggy", "org.apache.commons.math3.geometry.euclidean.threed.SubLine", 116));
		programs.add(new Program("math_4_buggy", "org.apache.commons.math3.geometry.euclidean.twod.SubLine", 120));
		programs.add(new Program("math_7_buggy", "org.apache.commons.math3.ode.AbstractIntegrator", 346));
		programs.add(new Program("math_7_buggy", "org.apache.commons.math3.ode.AbstractIntegrator", 357));
		programs.add(new Program("math_7_buggy", "org.apache.commons.math3.ode.AbstractIntegrator", 363));
		programs.add(new Program("math_7_buggy", "org.apache.commons.math3.ode.AbstractIntegrator", 370));
		programs.add(new Program("math_10_buggy", "org.apache.commons.math3.analysis.differentiation.DSCompiler", 1394));
		
		/*
		 * JFreeChar info
		 */
//		final String rootFolder = "C:\\Users\\unknown\\Dropbox\\Henrique\\POS\\Results\\jaguar-results\\chart\\";
//		Collection<Program> programs = new ArrayList<Program>();
//		programs.add(new Program("chart_1_buggy", "org.jfree.chart.renderer.category.AbstractCategoryItemRenderer", 1797));
//		programs.add(new Program("chart_2_buggy", "org.jfree.data.general.DatasetUtilities", 755));
//		programs.add(new Program("chart_2_buggy", "org.jfree.data.general.DatasetUtilities", 1242));
//		programs.add(new Program("chart_3_buggy", "org.jfree.data.time.TimeSeries", 1058));
//		programs.add(new Program("chart_4_buggy", "org.jfree.chart.plot.XYPlot", 4493));
//		programs.add(new Program("chart_5_buggy", "org.jfree.data.xy.XYSeries", 544));
//		programs.add(new Program("chart_6_buggy", "org.jfree.chart.util.ShapeList", 105));
//		programs.add(new Program("chart_7_buggy", "org.jfree.data.time.TimePeriodValues", 300));
//		programs.add(new Program("chart_8_buggy", "org.jfree.data.time.Week", 175));
//		programs.add(new Program("chart_9_buggy", "org.jfree.data.time.TimeSeries", 944));
//		programs.add(new Program("chart_10_buggy", "org.jfree.chart.imagemap.DynamicDriveToolTipTagFragmentGenerator", 83));
//		programs.add(new Program("chart_10_buggy", "org.jfree.chart.imagemap.OverLIBToolTipTagFragmentGenerator", 59));
//		programs.add(new Program("chart_10_buggy", "org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator", 58));
//		programs.add(new Program("chart_11_buggy", "org.jfree.chart.util.ShapeUtilities", 275));
//		programs.add(new Program("chart_11_buggy", "org.jfree.chart.util.SerialUtilities", 326));
//		programs.add(new Program("chart_12_buggy", "org.jfree.chart.plot.MultiplePiePlot", 145));
//		programs.add(new Program("chart_13_buggy", "org.jfree.chart.block.BorderArrangement", 493));
//		programs.add(new Program("chart_14_buggy", "org.jfree.chart.plot.CategoryPlot", 2166));
//		programs.add(new Program("chart_14_buggy", "org.jfree.chart.plot.CategoryPlot", 2449));
//		programs.add(new Program("chart_14_buggy", "org.jfree.chart.plot.XYPlot", 2293));
//		programs.add(new Program("chart_14_buggy", "org.jfree.chart.plot.XYPlot", 2530));
//		programs.add(new Program("chart_15_buggy", "org.jfree.chart.plot.PiePlot", 1378));
//		programs.add(new Program("chart_15_buggy", "org.jfree.chart.plot.PiePlot", 2051));
//		programs.add(new Program("chart_16_buggy", "org.jfree.data.category.DefaultIntervalCategoryDataset", 207));
//		programs.add(new Program("chart_16_buggy", "org.jfree.data.category.DefaultIntervalCategoryDataset", 338));
//		programs.add(new Program("chart_17_buggy", "org.jfree.data.time.TimeSeries", 857));
//		programs.add(new Program("chart_18_buggy", "org.jfree.data.DefaultKeyedValues", 318));
//		programs.add(new Program("chart_18_buggy", "org.jfree.data.DefaultKeyedValues", 335));
//		programs.add(new Program("chart_18_buggy", "org.jfree.data.DefaultKeyedValues2D", 455));
//		programs.add(new Program("chart_18_buggy", "org.jfree.data.DefaultKeyedValues2D", 458));
//		programs.add(new Program("chart_19_buggy", "org.jfree.chart.plot.CategoryPlot", 698));
//		programs.add(new Program("chart_19_buggy", "org.jfree.chart.plot.CategoryPlot", 976));
//		programs.add(new Program("chart_20_buggy", "org.jfree.chart.plot.ValueMarker", 95));
//		programs.add(new Program("chart_21_buggy", "org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset", 152));
//		programs.add(new Program("chart_21_buggy", "org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset", 740));
//		programs.add(new Program("chart_22_buggy", "org.jfree.data.KeyedObjects2D", 231));
//		programs.add(new Program("chart_22_buggy", "org.jfree.data.KeyedObjects2D", 345));
//		programs.add(new Program("chart_22_buggy", "org.jfree.data.KeyedObjects2D", 375));
//		//programs.add(new Program("chart_23_buggy", "org.jfree.chart.renderer.category.MinMaxCategoryRenderer", 0));
//		programs.add(new Program("chart_24_buggy", "org.jfree.chart.renderer.GrayPaintScale", 126));
//		programs.add(new Program("chart_25_buggy", "org.jfree.chart.renderer.category.StatisticalBarRenderer", 258));
//		programs.add(new Program("chart_25_buggy", "org.jfree.chart.renderer.category.StatisticalBarRenderer", 315));
//		programs.add(new Program("chart_25_buggy", "org.jfree.chart.renderer.category.StatisticalBarRenderer", 402));
//		programs.add(new Program("chart_25_buggy", "org.jfree.chart.renderer.category.StatisticalBarRenderer", 459));
//		programs.add(new Program("chart_26_buggy", "org.jfree.chart.axis.Axis", 1192 ));
		
		/*
		 * JSoup info
		 */
//		final String rootFolder = "C:\\Users\\unknown\\Dropbox\\Henrique\\POS\\Results\\jaguar-results\\jsoup\\";
//		Collection<Program> programs = new ArrayList<Program>();		
//		programs.add(new Program("jsoup-1_3_4-1", "org.jsoup.nodes.Node", 175));
//		programs.add(new Program("jsoup-1_3_4-2", "org.jsoup.nodes.Entities", 66));
//		programs.add(new Program("jsoup-1_4_2-1", "org.jsoup.nodes.Entities", 76));
//		programs.add(new Program("jsoup-1_4_2-2", "org.jsoup.select.Selector", 139));
//		programs.add(new Program("jsoup-1_5_2-1", "org.jsoup.nodes.Element", 762));
//		programs.add(new Program("jsoup-1_5_2-2", "org.jsoup.nodes.Element", 960));
//		programs.add(new Program("jsoup-1_5_2-3", "org.jsoup.nodes.Element", 732));
//		programs.add(new Program("jsoup-1_5_2-4", "org.jsoup.parser.Tag", 309));
//		programs.add(new Program("jsoup-1_5_2-5", "org.jsoup.select.CombiningEvaluator$Or", 57));
//		programs.add(new Program("jsoup-1_5_3", "org.jsoup.nodes.Node", 73));
//		programs.add(new Program("jsoup-1_6_1-1", "org.jsoup.parser.CharacterReader", 19));
//		programs.add(new Program("jsoup-1_6_1-2", "org.jsoup.parser.TreeBuilderState", 1451));
//		programs.add(new Program("jsoup-1_6_1-2 - FaultyLine 250", "org.jsoup.parser.TreeBuilderState$7", 250));
//		programs.add(new Program("jsoup-1_6_1-2 - FaultyLine 907", "org.jsoup.parser.TreeBuilderState$10", 907));
//		programs.add(new Program("jsoup-1_6_1-2 - FaultyLine 1211", "org.jsoup.parser.TreeBuilderState$16", 1211));
//     	programs.add(new Program("jsoup-1_6_1-3", "org.jsoup.nodes.DocumentType", 35));
//		programs.add(new Program("jsoup-1_6_1-4", "org.jsoup.parser.CharacterReader", 32));
//		programs.add(new Program("jsoup-1_6_1-5", "org.jsoup.parser.TreeBuilderState$7", 283));
//		programs.add(new Program("jsoup-1_6_1-6", "org.jsoup.parser.CharacterReader", 99));
//		programs.add(new Program("jsoup-1_6_3-1", "org.jsoup.safety.Whitelist", 334));
//		programs.add(new Program("jsoup-1_6_3-2", "org.jsoup.parser.TokeniserState$27", 558));
//		programs.add(new Program("jsoup-1_6_3-3", "org.jsoup.parser.Tokeniser", 135));
//		programs.add(new Program("jsoup-1_6_4-1", "org.jsoup.helper.StringUtil", 113));
//		programs.add(new Program("jsoup-1_6_4-2", "org.jsoup.helper.DataUtil", 131));
//		programs.add(new Program("jsoup-1_7_2-1", "org.jsoup.parser.HtmlTreeBuilder", 148));
//		programs.add(new Program("jsoup-1_7_2-2", "org.jsoup.nodes.Element", 1138));
//		programs.add(new Program("jsoup-1_7_3-1", "org.jsoup.parser.HtmlTreeBuilder", 163));
//		programs.add(new Program("jsoup-1_7_3-2", "org.jsoup.parser.HtmlTreeBuilderState$7", 725));
//		programs.add(new Program("jsoup-1_7_3-3", "org.jsoup.parser.CharacterReader", 89));
//		programs.add(new Program("jsoup-1_7_4-1", "org.jsoup.safety.Cleaner$CleaningVisitor", 105));
//		programs.add(new Program("jsoup-1_7_4-2", "org.jsoup.helper.DataUtil", 121));
//		programs.add(new Program("jsoup-1_7_4-3", "org.jsoup.parser.HtmlTreeBuilderState$7", 456));
//		programs.add(new Program("jsoup-1_7_4-4", "org.jsoup.nodes.Element", 1101));
//		programs.add(new Program("jsoup-1_8_2-1", "org.jsoup.nodes.FormElement", 99));
//		programs.add(new Program("jsoup-1_8_2-2", "org.jsoup.nodes.Element", 574));
//		programs.add(new Program("jsoup-1_8_3-1", "org.jsoup.nodes.Entities", 118));
//		programs.add(new Program("jsoup-1_8_3-2", "org.jsoup.nodes.Entities", 118));
//		programs.add(new Program("jsoup-1_8_3-2 - FaultyLine 122", "org.jsoup.nodes.Entities", 122));		
//		programs.add(new Program("jsoup-1_8_3-3", "org.jsoup.parser.HtmlTreeBuilder", 394));
//		programs.add(new Program("jsoup-1_8_4-1", "org.jsoup.select.StructuralEvaluator$Parent", 61));
//		programs.add(new Program("jsoup-1_8_4-2", "org.jsoup.select.Selector", 132));
//		programs.add(new Program("jsoup-1_8_4-3", "org.jsoup.helper.HttpConnection$Response", 773));
		
		for (Program program : programs) {
			String programFolder = program.getName();
			String faultyClass = program.getFaultyClass();
			Integer faultyLine = program.getFaultyLine();
			new Report().createReport(rootFolder, programFolder, faultyClass, faultyLine);			
		}
		
	}
	
}
