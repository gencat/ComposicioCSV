package cat.gencat.ctti.std.csv.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/canigo-core.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CodiSegurVerificacioServiceTest {

	@Autowired
	private CodiSegurVerificacioService codiSegurVerificacioService;
	
	@Test
	public void contextLoaded() {
		Assert.assertNotNull(codiSegurVerificacioService);
	}
	
	@Test
	public void checkGetTimeStampFormatCSV() throws ParseException{
		String timeStamp = codiSegurVerificacioService.getTimeStampFormatCSV();
		Assert.assertNotNull(timeStamp);
		
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		formato.parse(timeStamp);
		
	}

}