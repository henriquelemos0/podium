package br.usp.each.saeg.podium.jaguar.input.model;

public class Program {
	
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
