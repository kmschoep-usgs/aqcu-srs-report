package gov.usgs.aqcu.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

import gov.usgs.aqcu.parameter.SensorReadingSummaryRequestParameters;
import gov.usgs.aqcu.util.AqcuTimeUtils;
import gov.usgs.aqcu.util.TimeSeriesUtils;
import gov.usgs.aqcu.calc.NearestTimePointCalculator;
import gov.usgs.aqcu.calc.ReadingsTimeCombiner;
import gov.usgs.aqcu.model.*;
import gov.usgs.aqcu.retrieval.*;

@Service
public class ReportBuilderService {
	public static final String REPORT_TITLE = "Sensor Reading Summary";
	public static final String REPORT_TYPE = "sensorreadingsummary";
	private static final String READING_TYPE_REF = "Reference";
	private static final String READING_TYPE_REF_PRIM = "ReferencePrimary";
	private static final String ALT_READING_TYPE_REF_PRIM = "REFERENCE_PRIMARY";
	private static final String INSPECTION_ACTIVITY = "Inspection";

	private static final Logger LOG = LoggerFactory.getLogger(ReportBuilderService.class);

	private LocationDescriptionListService locationDescriptionListService;
	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	private TimeSeriesDataCorrectedService timeSeriesDataCorrectedService;
	private TimeSeriesDataRawService timeSeriesDataRawService;
	private FieldVisitDescriptionsService fieldVisitDescriptionsService;
	private FieldVisitDataService fieldVisitDataService;
	private ReadingsBuilderService readingsBuilderService;
	private QualifierLookupService qualifierLookupService;

	@Autowired
	public ReportBuilderService(
		LocationDescriptionListService locationDescriptionListService,
		TimeSeriesDescriptionListService timeSeriesDescriptionListService,
		TimeSeriesDataCorrectedService timeSeriesDataCorrectedService,
		TimeSeriesDataRawService timeSeriesDataRawService,
		FieldVisitDescriptionsService  fieldVisitDescriptionsService,
		FieldVisitDataService fieldVisitDataService,
		ReadingsBuilderService readingsBuilderService,
		QualifierLookupService qualifierLookupService) {
		this.timeSeriesDataCorrectedService = timeSeriesDataCorrectedService;
		this.timeSeriesDataRawService = timeSeriesDataRawService;
		this.locationDescriptionListService = locationDescriptionListService;
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
		this.fieldVisitDescriptionsService = fieldVisitDescriptionsService;
		this.fieldVisitDataService = fieldVisitDataService;
		this.readingsBuilderService = readingsBuilderService;
		this.qualifierLookupService = qualifierLookupService;
	}

	public SensorReadingSummaryReport buildReport(SensorReadingSummaryRequestParameters requestParameters, String requestingUser) {
		SensorReadingSummaryReport report = new SensorReadingSummaryReport();
		List<Readings> readings = new ArrayList<>();
		List<SensorReadingSummaryReading> srsReadings = new ArrayList<>();
		ReadingsTimeCombiner readingsTimeCombiner = new ReadingsTimeCombiner();
		NearestTimePointCalculator nearestTimePointCalculator = new NearestTimePointCalculator();

		//Primary TS Metadata
		TimeSeriesDescription primaryDescription = timeSeriesDescriptionListService.getTimeSeriesDescription(requestParameters.getPrimaryTimeseriesIdentifier());
		ZoneOffset primaryZoneOffset = TimeSeriesUtils.getZoneOffset(primaryDescription);
		String primaryStationId = primaryDescription.getLocationIdentifier();
		
		//Time Series Corrected Data
		TimeSeriesDataServiceResponse timeSeriesCorrectedData = getCorrectedData(requestParameters, primaryZoneOffset);
		
		//Time Series Raw Data
		TimeSeriesDataServiceResponse timeSeriesRawData = getRawData(requestParameters, primaryZoneOffset);
		
		//Field Visits
		List<FieldVisitDescription> fieldVisits = fieldVisitDescriptionsService.getDescriptions(primaryStationId, primaryZoneOffset, requestParameters);
		
		//Readings
		for (FieldVisitDescription visit: fieldVisits) {
			FieldVisitDataServiceResponse fieldVisitData = fieldVisitDataService.get(visit.getIdentifier(), INSPECTION_ACTIVITY);
			List<Readings> reading = readingsBuilderService.getAqcuFieldVisitsReadings(visit, fieldVisitData, primaryDescription.getParameter());
			readings.addAll(reading);
		}
		srsReadings = readingsTimeCombiner.combine(readings, READING_TYPE_REF + "," + READING_TYPE_REF_PRIM + "," + ALT_READING_TYPE_REF_PRIM);
		srsReadings = nearestTimePointCalculator.findNearest(srsReadings, timeSeriesCorrectedData, true);
		srsReadings = nearestTimePointCalculator.findNearest(srsReadings, timeSeriesRawData, false);
		
		report.setReadings(srsReadings);
		
		//Report Metadata
		report.setReportMetadata(getReportMetadata(requestParameters,
			requestingUser,
			primaryDescription.getLocationIdentifier(), 
			primaryDescription.getIdentifier(),
			primaryDescription.getUtcOffset(),
			primaryDescription.getIdentifier(),
			getReadingQualifiers(srsReadings)
		));

		return report;
	}

	protected SensorReadingSummaryReportMetadata getReportMetadata(SensorReadingSummaryRequestParameters requestParameters, String requestingUser, String stationId, String primaryParameter, Double utcOffset, String timeSeriesLabel, List<Qualifier> qualifierList) {
		SensorReadingSummaryReportMetadata metadata = new SensorReadingSummaryReportMetadata();
		metadata.setTitle(REPORT_TITLE);
		metadata.setRequestingUser(requestingUser);
		metadata.setRequestParameters(requestParameters);
		metadata.setStationId(stationId);
		metadata.setStationName(locationDescriptionListService.getByLocationIdentifier(stationId).getName());
		metadata.setTimeSeriesParams(primaryParameter);
		metadata.setTimeseriesLabel(timeSeriesLabel);
		metadata.setTimezone(AqcuTimeUtils.getTimezone(utcOffset));
		
		if(qualifierList != null && !qualifierList.isEmpty()) {
			metadata.setQualifierMetadata(qualifierLookupService.getByQualifierList(qualifierList));
		}
		
		return metadata;
	}
	
	protected TimeSeriesDataServiceResponse getCorrectedData(SensorReadingSummaryRequestParameters requestParams, ZoneOffset zoneOffset) {
		//Fetch Corrected Data
		TimeSeriesDataServiceResponse dataResponse = timeSeriesDataCorrectedService.get(
			requestParams.getPrimaryTimeseriesIdentifier(), 
			requestParams.getStartInstant(zoneOffset), 
			requestParams.getEndInstant(zoneOffset));

		return dataResponse;
	}
	
	protected TimeSeriesDataServiceResponse getRawData(SensorReadingSummaryRequestParameters requestParameters, ZoneOffset primaryZoneOffset) {
		//Fetch Corrected Data
		TimeSeriesDataServiceResponse dataResponse = timeSeriesDataRawService.get(
			requestParameters.getPrimaryTimeseriesIdentifier(), 
			requestParameters.getStartInstant(primaryZoneOffset), 
			requestParameters.getEndInstant(primaryZoneOffset));

		return dataResponse;
	}
	
	protected List<Qualifier> getReadingQualifiers (List<SensorReadingSummaryReading> inReadings){
		List<Qualifier> qualList = new ArrayList<>();
		
		for (SensorReadingSummaryReading srs: inReadings) {
			qualList.addAll(srs.getQualifiers());
		}
		return qualList;
	}
}