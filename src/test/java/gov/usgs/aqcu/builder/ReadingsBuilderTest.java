package gov.usgs.aqcu.builder;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Inspection;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ReadingType;

import gov.usgs.aqcu.model.Readings;

@RunWith(SpringRunner.class)
public class ReadingsBuilderTest {

	@MockBean
	private ReadingsBuilderService builderService;
	private Instant now = Instant.now();
	
	private FieldVisitDescription descriptionA = new FieldVisitDescription()
			.setLocationIdentifier("00100100")
			.setStartTime(Instant.parse("2017-01-01T10:00:00Z"))
			.setEndTime(Instant.parse("2017-01-01T11:00:00Z"))
			.setIsValid(true)
			.setLastModified(now)
			.setParty("on")
			.setRemarks("this is cool")
			.setWeather("Cloudy with a chance of meatballs.")
			.setIdentifier("a");
	
	private Inspection inspection1 = new Inspection()
			.setInspectionType(InspectionType.Other)
			.setSerialNumber("Unspecified")
			.setComments("INSPECTION COMMENT");
	
	private Reading recReading1 = new Reading()
			.setParameter("Gage height")
			.setMonitoringMethod("Gage height, non-contact radar")
			.setReadingType(ReadingType.Routine)
			.setSerialNumber("Unspecified")
			.setTime(Instant.parse("2017-01-01T10:05:00Z"))
			.setValue(new DoubleWithDisplay().setDisplay("1.20"))
			.setUncertainty(new DoubleWithDisplay().setDisplay(""))
			.setComments("TEST READING COMMENT ONE");
	
	private Reading recReading2 = new Reading()
			.setParameter("Precipitation")
			.setMonitoringMethod("Single tipping bucket")
			.setReadingType(ReadingType.Routine)
			.setSerialNumber("Unspecified")
			.setTime(Instant.parse("2017-01-01T10:06:00Z"))
			.setValue(new DoubleWithDisplay().setDisplay("1.21"))
			.setUncertainty(new DoubleWithDisplay().setDisplay(""))
			.setComments("TEST READING COMMENT TWO");
	
	private Reading recReading3 = new Reading()
			.setParameter("Precipitation")
			.setMonitoringMethod("Single tipping bucket")
			.setReadingType(ReadingType.Routine)
			.setSerialNumber("BR549")
			.setTime(Instant.parse("2017-01-01T10:06:00Z"))
			.setValue(new DoubleWithDisplay().setDisplay("1.21"))
			.setUncertainty(new DoubleWithDisplay().setDisplay(""))
			.setComments("TEST READING COMMENT THREE");
	
	private Reading refReading1 = new Reading()
			.setParameter("Gage height")
			.setMonitoringMethod("Gage height, non-contact radar")
			.setReadingType(ReadingType.ReferencePrimary)
			.setSerialNumber("Unspecified")
			.setTime(Instant.parse("2017-01-01T10:15:00Z"))
			.setValue(new DoubleWithDisplay().setDisplay("1.30"))
			.setUncertainty(new DoubleWithDisplay().setDisplay(""))
			.setComments("TEST READING COMMENT THREE");
	
	private Reading refReading2 = new Reading()
			.setParameter("Precipitation")
			.setMonitoringMethod("Single tipping bucket")
			.setReadingType(ReadingType.ReferencePrimary)
			.setTime(Instant.parse("2017-01-01T10:07:00Z"))
			.setValue(new DoubleWithDisplay().setDisplay("1.27"))
			.setUncertainty(new DoubleWithDisplay().setDisplay(""))
			.setSerialNumber("Unspecified");
	
	private Reading refReading3 = new Reading()
			.setParameter("Precipitation")
			.setMonitoringMethod("Single tipping bucket")
			.setReadingType(ReadingType.ExtremeMax)
			.setTime(Instant.parse("2017-01-01T10:08:00Z"))
			.setUncertainty(new DoubleWithDisplay().setDisplay(""))
			.setValue(new DoubleWithDisplay().setDisplay("1.21"))
			.setSerialNumber("Unspecified");
	
	private ArrayList<Reading> readingList = new ArrayList<>(Arrays.asList(recReading1, recReading2, recReading3, refReading1, refReading2, refReading3));
	
	private ArrayList<Inspection> inspectionList = new ArrayList<>(Arrays.asList(inspection1));
	
	private InspectionActivity inspectionActivity = new InspectionActivity()
			.setParty("TEST PARTY")
			.setReadings(readingList)
			.setInspections(inspectionList);
	
	private FieldVisitDataServiceResponse fieldVisitData = new FieldVisitDataServiceResponse()
			.setInspectionActivity(inspectionActivity);
	
	@Before
	public void setup() {
		builderService = new ReadingsBuilderService();
	}

	@Test
	public void get_happyTest() {
		List<Readings> reading = builderService.getAqcuFieldVisitsReadings(descriptionA, fieldVisitData, "Precipitation");

		assertEquals(3, reading.size());
		assertEquals("Precipitation", reading.get(0).getParameter());
		assertEquals("TEST PARTY", reading.get(0).getParty());
		assertEquals(Instant.parse("2017-01-01T10:06:00Z"), reading.get(0).getTime());
		assertEquals(Arrays.asList("INSPECTION COMMENT","TEST READING COMMENT TWO"), reading.get(0).getComments());
		assertEquals("a", reading.get(0).getFieldVisitIdentifier());
		assertEquals(Instant.parse("2017-01-01T10:00:00Z"), reading.get(0).getVisitTime());
		assertEquals(ReadingType.Routine.toString(), reading.get(0).getType());
		
		assertEquals("Precipitation", reading.get(1).getParameter());
		assertEquals("TEST PARTY", reading.get(1).getParty());
		assertEquals(Instant.parse("2017-01-01T10:06:00Z"), reading.get(1).getTime());
		assertEquals(Arrays.asList("TEST READING COMMENT THREE"), reading.get(1).getComments());
		assertEquals("a", reading.get(1).getFieldVisitIdentifier());
		assertEquals(Instant.parse("2017-01-01T10:00:00Z"), reading.get(1).getVisitTime());
		assertEquals(ReadingType.Routine.toString(), reading.get(1).getType());
		
		assertEquals("Precipitation", reading.get(2).getParameter());
		assertEquals("TEST PARTY", reading.get(2).getParty());
		assertEquals(Instant.parse("2017-01-01T10:07:00Z"), reading.get(2).getTime());
		assertEquals(Arrays.asList("INSPECTION COMMENT"), reading.get(2).getComments());
		assertEquals("a", reading.get(2).getFieldVisitIdentifier());
		assertEquals(Instant.parse("2017-01-01T10:00:00Z"), reading.get(2).getVisitTime());
		assertEquals(ReadingType.ReferencePrimary.toString(), reading.get(2).getType());
	}

}
