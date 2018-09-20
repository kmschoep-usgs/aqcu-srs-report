package gov.usgs.aqcu.builder;

import java.util.Map;
import java.util.List;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Inspection;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ReadingType;

import gov.usgs.aqcu.model.Readings;
import gov.usgs.aqcu.parameter.ReportRequestParameters;
import org.springframework.beans.factory.annotation.Value;

@Service
public class ReadingsBuilderService {
	private static final Logger LOG = LoggerFactory.getLogger(ReadingsBuilderService.class);
	
	private static final String EXCLUDED_READING_TYPES = "ExtremeMax,ExtremeMin";
	private static final String MON_METH_CREST_STAGE = "Crest stage";
	private static final String MON_METH_MAX_MIN_INDICATOR = "Max-min indicator";
	
	private enum EmptyCsgReadings {
		NOMK("No mark"),
		NTRD("Not read"),
		OTOP("Over topped");
		
		private String readingDisplay;
		
		EmptyCsgReadings(String readingDisplay) {
			this.readingDisplay = readingDisplay;
		}
		
		public String getDisplay() {
			return readingDisplay;
		}
	}

	/**
	 * Returns a list of all readings from a field visit.
	 * 
	 * @param visit The field visit to get readings from
	 * @param fieldVisitResponse The Aquarius FieldVisit service response
	 * @param allowedTypes The allowed types of readings to collect
	 * @return The list of extracted readings
	 */
	public List<Readings> getAqcuFieldVisitsReadings(FieldVisitDescription visit, FieldVisitDataServiceResponse fieldVisitResponse, List<String> allowedTypes, String parameter){
		List<Readings> result = new ArrayList<>();

		InspectionActivity activity = fieldVisitResponse.getInspectionActivity();
		
		if(activity != null){
			List<Reading> readings = activity.getReadings();
			List<Inspection> inspections = activity.getInspections();
			String visitStatus = "TODO"; //TODO see AQCU-265, this is currently being left out of the rendered report until Aquarius adds the information

			Map<String,List<String>> inspectionCommentsBySerial = serialNumberToComment(inspections, allowedTypes);

			for(Reading read : readings){
				List<String> comments = new ArrayList<>();
				
				//comments attached to the inspection activity, linked by the reading's serial number 
				List<String> inspectionComments = inspectionCommentsBySerial.get(read.getSerialNumber());
				if(inspectionComments != null) {
					comments.addAll(inspectionComments);
				}
				
				//comments already attached to the reading;
				String readingComments = read.getComments();
				if(readingComments != null){
					comments.add(readingComments);
				}
				
				Temporal fieldVisitTemporal = visit.getStartTime();
				Temporal readingTime = read.getTime(); 
				
				Readings readingReport = new Readings(visit.getIdentifier(), visitStatus, activity.getParty(), 
						read.getReadingType().toString(), read.getSubLocationIdentifier(), read.getParameter(), read.getMonitoringMethod(),
						readingTime, read.getValue().getDisplay(), read.getUncertainty().getDisplay(), fieldVisitTemporal, 
						comments);
				result.add(readingReport);
			}
			
			//add possible "no read" inspections
			result.addAll(extractEmptyCrestStageReadings(visit, inspections, activity));
			result.addAll(extractEmptyMaxMinIndicatorReadings(visit, inspections, activity));
			result.addAll(extractEmptyHighWaterMarkReadings(visit, inspections, activity));
			result.removeAll(excludedTypes(result));
		}			
		
		return result;
	}
	/**
	 * Returns comments stored in inspections linked by serial number. These comments are known as 
	 * "reference reading comments".
	 * 
	 * @param inspections The list of Aquarius Inspections to extract serial numbers from
	 * @return The extracted serial numbers and their associated comments
	 */
	private Map<String, List<String>> serialNumberToComment(List<Inspection> inspections) {
		Map<String, List<String>> toRet = new HashMap<>();

		for(Inspection inspection: inspections){
			if(StringUtils.isNotBlank(inspection.getComments())) {
				List<String> previous = toRet.get(inspection.getSerialNumber());
				if(previous == null || previous.isEmpty()){
					previous = new ArrayList<>();
					toRet.put(inspection.getSerialNumber(), previous);
				}
				previous.add(inspection.getComments());
			}
		}

		return toRet;
	}
	
	private Map<String, List<String>> serialNumberToComment(List<Inspection> inspections, List<String> allowedTypes){
		if(allowedTypes.isEmpty()){
			return serialNumberToComment(inspections);
		} 
		else {
			List<Inspection> filteredInspections = new ArrayList<>();
			//Considering making this a function to abstract it away. Currently kind of ugly.
			for(Inspection inspection : inspections){
				if(allowedTypes.contains(inspection.getInspectionType().toString())){
					filteredInspections.add(inspection);
				}
			}
			return serialNumberToComment(filteredInspections);
		}
	}
	
	private List<Readings> excludedTypes(List<Readings> inReadings){
		List<Readings> outReadings = new ArrayList<>();
		for (Readings reading: inReadings) {
			if (EXCLUDED_READING_TYPES.contains(reading.getType())) {
				outReadings.add(reading);
			}
		}
		return outReadings;
	}
	
	public List<Readings> selectedParameter(String selectedParameter, List<Readings> inReadings){
		List<Readings> outReadings = new ArrayList<>();
		for (Readings reading: inReadings) {
			if (reading.getParameter().equals(selectedParameter)) {
				outReadings.add(reading);
			}
		}
		return outReadings;
	}
	
	private List<Readings> extractEmptyCrestStageReadings(FieldVisitDescription visit, List<Inspection> inspections, InspectionActivity activity) {
		List<Readings> readings = new ArrayList<>();
		for(Inspection ins : inspections) {
			if (ins.getInspectionType().equals(InspectionType.CrestStageGage)) {
				for(EmptyCsgReadings csg: EmptyCsgReadings.values()) {
					if(ins.getComments().contains(csg.name()) || ins.getComments().contains(csg.readingDisplay)) {
						readings.add(new Readings(visit.getIdentifier(), "TODO", activity.getParty(), 
								ReadingType.ExtremeMax.toString(), ins.getSubLocationIdentifier(), null, MON_METH_CREST_STAGE,
								null, csg.getDisplay(), 
								null, visit.getStartTime(), Arrays.asList(new String [] { ins.getComments() }))
								);						
					}
				}
			}
		}
		return readings;
	}
	
	private List<Readings> extractEmptyMaxMinIndicatorReadings(FieldVisitDescription visit, List<Inspection> inspections, InspectionActivity activity) {
		List<Readings> readings = new ArrayList<>();
		for(Inspection ins : inspections) {
			if (ins.getInspectionType().equals(InspectionType.MaximumMinimumGage)) {
				if(StringUtils.isNotBlank(ins.getComments())){	
					readings.add(new Readings(visit.getIdentifier(), "TODO", activity.getParty(), 
							ReadingType.ExtremeMax.toString(), ins.getSubLocationIdentifier(), null, MON_METH_MAX_MIN_INDICATOR,
							null, "", 
							null, visit.getStartTime(), Arrays.asList(new String[] { ins.getComments() }))
							);
				}
			} 
		}
		return readings;
	}
	
	private List<Readings> extractEmptyHighWaterMarkReadings(FieldVisitDescription visit, List<Inspection> inspections, InspectionActivity activity) {
		List<Readings> readings = new ArrayList<>();
		//TODO when Aquarius adds HWM method
		return readings;
	}
}