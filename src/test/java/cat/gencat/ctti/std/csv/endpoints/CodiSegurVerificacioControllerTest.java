package cat.gencat.ctti.std.csv.endpoints;


import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cat.gencat.ctti.std.ComposicioCSVBaseTest;
import cat.gencat.ctti.std.csv.constants.CodiSegurVerificacioConstants;
import cat.gencat.ctti.std.csv.constants.CodiSegurVerificacioErrorsConstants;
import cat.gencat.ctti.std.csv.dto.GenerarCSV;
import cat.gencat.ctti.std.csv.dto.GenerarCSVTimeStamp;
import cat.gencat.ctti.std.csv.exceptions.CodiSegurVerificacioException;
import cat.gencat.ctti.std.dto.ResultSTD;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/canigo-core.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CodiSegurVerificacioControllerTest extends ComposicioCSVBaseTest{
	
	@Autowired
	private CodiSegurVerificacioController codiSegurVerificacioController;

	@Test
    public void contextLoaded(){
		Assert.assertNotNull(codiSegurVerificacioController);
	}
	
	@Test
	public void checkGenerarCSVTimeStampAmbitRequired() {
		try {
			codiSegurVerificacioController.generarCSVTimeStamp(UUID.randomUUID().toString(), null, APLICACIO_VALIDA,
					new GenerarCSVTimeStamp());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getCodiError());
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getMessage());
		}
	}

	@Test
	public void checkGenerarCSVTimeStampAmbitValid() {
		try {
			codiSegurVerificacioController.generarCSVTimeStamp(UUID.randomUUID().toString(), "ambitNoValid", APLICACIO_VALIDA,
					new GenerarCSVTimeStamp());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getCodiError());
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getMessage());
		}
	}

	@Test
	public void checkGenerarCSVTimeStampAplicacioRequired() {
		try {
			codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), AMBIT_VALID, null, new GenerarCSVTimeStamp());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getCodiError());
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getMessage());
		}
	}

	@Test
	public void checkGenerarCSVTimeStampAplicacioNoValid() {
		try {
			codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), AMBIT_VALID, "aplicacioNoValid", new GenerarCSVTimeStamp());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getCodiError());
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getMessage());
		}
	}

	@Test
	public void checkGenerarCSVTimeStampDataRequired() {
		try {
			codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA, new GenerarCSVTimeStamp());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_FILE_EMPTY, e.getCodiError());
		}
	}

	@Test
	public void checkGenerarCSVTimeStampKeyRequired() throws IOException {
		GenerarCSVTimeStamp generarCSVTimeStamp = new GenerarCSVTimeStamp();
		generarCSVTimeStamp.setData(IOUtils.toByteArray(this.getClass()
				.getResource(PATH_DATA_IN + "/" + FILE_NAME_ECOPIA + CodiSegurVerificacioConstants.EXTENSIO_PDF)));
		try {
			codiSegurVerificacioController.generarCSVTimeStamp(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA, generarCSVTimeStamp);
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT,
					e.getCodiError());
		}
	}

	@Test
	public void checkGenerarCSVTimeStamp() throws IOException, CodiSegurVerificacioException {
		GenerarCSVTimeStamp generarCSVTimeStamp = getGenerarCSVTimeStamp();

		ResultSTD resultSTD = codiSegurVerificacioController.generarCSVTimeStamp(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA,
				generarCSVTimeStamp);

		checkGenerarCSVTimeStampResultSTD(resultSTD, generarCSVTimeStamp.getTimeStamp());
	}

	protected GenerarCSVTimeStamp getGenerarCSVTimeStamp() throws IOException {
		GenerarCSVTimeStamp generarCSVTimeStamp = new GenerarCSVTimeStamp();
		generarCSVTimeStamp.setData(IOUtils.toByteArray(this.getClass()
				.getResource(PATH_DATA_IN + "/" + FILE_NAME_ECOPIA + CodiSegurVerificacioConstants.EXTENSIO_PDF)));
		generarCSVTimeStamp.setKey("");
		generarCSVTimeStamp.setTimeStamp("timestamp");
		
		return generarCSVTimeStamp;
	}

	private void checkGenerarCSVTimeStampResultSTD(ResultSTD resultSTD, String timeStamp) {
		Assert.assertNotNull(resultSTD);
		// Assert.assertNotNull(resultSTD.getStatus());  //NOSONAR
		Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_NO_ERROR, new Integer(resultSTD.getStatus()));
		Assert.assertNotNull(resultSTD.getKey());
		Assert.assertNotNull(resultSTD.getTimeStamp());
		Assert.assertEquals(timeStamp, resultSTD.getTimeStamp());
	}

	@Test
	public void checkGenerarCSVAmbitRequired() {
		try {
			codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), null, APLICACIO_VALIDA, new GenerarCSV());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getCodiError());
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getMessage());
		}
	}

	@Test
	public void checkGenerarCSVAmbitValid() {
		try {
			codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), "ambitNoValid", APLICACIO_VALIDA, new GenerarCSV());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getCodiError());
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getMessage());
		}
	}

	@Test
	public void checkGenerarCSVAplicacioRequired() {
		try {
			codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), AMBIT_VALID, null, new GenerarCSV());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getCodiError());
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getMessage());
		}
	}

	@Test
	public void checkGenerarCSVAplicacioNoValid() {
		try {
			codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), AMBIT_VALID, "aplicacioNoValid", new GenerarCSV());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getCodiError());
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getMessage());
		}
	}

	@Test
	public void checkGenerarCSVDataRequired() {
		try {
			codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA, new GenerarCSV());
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_FILE_EMPTY, e.getCodiError());
		}
	}

	@Test
	public void checkGenerarCSVKeyRequired() throws IOException {
		GenerarCSV generarCSV = new GenerarCSV();
		generarCSV.setData(IOUtils.toByteArray(this.getClass()
				.getResource(PATH_DATA_IN + "/" + FILE_NAME_ECOPIA + CodiSegurVerificacioConstants.EXTENSIO_PDF)));
		try {
			codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA, generarCSV);
			Assert.fail();
		} catch (CodiSegurVerificacioException e) {
			Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT,
					e.getCodiError());
		}
	}
	
	@Test
	public void checkGenerarCSV() throws IOException, CodiSegurVerificacioException {
		GenerarCSV generarCSV = getGenerarCSV();

		ResultSTD resultSTD = codiSegurVerificacioController.generarCSV(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA,
				generarCSV);

		checkGenerarCSVResultSTD(resultSTD);
	}

	protected GenerarCSV getGenerarCSV() throws IOException {
		GenerarCSV generarCSV = new GenerarCSV();
		
		generarCSV.setData(IOUtils.toByteArray(this.getClass()
				.getResource(PATH_DATA_IN + "/" + FILE_NAME_ECOPIA + CodiSegurVerificacioConstants.EXTENSIO_PDF)));
		generarCSV.setKey("");
		
		return generarCSV;
	}

	private void checkGenerarCSVResultSTD(ResultSTD resultSTD) {
		Assert.assertNotNull(resultSTD);
		//Assert.assertNotNull(resultSTD.getStatus());   //NOSONAR
		Assert.assertEquals(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_NO_ERROR, new Integer(resultSTD.getStatus()));
		Assert.assertNotNull(resultSTD.getKey());
		Assert.assertNotNull(resultSTD.getTimeStamp());
	}
	
}
