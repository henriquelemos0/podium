package br.usp.each.saeg.podium.jaguar.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FaultLocalizationEntry")
@SuppressWarnings("restriction")
public class FaultLocalizationEntry implements Comparable<FaultLocalizationEntry>{

	private Long totalTime = 0L;
	private String heuristic;
	private String coverageType;
	private Double faultSuspiciousValue;
	private String fileName;
	private Map<String, Double> lineMap = new HashMap<String, Double>();
	private Integer maxCostLimit = Integer.MAX_VALUE;	
	private Integer maxCost = null;
	private Integer minCost = null;
	private Integer neighborhoodLimit = 0;
	private String projectName;
	
	/**
	 * @return the projectName
	 */
	@XmlAttribute
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

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
		if (minCost != null){
			return minCost;
		}else{
			minCost = 0;
			for (Double currentSuspiciousValue : lineMap.values()) {
				if (this.faultSuspiciousValue < currentSuspiciousValue){
					minCost++;
					
					if (minCost >= maxCostLimit){
						break;
					}
				}
			}
		}
		return minCost;
	}
	
	/**
	 * @param minCost the minCost to set
	 */
	public void setMinCost(Integer minCost) {
		this.minCost = minCost;
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
		if (maxCost != null){
			return maxCost;
		}else{
			maxCost = 0;
			for (Double currentSuspiciousValue : lineMap.values()) {
				if (this.faultSuspiciousValue <= currentSuspiciousValue){
					maxCost++;
					
					if (maxCost >= maxCostLimit){
						break;
					}
				}
			}
		}
		return maxCost;
	}
	
	/**
	 * @param maxCost the maxCost to set
	 */
	public void setMaxCost(Integer maxCost) {
		this.maxCost = maxCost;
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
	 * @return the maxCostLimit
	 */
	@XmlAttribute
	public Integer getMaxCostLimit() {
		return maxCostLimit;
	}

	/**
	 * Set maxCostLimit only if it is bigger than 0.
	 * Otherwise the value will still Integer.MAX_VALUE;
	 * 
	 * @param maxCostLimit the maxCostLimit to set
	 */
	public void setMaxCostLimit(Integer maxCostLimit) {
		if (maxCostLimit > 0){
			this.maxCostLimit = maxCostLimit;
		}
	}
	
	/**
	 * @return the neighborhoodLimit
	 */
	@XmlAttribute
	public Integer getNeighborhoodLimit() {
		return neighborhoodLimit;
	}

	/**
	 * @param neighborhoodLimit the neighborhoodLimit to set
	 */
	public void setNeighborhoodLimit(Integer neighborhoodLimit) {
		this.neighborhoodLimit = neighborhoodLimit;
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

	public boolean isLargeEnough() {
		return lineMap.size() >= maxCostLimit;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coverageType == null) ? 0 : coverageType.hashCode());
		result = prime * result + ((faultSuspiciousValue == null) ? 0 : faultSuspiciousValue.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((heuristic == null) ? 0 : heuristic.hashCode());
		result = prime * result + ((lineMap == null) ? 0 : lineMap.hashCode());
		result = prime * result + ((maxCost == null) ? 0 : maxCost.hashCode());
		result = prime * result + ((maxCostLimit == null) ? 0 : maxCostLimit.hashCode());
		result = prime * result + ((minCost == null) ? 0 : minCost.hashCode());
		result = prime * result + ((neighborhoodLimit == null) ? 0 : neighborhoodLimit.hashCode());
		result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
		result = prime * result + ((totalTime == null) ? 0 : totalTime.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FaultLocalizationEntry)) {
			return false;
		}
		FaultLocalizationEntry other = (FaultLocalizationEntry) obj;
		if (coverageType == null) {
			if (other.coverageType != null) {
				return false;
			}
		} else if (!coverageType.equals(other.coverageType)) {
			return false;
		}
		if (faultSuspiciousValue == null) {
			if (other.faultSuspiciousValue != null) {
				return false;
			}
		} else if (!faultSuspiciousValue.equals(other.faultSuspiciousValue)) {
			return false;
		}
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		} else if (!fileName.equals(other.fileName)) {
			return false;
		}
		if (heuristic == null) {
			if (other.heuristic != null) {
				return false;
			}
		} else if (!heuristic.equals(other.heuristic)) {
			return false;
		}
		if (lineMap == null) {
			if (other.lineMap != null) {
				return false;
			}
		} else if (!lineMap.equals(other.lineMap)) {
			return false;
		}
		if (maxCost == null) {
			if (other.maxCost != null) {
				return false;
			}
		} else if (!maxCost.equals(other.maxCost)) {
			return false;
		}
		if (maxCostLimit == null) {
			if (other.maxCostLimit != null) {
				return false;
			}
		} else if (!maxCostLimit.equals(other.maxCostLimit)) {
			return false;
		}
		if (minCost == null) {
			if (other.minCost != null) {
				return false;
			}
		} else if (!minCost.equals(other.minCost)) {
			return false;
		}
		if (neighborhoodLimit == null) {
			if (other.neighborhoodLimit != null) {
				return false;
			}
		} else if (!neighborhoodLimit.equals(other.neighborhoodLimit)) {
			return false;
		}
		if (projectName == null) {
			if (other.projectName != null) {
				return false;
			}
		} else if (!projectName.equals(other.projectName)) {
			return false;
		}
		if (totalTime == null) {
			if (other.totalTime != null) {
				return false;
			}
		} else if (!totalTime.equals(other.totalTime)) {
			return false;
		}
		return true;
	}

	public int compareTo(FaultLocalizationEntry other) {
		int i = coverageType.compareTo(other.coverageType);
	    if (i != 0) return i;
	    
	    i = projectName.compareTo(other.projectName);
	    if (i != 0) return i;
	    
		i = heuristic.compareTo(other.heuristic);
	    if (i != 0) return i;

	    return neighborhoodLimit.compareTo(other.neighborhoodLimit);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FaultLocalizationEntry [projectName=" + projectName + ", heuristic=" + heuristic + ", coverageType=" + coverageType
				+ ", neighborhoodLimit=" + neighborhoodLimit + ", fileName=" + fileName + ", lineMap=" + lineMap + ", maxCostLimit="
				+ maxCostLimit + ", maxCost=" + maxCost + ", minCost=" + minCost + ", faultSuspiciousValue=" + faultSuspiciousValue
				+ ", totalTime=" + totalTime + "]";
	}
	
	

}