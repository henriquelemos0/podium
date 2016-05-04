package br.usp.each.saeg.podium.jaguar.input.model;

public class Project {
	
	final private String name;
	final private String faultyClass;
	final private Integer faultyLine;

	public Project(String name, String faultyClass, Integer faultyLine) {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((faultyClass == null) ? 0 : faultyClass.hashCode());
		result = prime * result + ((faultyLine == null) ? 0 : faultyLine.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof Project)) {
			return false;
		}
		Project other = (Project) obj;
		if (faultyClass == null) {
			if (other.faultyClass != null) {
				return false;
			}
		} else if (!faultyClass.equals(other.faultyClass)) {
			return false;
		}
		if (faultyLine == null) {
			if (other.faultyLine != null) {
				return false;
			}
		} else if (!faultyLine.equals(other.faultyLine)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	
}
