package br.usp.each.saeg.podium.core.model;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "report")
public class FaultLocalizationReport {

	Collection<FaultLocalizationEntry> entries;

	@XmlElement(name = "entry")
	public Collection<FaultLocalizationEntry> getEntries() {
		return entries;
	}

	public void setEntries(Collection<FaultLocalizationEntry> entries) {
		this.entries = entries;
	}

}