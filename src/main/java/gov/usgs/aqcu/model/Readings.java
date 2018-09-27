/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.aqcu.model;

import java.util.List;
import java.time.Instant;

/** 
 * Summarized AQCU version of a readings report.
 *
 * @author kmschoep
 */
public class Readings {
	private final String fieldVisitIdentifier;
	private final String visitStatus;
	private final Instant time;
	private final Instant visitTime;
	private final String party;
	private final String monitoringMethod;
	private final String value;
	private final String parameter;
	private final String uncertainty;
	private final List<String> comments;
	private final String type;
	private final String sublocation;

	public Readings(String fieldVisitIdentifier, String visitStatus, String party, String type, String sublocation, String parameter, String monitoringMethod, Instant time, String value,
			String uncertainty, Instant visitTime, List<String> comments){
		this.fieldVisitIdentifier = fieldVisitIdentifier;
		this.visitStatus = visitStatus;
		this.party = party;
		this.monitoringMethod = monitoringMethod;
		this.sublocation = sublocation;
		this.parameter = parameter;
		this.value = value;
		this.uncertainty = uncertainty;
		this.time = time;
		this.visitTime = visitTime;
		this.comments = comments;
		this.type = type;
	}
	
	/**
	 *
	 * @return The visit time
	 */
	public Instant getVisitTime() {
		return visitTime;
	}

	/**
	 *
	 * @return The visit status
	 */
	public String getVisitStatus() {
		return visitStatus;
	}

	/**
	 *
	 * @return The time the readings were collected
	 */
	public Instant getTime() {
		return time;
	}

	/**
	 *
	 * @return The associated party information describing the participants in these readings
	 */
	public String getParty() {
		return party;
	}

	/**
	 *
	 * @return The monitoring method
	 */
	public String getMonitoringMethod() {
		return monitoringMethod;
	}

	/**
	 *
	 * @return The value of the readings
	 */
	public String getValue() {
		return value;
	}

	/**
	 *
	 * @return The uncertainty value associated with the readings
	 */
	public String getUncertainty() {
		return uncertainty;
	}

	/**
	 *
	 * @return The comments associated with the readings
	 */
	public List<String> getComments() {
		return comments;
	}

	/**
	 *
	 * @return The type of readings
	 */
	public String getType() {
		return type;
	}

	/**
	 *
	 * @return The parameter being measured
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 *
	 * @return The sub-location
	 */
	public String getSublocation() {
		return sublocation;
	}


	/**
	 *
	 * @return The associated field visit unique identifier
	 */
	public String getFieldVisitIdentifier() {
		return fieldVisitIdentifier;
	}
	
}
