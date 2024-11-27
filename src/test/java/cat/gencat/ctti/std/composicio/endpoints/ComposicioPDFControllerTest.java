package cat.gencat.ctti.std.composicio.endpoints;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
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
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFConstants;
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFRemot;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFStream;
import cat.gencat.ctti.std.composicio.estampat.dto.ImatgeSegell;
import cat.gencat.ctti.std.composicio.estampat.dto.StringSegell;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.dto.ResultSTD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/canigo-core.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComposicioPDFControllerTest extends ComposicioCSVBaseTest {

	@Autowired
	private ComposicioPDFController composicioPDFController;

	@Test
	public void contextLoaded() {
		Assert.assertNotNull(composicioPDFController);
	}

	@Test
	public void checkComposarPDFStreamWithoutParametresString() throws IOException, ComposicioPDFException {
		ComposarPDFStream composarPDFStream = getComposarPDFStreamIecisaPlantillaBase(FILE_NAME_ADJUNT, false);
		composarPDFStream.setParametresString(null);

		String uuid = UUID.randomUUID().toString();

		ResultSTD resultSTD = composicioPDFController.composarPDFStream(uuid, AMBIT_VALID, APLICACIO_VALIDA,
				composarPDFStream);

		checkComposarPDFStreamResultSTD(resultSTD, composarPDFStream.isGenerarCSV());

		writeResult(uuid, resultSTD.getArxiu());
	}

	@Test
	public void checkComposarPDFStreamWithoutParametresImatge() throws IOException, ComposicioPDFException {
		ComposarPDFStream composarPDFStream = getComposarPDFStreamPlantillaCodiSegur02(FILE_NAME_ADJUNT, false);
		composarPDFStream.setParametresImatge(null);

		String uuid = UUID.randomUUID().toString();

		ResultSTD resultSTD = composicioPDFController.composarPDFStream(uuid, AMBIT_VALID, APLICACIO_VALIDA,
				composarPDFStream);

		checkComposarPDFStreamResultSTD(resultSTD, composarPDFStream.isGenerarCSV());

		writeResult(uuid, resultSTD.getArxiu());
	}

	@Test
	public void checkComposarPDFStreamParametresRequired() throws IOException {
		ComposarPDFStream composarPDFStream = new ComposarPDFStream();
		composarPDFStream.setNomPlantilla(FILE_NAME_PLANTILLA_IECISA_BASE);
		composarPDFStream.setInputStreamEntrada(IOUtils.toByteArray(this.getClass()
				.getResource(PATH_DATA_IN + "/" + FILE_NAME_ECOPIA + ComposicioPDFConstants.EXTENSIO_PDF)));
		try {
			composicioPDFController.composarPDFStream(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA,
					composarPDFStream);
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT, e.getCodiError());
		}
	}

	@Test
	public void checkComposarPDFStreamDataRequired() {
		try {
			composicioPDFController.composarPDFStream(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA,
					new ComposarPDFStream());
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FILE_EMPTY, e.getCodiError());
		}
	}

	@Test
	public void checkComposarPDFStreamMidaFitxer() throws IOException {
		ComposarPDFStream composarPDFStream = new ComposarPDFStream();
		composarPDFStream.setInputStreamEntrada(IOUtils.toByteArray(this.getClass()
				.getResource(PATH_DATA_IN + "/" + FILE_NAME_LARGE + ComposicioPDFConstants.EXTENSIO_PDF)));

		try {
			composicioPDFController.composarPDFStream(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA,
					composarPDFStream);
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_MAX_LENGTH_FILE, e.getCodiError());
		}
	}

	@Test
	public void checkComposarPDFStreamIecisaPlantillaBase() throws IOException, ComposicioPDFException {
		checkComposarPDFStream(FILE_NAME_ECOPIA, false);
	}

	@Test
	public void checkComposarPDFStreamPDFAmbFormulariGenCSV() throws IOException, ComposicioPDFException {
		checkComposarPDFStream(FILE_NAME_FORMULARI, true);
	}

	@Test
	public void checkComposarPDFStreamPDFAmbFormulariNoGenCSV() throws IOException, ComposicioPDFException {
		checkComposarPDFStream(FILE_NAME_FORMULARI, false);
	}

	@Test
	public void checkComposarPDFStreamPDFAmbAdjuntGenCSV() throws IOException, ComposicioPDFException {
		checkComposarPDFStream(FILE_NAME_ADJUNT, true);
	}

	@Test
	public void checkComposarPDFStreamPDFAmbAdjuntNoGenCSV() throws IOException, ComposicioPDFException {
		checkComposarPDFStream(FILE_NAME_ADJUNT, false);
	}

	private void checkComposarPDFStream(String filename, boolean genCSV) throws IOException, ComposicioPDFException {
		ComposarPDFStream composarPDFStream = getComposarPDFStreamIecisaPlantillaBase(filename, genCSV);

		String uuid = UUID.randomUUID().toString();

		ResultSTD resultSTD = composicioPDFController.composarPDFStream(uuid, AMBIT_VALID, APLICACIO_VALIDA,
				composarPDFStream);

		checkComposarPDFStreamResultSTD(resultSTD, composarPDFStream.isGenerarCSV());

		writeResult(uuid, resultSTD.getArxiu());
	}

	protected ComposarPDFStream getComposarPDFStreamIecisaPlantillaBase(String filename, boolean genCSV) throws IOException {
		ComposarPDFStream composarPDFStream = new ComposarPDFStream();

		composarPDFStream.setNomPlantilla(FILE_NAME_PLANTILLA_IECISA_BASE);
		composarPDFStream.setInputStreamEntrada(IOUtils.toByteArray(getURL(filename)));
		composarPDFStream.setGenerarCSV(genCSV);
		composarPDFStream.setParametresString(getParametresStringIecisaPlantillaBase(genCSV));
		composarPDFStream.setParametresImatge(getParametresImatgeIecisaPlantillaBase(genCSV));

		return composarPDFStream;
	}
	
	protected URL getURL(String filename) throws FileNotFoundException{
		URL url = this.getClass().getResource(PATH_DATA_IN + "/" + filename + ComposicioPDFConstants.EXTENSIO_PDF);
		if(url == null){
			url = this.getClass().getResource(PATH_DATA_IN + "/" + filename + ComposicioPDFConstants.EXTENSIO_PDF.toUpperCase());
		}
		return url;
	}

	protected ComposarPDFStream getComposarPDFStreamPlantillaCodiSegur02(String filename, boolean genCSV)
			throws IOException {
		ComposarPDFStream composarPDFStream = new ComposarPDFStream();

		composarPDFStream.setNomPlantilla(FILE_NAME_PLANTILLA_CODI_SEGUR_02);
		composarPDFStream.setInputStreamEntrada(IOUtils.toByteArray(getURL(filename)));
		composarPDFStream.setGenerarCSV(genCSV);
		composarPDFStream.setParametresString(getParametresStringPlantillaCodiSegur02(genCSV));

		return composarPDFStream;
	}

	private void checkComposarPDFStreamResultSTD(ResultSTD resultSTD, boolean generarCSV) {
		Assert.assertNotNull(resultSTD);
		//Assert.assertNotNull(resultSTD.getStatus()); //NOSONAR
		Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_NO_ERROR, new Integer(resultSTD.getStatus()));
		Assert.assertNotNull(resultSTD.getArxiu());
		if (generarCSV) {
			Assert.assertNotNull(resultSTD.getTimeStamp());
		}
	}

	private Map<String, StringSegell> getParametresStringPlantillaCodiSegur02(boolean genCSV) {
		Map<String, StringSegell> parametres = new HashMap<String, StringSegell>();

		if (!genCSV) {
			parametres.put("CSV", getStringSegell("00KPP7EWZQKNWSYZZFPHN5M7RT8JDBOQ"));
		}

		return parametres;
	}

	private Map<String, StringSegell> getParametresStringIecisaPlantillaBase(boolean genCSV) {
		Map<String, StringSegell> parametres = new HashMap<String, StringSegell>();

		parametres.put("num_reg_sortida", getStringSegell("23456"));
		parametres.put("num_reg_sortida_label", getStringSegell("Núm. Registre Sortida:"));
		parametres.put("tipologia_origen", getStringSegell("Original electrònic"));
		parametres.put("tipologia_copia", getStringSegell("Cópia autèntica"));
		parametres.put("data_creacio", getStringSegell("02/09/2016"));
		parametres.put("data_caducitat", getStringSegell("29/11/2016"));
		parametres.put("texto_diligencia", getStringSegell(
				"Document electronic garantit amb signatura electronica (certificats de l'Agencia Catalana de Certificació). "));
		parametres.put("ens_peticionari", getStringSegell("GENERALITAT DE CATALUNYA"));
		parametres.put("qrValue", getStringSegell(
				"http://integracio.www14.gencat.cat/ecopia_vrf/AppJava/views/index.xhtml?csv=00KPP7EWZQKNWSYZZFPHN5M7RT8JDBOQ"));
		parametres.put("signat_per", getStringSegell("CPISR-1 Roser Lopez Sala"));
		parametres.put("data_signatura", getStringSegell("11/11/2015"));

		if (!genCSV) {
			parametres.put("csv", getStringSegell("00KPP7EWZQKNWSYZZFPHN5M7RT8JDBOQ"));
		}

		return parametres;
	}

	private Map<String, ImatgeSegell> getParametresImatgeIecisaPlantillaBase(boolean genCSV) {
		Map<String, ImatgeSegell> parametres = new HashMap<String, ImatgeSegell>();

		parametres.put("logo", getImatgeSegell(Base64.getDecoder().decode(
				"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")));

		return parametres;
	}

	private StringSegell getStringSegell(String text) {
		StringSegell stringSegell = new StringSegell();
		stringSegell.setText(text);
		return stringSegell;
	}

	private ImatgeSegell getImatgeSegell(byte[] contingut) {
		ImatgeSegell imatgeSegell = new ImatgeSegell();
		imatgeSegell.setContingut(contingut);
		return imatgeSegell;
	}

	@Test
	public void checkComposarPDFRemotAmbitRequired() {
		try {
			composicioPDFController.composarPDFRemot(UUID.randomUUID().toString(), null, APLICACIO_VALIDA,
					new ComposarPDFRemot());
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getCodiError());
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getMessage());
		}
	}

	@Test
	public void checkComposarPDFRemotAmbitNoValid() {
		try {
			composicioPDFController.composarPDFRemot(UUID.randomUUID().toString(), "ambitNoValid", APLICACIO_VALIDA,
					new ComposarPDFRemot());
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getCodiError());
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_VALIDS, e.getMessage());
		}
	}

	@Test
	public void checkComposarPDFRemotAplicacioRequired() {
		try {
			composicioPDFController.composarPDFRemot(UUID.randomUUID().toString(), AMBIT_VALID, null,
					new ComposarPDFRemot());
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getCodiError());
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_INFORMATS,
					e.getMessage());
		}
	}

	@Test
	public void checkComposarPDFRemotAplicacioNoValid() {
		try {
			composicioPDFController.composarPDFRemot(UUID.randomUUID().toString(), AMBIT_VALID, "aplicacioNoValid",
					new ComposarPDFRemot());
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_AMBIT_APLICACIO_NO_VALIDS,
					e.getCodiError());
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_AMBIT_APLICACIO_NO_VALIDS, e.getMessage());
		}
	}

	@Test
	public void checkComposarPDFRemotNomFitxerEntradaRequired() {
		try {
			composicioPDFController.composarPDFRemot(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA,
					new ComposarPDFRemot());
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FILE_EMPTY, e.getCodiError());
		}
	}

	@Test
	public void checkComposarPDFRemotNomFitxerEntradaNoValid() {
		ComposarPDFRemot composarPDFRemot = new ComposarPDFRemot();
		composarPDFRemot.setNomFitxerEntrada("nomFitxerEntradaNoValid");

		try {
			composicioPDFController.composarPDFRemot(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA,
					composarPDFRemot);
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FILE_EMPTY, e.getCodiError());
		}
	}

	@Test
	public void checkComposarPDFRemotNomFitxerSortidaRequired() {
		ComposarPDFRemot composarPDFRemot = new ComposarPDFRemot();
		composarPDFRemot.setNomFitxerEntrada(FILE_NAME_ECOPIA);

		try {
			composicioPDFController.composarPDFRemot(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA,
					composarPDFRemot);
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FILE_EMPTY, e.getCodiError());
		}
	}

	@Test
	public void checkComposarPDFRemotWithoutParametresString() throws IOException, ComposicioPDFException {
		ComposarPDFRemot composarPDFRemot = getComposarPDFRemotIecisaPlantillaBase(FILE_NAME_ADJUNT, false);
		composarPDFRemot.setParametresString(null);

		String uuid = UUID.randomUUID().toString();

		ResultSTD resultSTD = composicioPDFController.composarPDFRemot(uuid, AMBIT_VALID, APLICACIO_VALIDA,
				composarPDFRemot);

		checkComposarPDFRemotResultSTD(resultSTD, composarPDFRemot.isGenerarCSV());

		System.out.println(resultSTD.getKey());
	}

	@Test
	public void checkComposarPDFRemotWithoutParametresImatge() throws IOException, ComposicioPDFException {
		ComposarPDFRemot composarPDFRemot = getComposarPDFRemotPlantillaCodiSegur02(FILE_NAME_ADJUNT, false);
		composarPDFRemot.setParametresImatge(null);

		String uuid = UUID.randomUUID().toString();

		ResultSTD resultSTD = composicioPDFController.composarPDFRemot(uuid, AMBIT_VALID, APLICACIO_VALIDA,
				composarPDFRemot);

		checkComposarPDFRemotResultSTD(resultSTD, composarPDFRemot.isGenerarCSV());

		System.out.println(resultSTD.getKey());
	}

	@Test
	public void checkComposarPDFRemotParametresRequired() throws IOException {
		ComposarPDFRemot composarPDFRemot = new ComposarPDFRemot();
		composarPDFRemot.setNomPlantilla(FILE_NAME_PLANTILLA_IECISA_BASE);
		composarPDFRemot.setNomFitxerEntrada(FILE_NAME_ECOPIA);
		composarPDFRemot.setNomFitxerSortida(FILE_NAME_ECOPIA + ComposicioPDFConstants.EXTENSIO_PDF);
		try {
			composicioPDFController.composarPDFRemot(UUID.randomUUID().toString(), AMBIT_VALID, APLICACIO_VALIDA,
					composarPDFRemot);
			Assert.fail();
		} catch (ComposicioPDFException e) {
			Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT, e.getCodiError());
		}
	}

	@Test
	public void checkComposarPDFRemotIecisaPlantillaBase() throws IOException, ComposicioPDFException {
		checkComposarPDFRemot(FILE_NAME_ECOPIA, false);
	}

	@Test
	public void checkComposarPDFRemotPDFAmbFormulariGenCSV() throws IOException, ComposicioPDFException {
		checkComposarPDFRemot(FILE_NAME_FORMULARI, true);
	}

	@Test
	public void checkComposarPDFRemotPDFAmbFormulariNoGenCSV() throws IOException, ComposicioPDFException {
		checkComposarPDFRemot(FILE_NAME_FORMULARI, false);
	}

	@Test
	public void checkComposarPDFRemotPDFAmbAdjuntGenCSV() throws IOException, ComposicioPDFException {
		checkComposarPDFRemot(FILE_NAME_ADJUNT, true);
	}

	@Test
	public void checkComposarPDFRemotPDFAmbAdjuntNoGenCSV() throws IOException, ComposicioPDFException {
		checkComposarPDFRemot(FILE_NAME_ADJUNT, false);
	}

	private void checkComposarPDFRemot(String filename, boolean genCSV) throws IOException, ComposicioPDFException {
		ComposarPDFRemot composarPDFRemot = getComposarPDFRemotIecisaPlantillaBase(filename, genCSV);

		ResultSTD resultSTD = composicioPDFController.composarPDFRemot(UUID.randomUUID().toString(), AMBIT_VALID,
				APLICACIO_VALIDA, composarPDFRemot);

		checkComposarPDFRemotResultSTD(resultSTD, composarPDFRemot.isGenerarCSV());

		System.out.println(resultSTD.getKey());
	}

	protected ComposarPDFRemot getComposarPDFRemotIecisaPlantillaBase(String filename, boolean genCSV) {
		ComposarPDFRemot composarPDFRemot = new ComposarPDFRemot();

		composarPDFRemot.setNomFitxerEntrada(filename);
		composarPDFRemot.setNomFitxerSortida(filename + ComposicioPDFConstants.EXTENSIO_PDF);
		composarPDFRemot.setNomPlantilla(FILE_NAME_PLANTILLA_IECISA_BASE);
		composarPDFRemot.setGenerarCSV(genCSV);
		composarPDFRemot.setParametresString(getParametresStringIecisaPlantillaBase(genCSV));
		composarPDFRemot.setParametresImatge(getParametresImatgeIecisaPlantillaBase(genCSV));

		return composarPDFRemot;
	}

	private ComposarPDFRemot getComposarPDFRemotPlantillaCodiSegur02(String filename, boolean genCSV) {
		ComposarPDFRemot composarPDFRemot = new ComposarPDFRemot();

		composarPDFRemot.setNomFitxerEntrada(filename);
		composarPDFRemot.setNomFitxerSortida(filename + ComposicioPDFConstants.EXTENSIO_PDF);
		composarPDFRemot.setNomPlantilla(FILE_NAME_PLANTILLA_CODI_SEGUR_02);
		composarPDFRemot.setGenerarCSV(genCSV);
		composarPDFRemot.setParametresString(getParametresStringPlantillaCodiSegur02(genCSV));

		return composarPDFRemot;
	}

	private void checkComposarPDFRemotResultSTD(ResultSTD resultSTD, boolean generarCSV) {
		Assert.assertNotNull(resultSTD);
		//Assert.assertNotNull(resultSTD.getStatus()); //NOSONAR
		Assert.assertEquals(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_NO_ERROR, new Integer(resultSTD.getStatus()));
		Assert.assertNotNull(resultSTD.getKey());
		if (generarCSV) {
			Assert.assertNotNull(resultSTD.getTimeStamp());
		}
	}

}
