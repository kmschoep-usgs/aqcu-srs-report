package gov.usgs.aqcu.builder;

import java.util.Map;
import java.util.List;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Inspection;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;

import gov.usgs.aqcu.model.Readings;
import gov.usgs.aqcu.util.LogExecutionTime;

@Service
public class ReadingsBuilderService {
	private static final Logger LOG = LoggerFactory.getLogger(ReadingsBuilderService.class);
	
	private static final String EXCLUDED_READING_TYPES = "ExtremeMax,ExtremeMin";

        @LogExecutionTime
	public List<Readings> getAqcuFieldVisitsReadings(FieldVisitDescription visit, FieldVisitDataServiceResponse fieldVisitResponse, String parameter){
		try {
			List<Readings> result = new ArrayList<>();
                        LOG.debug("Get inspection activities.");
			InspectionActivity activity = fieldVisitResponse.getInspectionActivity();
			
			if(activity != null){
				List<Reading> readings = activity.getReadings();
				List<Inspection> inspections = activity.getInspections();
				String visitStatus = "TODO"; //TODO see AQCU-265, this is currently being left out of the rendered report until Aquarius adds the information
	
				Map<String,List<String>> inspectionCommentsBySerial = serialNumberToComment(inspections);
	
				for(Reading read : readings){
					List<String> comments = new ArrayList<>();
					
					//comments attached to the inspection activity, linked by the reading's serial number 
                                        LOG.debug("Get inspection activity comments.");
					List<String> inspectionComments = inspectionCommentsBySerial.get(read.getSerialNumber());
					if(inspectionComments != null) {
						comments.addAll(inspectionComments);
					}
					
					//comments already attached to the reading;
                                        LOG.debug("Get reading comments and add to comments.");
					String readingComments = read.getComments();
					if(readingComments != null){
						comments.add(readingComments);
					}
					
                                        LOG.debug("Get field visit start time.");
					Instant fieldVisitTemporal = visit.getStartTime();
                                        
                                        LOG.debug("Get field visit reading time.");
					Instant readingTime = read.getTime(); 
					
                                        LOG.debug("Get readings visit details.");
					Readings readingReport = new Readings(visit.getIdentifier(), 
							visitStatus, 
							activity.getParty(), 
							read.getReadingType().toString(), 
							read.getSubLocationIdentifier(), 
							read.getParameter(), 
							read.getMonitoringMethod(),
							readingTime, 
							read.getValue().getDisplay(), 
							read.getUncertainty() != null ? read.getUncertainty().getDisplay() : null, 
							fieldVisitTemporal, 
							comments);
					result.add(readingReport);
				}
				//Filter only readings from selected parameter
                                LOG.debug("Filter readings by selected parameter.");
				result = selectedParameter(parameter, result);
				
                                LOG.debug("Filter readings by excluded types");
				//Remove readings from excluded types
				result.removeAll(excludedTypes(result));			
			}			
			
			return result;
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to build readings: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}
	/**
	 * Returns comments stored in inspections linked by serial number. These comments are known as 
	 * "reference reading comments".
	 * 
	 * @param inspections The list of Aquarius Inspections to extract serial numbers from
	 * @return The extracted serial numbers and their associated comments
	 */
        @LogExecutionTime
	private Map<String, List<String>> serialNumberToComment(List<Inspection> inspections) {
		Map<String, List<String>> toRet = new HashMap<>();

		for(Inspection inspection: inspections){
			if(StringUtils.isNotBlank(inspection.getComments())) {
                                LOG.debug("Get unique inspection serial numbers.");
				List<String> previous = toRet.get(inspection.getSerialNumber());
				if(previous == null || previous.isEmpty()){
					previous = new ArrayList<>();
					toRet.put(inspection.getSerialNumber(), previous);
				}
                                LOG.debug("Get comments for inspection serial number.");
				previous.add(inspection.getComments());
			}
		}

		return toRet;
	}
	
        @LogExecutionTime
	private List<Readings> excludedTypes(List<Readings> inReadings){
		List<Readings> outReadings = new ArrayList<>();
                LOG.debug("Get list of excluded reading types.");
		for (Readings reading: inReadings) {
			if (EXCLUDED_READING_TYPES.contains(reading.getType())) {
				outReadings.add(reading);
			}
		}
		return outReadings;
	}
	
        @LogExecutionTime
	public List<Readings> selectedParameter(String selectedParameter, List<Readings> inReadings){
		List<Readings> outReadings = new ArrayList<>();
                LOG.debug("Get readings by selected parameter.");
		for (Readings reading: inReadings) {
			if (reading.getParameter().equals(selectedParameter)) {
				outReadings.add(reading);
			}
		}
		return outReadings;
	}
	
}