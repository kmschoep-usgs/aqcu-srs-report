package gov.usgs.aqcu.retrieval;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;

import net.servicestack.client.IReturn;

@RunWith(SpringRunner.class)
public class FieldVisitDataServiceTest {

	@MockBean
	private AquariusRetrievalService aquariusService;

	private FieldVisitDataService service;
	private FieldVisitDataServiceResponse expected = new FieldVisitDataServiceResponse();

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		service = new FieldVisitDataService(aquariusService);
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(expected);
	}

	@Test
	public void get_happyTest() {
		FieldVisitDataServiceResponse actual = service.get("a", "b");
		assertEquals(expected, actual);
	}

}
