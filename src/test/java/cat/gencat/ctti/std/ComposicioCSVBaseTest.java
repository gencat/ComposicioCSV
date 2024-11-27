package cat.gencat.ctti.std;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;

import cat.gencat.ctti.std.composicio.constants.ComposicioPDFConstants;

public abstract class ComposicioCSVBaseTest extends STDBaseTest{
	
	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_PATH_FITXER_ENTRADA_COMPOSICIO + "}")
	protected String pathFitxerEntrada;

	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_PATH_FITXER_SORTIDA_COMPOSICIO + "}")
	protected String pathFitxerSortida;

	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_PATH_FITXER_PLANTILLA_COMPOSICIO + "}")
	protected String pathFitxerPlantilla;

	protected final String PATH_DATA_PLANTILLES = PATH_DATA + "/plantilles";

	protected final String FILE_NAME_LARGE = "Sample large";
	protected final String FILE_NAME_ECOPIA = "STF-3030_OrigenAmbFirma";
	protected final String FILE_NAME_FORMULARI = "OrigenAmbFormulari";
	protected final String FILE_NAME_ADJUNT = "OrigenAmbAdjunt";
	
	protected final String FILE_NAME_PLANTILLA_IECISA_BASE = "iecisa_plantilla_base";
	protected final String FILE_NAME_PLANTILLA_CODI_SEGUR_02 = "plantilla_codi_segur_02";

	protected String plantillesStdWorkingDirectory;
	
	@Before
	public void setUp() throws IOException, URISyntaxException {
		setDirectories();

		createTestWorkingDirectory();
		createSTDWorkingDirectory();

		copyPlantilles();
		copyDocumentsEntrada();
	}
	
	protected void copyDocumentsEntrada() throws IOException, URISyntaxException {
		FileUtils.copyDirectory(Paths.get(this.getClass().getResource(PATH_DATA_IN).toURI()).toFile(),
				new File(entradaStdWorkingDirectory));
	}

	protected void setDirectories() {
		super.setDirectories();
		entradaStdWorkingDirectory = stdWorkingDirectory + pathFitxerEntrada;
		sortidaStdWorkingDirectory = stdWorkingDirectory + pathFitxerSortida;
		plantillesStdWorkingDirectory = stdWorkingDirectory + pathFitxerPlantilla;

		testWorkingDirectory = stdWorkingDirectory + "/" + this.getClass().getSimpleName();
	}

	protected void copyPlantilles() throws IOException, URISyntaxException {
		FileUtils.copyDirectory(Paths.get(this.getClass().getResource(PATH_DATA_PLANTILLES).toURI()).toFile(),
				new File(plantillesStdWorkingDirectory));
	}

	protected void createSTDWorkingDirectory() {
		super.createSTDWorkingDirectory();
		new File(entradaStdWorkingDirectory).mkdirs();
		new File(sortidaStdWorkingDirectory).mkdirs();
		new File(plantillesStdWorkingDirectory).mkdirs();
	}

	protected void createTestWorkingDirectory() {
		new File(testWorkingDirectory).mkdirs();
	}

}
