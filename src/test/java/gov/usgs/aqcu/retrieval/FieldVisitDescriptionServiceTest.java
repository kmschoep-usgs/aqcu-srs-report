package gov.usgs.aqcu.retrieval;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceResponse;

import gov.usgs.aqcu.parameter.SensorReadingSummaryRequestParameters;
import net.servicestack.client.IReturn;

@RunWith(SpringRunner.class)
public class FieldVisitDescriptionServiceTest {

	@MockBean
	private AquariusRetrievalService aquariusService;

	private FieldVisitDescriptionService service;
	private SensorReadingSummaryRequestParameters parameters;
	private Instant now = Instant.now();
	public static final LocalDate REPORT_END_DATE = LocalDate.of(2018, 03, 17);
	public static final LocalDate REPORT_START_DATE = LocalDate.of(2018, 03, 16);

	private FieldVisitDescription descriptionA = new FieldVisitDescription()
			.setLocationIdentifier("00100100")
			.setStartTime(now.minusSeconds(1000))
			.setEndTime(now.minusSeconds(10))
			.setIsValid(true)
			.setLastModified(now)
			.setParty("on")
			.setRemarks("this is cool")
			.setWeather("Cloudy with a chance of meatballs.")
			.setIdentifier("a");
	private FieldVisitDescription descriptionB = new FieldVisitDescription().setIdentifier("b");
	private FieldVisitDescription descriptionC = new FieldVisitDescription().setIdentifier("c");

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		service = new FieldVisitDescriptionService(aquariusService);
		parameters = new SensorReadingSummaryRequestParameters();
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(new FieldVisitDescriptionListServiceResponse()
				.setFieldVisitDescriptions(new ArrayList<FieldVisitDescription>(Arrays.asList(descriptionA, descriptionB, descriptionC))));
	}

	@Test
	public void getTest() throws Exception {
		FieldVisitDescriptionListServiceResponse actual = service.get("", null, null);
		assertEquals(3, actual.getFieldVisitDescriptions().size());
		assertThat(actual.getFieldVisitDescriptions(), containsInAnyOrder(descriptionA, descriptionB, descriptionC));
	}

	@Test
	public void getTimeSeriesDescriptions_happyTest() {
		parameters.setStartDate(REPORT_START_DATE);
		parameters.setEndDate(REPORT_END_DATE);
		List<FieldVisitDescription> actual = service.getDescriptions("station", ZoneOffset.UTC, parameters);
		assertEquals(3, actual.size());
		assertThat(actual, containsInAnyOrder(descriptionA, descriptionB, descriptionC));
	}

}
