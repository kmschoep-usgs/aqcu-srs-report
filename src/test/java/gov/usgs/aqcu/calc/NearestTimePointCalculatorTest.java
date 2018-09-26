package gov.usgs.aqcu.calc;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalTimeRange;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.model.SensorReadingSummaryReading;

public class NearestTimePointCalculatorTest {
	
	@MockBean
	private static SensorReadingSummaryReading reading1;
	private static SensorReadingSummaryReading reading2;
	private static SensorReadingSummaryReading reading3;
	private static List<SensorReadingSummaryReading> readings_date_order;
	private static List<SensorReadingSummaryReading> readings_out_of_date_order;
	private static final TimeSeriesDataServiceResponse TS_DATA_RESPONSE_QUALS = new TimeSeriesDataServiceResponse();
	private static final TimeSeriesDataServiceResponse TS_DATA_RESPONSE_NO_QUALS = new TimeSeriesDataServiceResponse();
	private static final Qualifier qualifier1 = new Qualifier();
	private static final Qualifier qualifier2 = new Qualifier();
	private static final ArrayList<Qualifier> qualifiers = new ArrayList<>();
	
	
	private static final ArrayList<TimeSeriesPoint> points = new ArrayList<>(Arrays.asList(
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-26T00:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("1").setNumeric(1.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-26T12:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("2").setNumeric(2.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-27T00:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("3").setNumeric(3.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-27T12:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("4").setNumeric(4.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-28T00:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("5").setNumeric(5.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-28T09:51:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("6").setNumeric(6.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-28T10:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("7").setNumeric(7.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-28T10:20:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("8").setNumeric(8.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-28T10:35:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("9").setNumeric(9.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-28T12:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("10").setNumeric(10.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-29T00:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("11").setNumeric(11.0)),
			new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-29T12:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("12").setNumeric(12.0))
		));
	
	
	 @Before
	 public void setup() {
		 qualifier1.setDateApplied(Instant.parse("2014-10-28T09:53:00Z"));
		 qualifier1.setIdentifier("REVISED");
		 qualifier1.setStartTime(Instant.parse("2013-10-27T00:00:00Z"));
		 qualifier1.setEndTime(Instant.parse("2013-10-29T00:00:00Z"));
		 
		 qualifier2.setDateApplied(Instant.parse("2013-10-28T09:53:00Z"));
		 qualifier2.setIdentifier("REVISED");
		 qualifier2.setStartTime(Instant.parse("2013-10-29T13:00:00Z"));
		 qualifier2.setEndTime(Instant.parse("2013-10-30T00:00:00Z"));
		 
		 qualifiers.add(qualifier1);
		 qualifiers.add(qualifier2);
		 
		 TS_DATA_RESPONSE_QUALS.setApprovals(null)
			.setGapTolerances(null)
			.setGrades(null)
			.setInterpolationTypes(null)
			.setLabel("label")
			.setLocationIdentifier("loc-id")
			.setMethods(null)
			.setNotes(null)
			.setNumPoints(new Long("1"))
			.setParameter("param")
			.setPoints(points)
			.setQualifiers(qualifiers)
			.setTimeRange(new StatisticalTimeRange()
				.setStartTime(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-27T00:00:00Z")))
				.setEndTime(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-29T00:00:00Z"))))
			.setUniqueId("uuid")
			.setUnit("unit");
		 
		 TS_DATA_RESPONSE_NO_QUALS.setApprovals(null)
			.setGapTolerances(null)
			.setGrades(null)
			.setInterpolationTypes(null)
			.setLabel("label")
			.setLocationIdentifier("loc-id")
			.setMethods(null)
			.setNotes(null)
			.setNumPoints(new Long("1"))
			.setParameter("param")
			.setPoints(points)
			.setQualifiers(new ArrayList<>())
			.setTimeRange(new StatisticalTimeRange()
				.setStartTime(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-27T00:00:00Z")))
				.setEndTime(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2013-10-29T00:00:00Z"))))
			.setUniqueId("uuid")
			.setUnit("unit");
		 
		 reading1 = new SensorReadingSummaryReading();
		 reading1.setDisplayTime(Instant.parse("2013-10-28T09:53:00Z"));
		 reading1.setMonitoringMethod("monitoring method 1");
		 reading1.setParameter("param");
		 reading1.setParty("Bill and Ted");
		 reading1.setType("Routine");
		 reading1.setUncertainty("0.2");
		 reading1.setValue("999.9");
		 
		 reading2 = new SensorReadingSummaryReading();
		 reading2.setDisplayTime(Instant.parse("2013-10-28T10:15:00Z"));
		 reading2.setMonitoringMethod("monitoring method 2");
		 reading2.setParameter("stuff");
		 reading2.setParty("Bill and Ted");
		 reading2.setType("Routine");
		 reading2.setUncertainty("0.9");
		 reading2.setValue("998.9");
		 
		 reading3 = new SensorReadingSummaryReading();
		 reading3.setDisplayTime(Instant.parse("2013-10-28T10:38:00Z"));
		 reading3.setMonitoringMethod("monitoring method 2");
		 reading3.setParameter("stuff");
		 reading3.setParty("Bill and Ted");
		 reading3.setType("Routine");
		 reading3.setUncertainty("0.4");
		 reading3.setValue("997.9");
		 
		 readings_date_order = Arrays.asList(reading1, reading2, reading3);
		 readings_out_of_date_order = Arrays.asList(reading2, reading1, reading3);
	 }
	 
	 
	
	@Test
	public void test_nearest_corrected_point_with_quals_readings_in_date_order() {
		List<SensorReadingSummaryReading> nearestPointResultsWithQuals = new NearestTimePointCalculator().findNearest(readings_date_order, TS_DATA_RESPONSE_QUALS, true);
		
		assertEquals(Instant.parse("2013-10-28T09:51:00Z"), nearestPointResultsWithQuals.get(0).getNearestCorrectedTime());
		assertEquals("6", nearestPointResultsWithQuals.get(0).getNearestCorrectedValue());
		assertEquals(1, nearestPointResultsWithQuals.get(0).getQualifiers().size());
		assertEquals(qualifier1, nearestPointResultsWithQuals.get(0).getQualifiers().get(0));
		assertEquals(Instant.parse("2013-10-28T10:20:00Z"), nearestPointResultsWithQuals.get(1).getNearestCorrectedTime());
		assertEquals("8", nearestPointResultsWithQuals.get(1).getNearestCorrectedValue());
		assertEquals(1, nearestPointResultsWithQuals.get(1).getQualifiers().size());
		assertEquals(qualifier1, nearestPointResultsWithQuals.get(1).getQualifiers().get(0));
		assertEquals(Instant.parse("2013-10-28T10:35:00Z"), nearestPointResultsWithQuals.get(2).getNearestCorrectedTime());
		assertEquals("9", nearestPointResultsWithQuals.get(2).getNearestCorrectedValue());
		assertEquals(1, nearestPointResultsWithQuals.get(2).getQualifiers().size());
		assertEquals(qualifier1, nearestPointResultsWithQuals.get(2).getQualifiers().get(0));

	}
	
	@Test
	public void test_nearest_corrected_point_without_quals_readings_in_date_order() {
		List<SensorReadingSummaryReading> nearestPointResultsNoQuals = new NearestTimePointCalculator().findNearest(readings_date_order, TS_DATA_RESPONSE_NO_QUALS, true);
		
		assertEquals(Instant.parse("2013-10-28T09:51:00Z"), nearestPointResultsNoQuals.get(0).getNearestCorrectedTime());
		assertEquals("6", nearestPointResultsNoQuals.get(0).getNearestCorrectedValue());
		assertEquals(0, nearestPointResultsNoQuals.get(0).getQualifiers().size());
		assertEquals(Instant.parse("2013-10-28T10:20:00Z"), nearestPointResultsNoQuals.get(1).getNearestCorrectedTime());
		assertEquals("8", nearestPointResultsNoQuals.get(1).getNearestCorrectedValue());
		assertEquals(0, nearestPointResultsNoQuals.get(1).getQualifiers().size());
		assertEquals(Instant.parse("2013-10-28T10:35:00Z"), nearestPointResultsNoQuals.get(2).getNearestCorrectedTime());
		assertEquals("9", nearestPointResultsNoQuals.get(2).getNearestCorrectedValue());
		assertEquals(0, nearestPointResultsNoQuals.get(2).getQualifiers().size());

	}
	
	@Test
	public void test_nearest_corrected_point_with_quals_readings_not_in_date_order() {		
		List<SensorReadingSummaryReading> nearestPointResultsNoDateOrder = new NearestTimePointCalculator().findNearest(readings_out_of_date_order, TS_DATA_RESPONSE_QUALS, true);
		
		assertEquals(Instant.parse("2013-10-28T10:20:00Z"), nearestPointResultsNoDateOrder.get(0).getNearestCorrectedTime());
		assertEquals("8", nearestPointResultsNoDateOrder.get(0).getNearestCorrectedValue());
		assertEquals(Instant.parse("2013-10-28T09:51:00Z"), nearestPointResultsNoDateOrder.get(1).getNearestCorrectedTime());
		assertEquals("6", nearestPointResultsNoDateOrder.get(1).getNearestCorrectedValue());
		assertEquals(Instant.parse("2013-10-28T10:35:00Z"), nearestPointResultsNoDateOrder.get(2).getNearestCorrectedTime());
		assertEquals("9", nearestPointResultsNoDateOrder.get(2).getNearestCorrectedValue());
	}
	
	@Test
	public void test_nearest_raw_point_with_quals_readings_in_date_order() {	
		List<SensorReadingSummaryReading> nearestPointResults = new NearestTimePointCalculator().findNearest(readings_date_order, TS_DATA_RESPONSE_QUALS, false);
		
		assertEquals(Instant.parse("2013-10-28T09:51:00Z"), nearestPointResults.get(0).getNearestRawTime());
		assertEquals("6", nearestPointResults.get(0).getNearestRawValue());
		assertEquals(null, nearestPointResults.get(0).getQualifiers());
		assertEquals(Instant.parse("2013-10-28T10:20:00Z"), nearestPointResults.get(1).getNearestRawTime());
		assertEquals("8", nearestPointResults.get(1).getNearestRawValue());
		assertEquals(Instant.parse("2013-10-28T10:35:00Z"), nearestPointResults.get(2).getNearestRawTime());
		assertEquals("9", nearestPointResults.get(2).getNearestRawValue());

	}
	
	@Test
	public void test_nearest_raw_point_with_quals_readings_not_in_date_order() {
		List<SensorReadingSummaryReading> nearestPointResults = new NearestTimePointCalculator().findNearest(readings_out_of_date_order, TS_DATA_RESPONSE_QUALS, false);
		
		assertEquals(Instant.parse("2013-10-28T10:20:00Z"), nearestPointResults.get(0).getNearestRawTime());
		assertEquals("8", nearestPointResults.get(0).getNearestRawValue());
		assertEquals(null, nearestPointResults.get(0).getQualifiers());
		assertEquals(Instant.parse("2013-10-28T09:51:00Z"), nearestPointResults.get(1).getNearestRawTime());
		assertEquals("6", nearestPointResults.get(1).getNearestRawValue());
		assertEquals(null, nearestPointResults.get(1).getQualifiers());
		assertEquals(Instant.parse("2013-10-28T10:35:00Z"), nearestPointResults.get(2).getNearestRawTime());
		assertEquals("9", nearestPointResults.get(2).getNearestRawValue());
		assertEquals(null, nearestPointResults.get(2).getQualifiers());
	}
}
