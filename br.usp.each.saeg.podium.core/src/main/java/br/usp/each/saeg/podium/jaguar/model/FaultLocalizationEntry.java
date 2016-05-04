package br.usp.each.saeg.podium.jaguar.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FaultLocalizationEntry")
@SuppressWarnings("restriction")
public class FaultLocalizationEntry {

	private Long totalTime = 0L;
	private String heuristic;
	private String coverageType;
	private Double faultSuspiciousValue;
	private String fileName;
	private Map<String, Double> lineMap = new HashMap<String, Double>();
	
	/**
	 * @return the faultSuspiciousValue
	 */
	@XmlAttribute
	public Double getFaultSuspiciousValue() {
		return faultSuspiciousValue;
	}
	
	/**
	 * @param faultSuspiciousValue the faultSuspiciousValue to set
	 */
	public void setFaultSuspiciousValue(Double faultSuspiciousValue) {
		this.faultSuspiciousValue = faultSuspiciousValue;
	}
	
	/**
	 * The total time spent to calculate the fault localization rank
	 * 
	 * @return time in milliseconds
	 */
	@XmlAttribute
	public Long getTotalTimeInMs() {
		return totalTime;
	}

	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}
	
	/**
	 * The total time spent to calculate the fault localization rank
	 * 
	 * @return time in milliseconds
	 */
	@XmlAttribute
	public String getReadableTime() {
		return String.format("%02d min, %02d sec", 
			    TimeUnit.MILLISECONDS.toMinutes(totalTime),
			    TimeUnit.MILLISECONDS.toSeconds(totalTime) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime))
			);
	}

	/**
	 * Cost to find the fault. It is the number of lines of code needed to be
	 * inspected to find the fault; All lines with higher suspicious value, plus
	 * the fault line.
	 * 
	 * @return total number of lines needed to be inspected to find the fault;
	 */
	@XmlAttribute
	public Integer getMinCost() {
		Integer minCost = 1;
		for (Double currentSuspiciousValue : lineMap.values()) {
			if (this.faultSuspiciousValue < currentSuspiciousValue){
				minCost++;
			}
		}
		return minCost;
	}

	/**
	 * Cost to find the fault. It is the number of lines of code needed to be
	 * inspected to find the fault; All lines with higher and same suspicious
	 * value.
	 * 
	 * @return total number of lines needed to be inspected to find the fault;
	 */
	@XmlAttribute
	public Integer getMaxCost() {
		Integer maxCost = 0;
		for (Double currentSuspiciousValue : lineMap.values()) {
			if (this.faultSuspiciousValue <= currentSuspiciousValue){
				maxCost++;
			}
		}
		return maxCost;
	}

	/**
	 * The heuristic used to calculate the element suspicious value
	 * 
	 * @return the heuristic
	 */
	@XmlAttribute
	public String getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(String heuristic) {
		this.heuristic = heuristic;
	}

	/**
	 * The type of element coverage.
	 * 
	 * @return Dua or Line
	 */
	@XmlAttribute
	public String getCoverageType() {
		return coverageType;
	}

	public void setCoverageType(String coverageType) {
		this.coverageType = coverageType;
	}
	
	/**
	 * @return the fileName
	 */
	@XmlAttribute
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * If the line did not exist before, add it to the global map.
	 * If the suspiciousValue is higher, updated it.
	 */
	public void addLine(String lineDesc, Double newSuspiciousValue) {
		if (lineMap.containsKey(lineDesc)){
			Double oldSuspiciousValue = lineMap.get(lineDesc);
			if (newSuspiciousValue < oldSuspiciousValue){
				return;
			}
		}
		
		lineMap.put(lineDesc, newSuspiciousValue);
	}

}