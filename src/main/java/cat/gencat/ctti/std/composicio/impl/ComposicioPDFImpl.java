/**
 * 
 */
package cat.gencat.ctti.std.composicio.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import cat.gencat.ctti.std.composicio.ComposicioPDF;
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFConstants;
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.escalat.EscalatPDF;
import cat.gencat.ctti.std.composicio.escalat.exceptions.EscalatException;
import cat.gencat.ctti.std.composicio.estampat.EstampatPDF;
import cat.gencat.ctti.std.composicio.estampat.dto.ImatgeSegell;
import cat.gencat.ctti.std.composicio.estampat.dto.StringSegell;
import cat.gencat.ctti.std.composicio.estampat.exceptions.EstampatException;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.composicio.fusionat.FusionatPDF;
import cat.gencat.ctti.std.composicio.fusionat.exceptions.FusionatException;
import cat.gencat.forms.webservice.Annex;
import cat.gencat.forms.webservice.ResultAnnexos;

/**
 * @author A163617
 * 
 */
@Component
public class ComposicioPDFImpl implements ComposicioPDF {

	private final Logger logger = LoggerFactory.getLogger(ComposicioPDFImpl.class);

	private final String LOG_INICI_OPERACIO = "Inici Operacio";
	private final String LOG_FINAL_OPERACIO = "Final Operacio";
	
	public static final String ERROR_NO_FITXER_ENTRADA = "ERROR no s'ha trobat el fitxer d'entrada";
	public static final String ERROR_VARIABLE = "ERROR la variable ";
	public static final String TXT_RESERVADA = " esta reservada";

	@Autowired
	private EscalatPDF escalatPDF;
	@Autowired
	private FusionatPDF fusionatPDF;
	@Autowired
	private EstampatPDF estampatPDF;

	public void composarPDF(InputStream inputStream, InputStream inputStreamPlantilla,
			InputStream inputStreamPlantillaMarcaAigua, InputStream inputStreamPlantillaHoritzontal,
			InputStream inputStreamPlantillaHoritzontalMarcaAigua, Float porcentatgeEscalat, Float posicioEscalatX,
			Float posicioEscalatY, Map<String, StringSegell> parametresString,
			Map<String, ImatgeSegell> parametresImatge, ResultAnnexos annexos, OutputStream outputStream)
			throws ComposicioPDFException {
		logger.debug("[ComposicioPDFImpl][composarPDF] " + LOG_INICI_OPERACIO);

		boolean dobleOrientacio = false;

		try {
			// Es comprova que les dades passades siguin correctes
			logger.debug("[ComposicioPDFImpl][composarPDF] Comprovacio de parametres...");
			comprovarInputStream(inputStream);
			comprovarPlantilla(inputStreamPlantilla);
			comprovarPlantillaMarcaAigua(inputStreamPlantillaMarcaAigua);
			comprovarPorcentatgeEscalat(porcentatgeEscalat);
			comprovarParametres(parametresString, parametresImatge);

			if (inputStreamPlantillaHoritzontal != null) {
				dobleOrientacio = true;
				comprovarPlantillaMarcaAigua(inputStreamPlantillaHoritzontalMarcaAigua);
			}

			logger.debug("[ComposicioPDFImpl][composarPDF] parametres OK");

			parametresString = initParametresString(parametresString);

			PdfReader pdfReader = new PdfReader(inputStream);
			PdfReader readerPlantillaInicial = new PdfReader(inputStreamPlantilla);
			PdfReader readerPlantillaMarcaAiguaInicial = new PdfReader(inputStreamPlantillaMarcaAigua);

			PdfReader readerPlantillaHoritzontal = null;
			PdfReader readerPlantillaHoritzontalMarcaAigua = null;
			Rectangle horitzontalSize = null;

			if (dobleOrientacio) {
				readerPlantillaHoritzontal = new PdfReader(inputStreamPlantillaHoritzontal);
				readerPlantillaHoritzontalMarcaAigua = new PdfReader(inputStreamPlantillaHoritzontalMarcaAigua);
				horitzontalSize = readerPlantillaHoritzontal.getPageSizeWithRotation(1);
			}

			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, outputStream);

			Rectangle currentSize = readerPlantillaInicial.getPageSizeWithRotation(1);
			document.setPageSize(currentSize);

			obreDocument(document);

			int n = pdfReader.getNumberOfPages();
			StringSegell totalPagines = new StringSegell();
			totalPagines.setText(String.valueOf(n));
			parametresString.put(ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_TOTAL_PAGINES, totalPagines);

			for (int i = 1; i <= n; i++) {
				logger.debug("[ComposicioPDFImpl][composarPDF] Inici tractament pàgina " + i + "...");

				boolean horitzontal = pdfReader.getPageSizeWithRotation(i).getWidth() > pdfReader
						.getPageSizeWithRotation(i).getHeight();

				PdfContentByte cb = writer.getDirectContent();

				Rectangle originalSize = pdfReader.getPageSizeWithRotation(i);
				Rectangle actualRectangle = currentSize;

				PdfReader actualReader = readerPlantillaInicial;
				PdfReader actualReaderAigua = readerPlantillaMarcaAiguaInicial;

				if (dobleOrientacio && horitzontal) {
					actualRectangle = horitzontalSize;
					actualReader = readerPlantillaHoritzontal;
					actualReaderAigua = readerPlantillaHoritzontalMarcaAigua;
				}

				document.setPageSize(actualRectangle);
				document.newPage();

				StringSegell numeroPagina = new StringSegell();
				numeroPagina.setText(String.valueOf(i));
				parametresString.put(ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_NUMERO_PAGINA, numeroPagina);

				// Torem a agafar els formularis inicials
				ByteArrayOutputStream bufferPlantilla = new ByteArrayOutputStream();
				PdfStamper stampPlantilla = new PdfStamper(new PdfReader(actualReader), bufferPlantilla);
				ByteArrayOutputStream bufferPlantillaMarcaAigua = new ByteArrayOutputStream();
				PdfStamper stampPlantillaMarcaAigua = new PdfStamper(new PdfReader(actualReaderAigua),
						bufferPlantillaMarcaAigua);

				// Per defecte agafem les dades de la plantilla i ho centrem si
				// es necessari
				float[] posicioEscalatComposicio = getPosicioEscalatPlantilla(stampPlantilla, originalSize,
						porcentatgeEscalat, actualRectangle);
				float porcentatgeEscalatComposicioX = getPorcentatgeEscalatX(posicioEscalatComposicio, originalSize);
				float porcentatgeEscalatComposicioY = getPorcentatgeEscalatY(posicioEscalatComposicio, originalSize);
				posicioEscalatComposicio = centrarPosicioEscalat(posicioEscalatComposicio, originalSize,
						porcentatgeEscalatComposicioX, porcentatgeEscalatComposicioY);

				if (posicioEscalatX != null && posicioEscalatY != null) {
					// si ens passen la posicio, fixem el lloc
					if (porcentatgeEscalat != null) {
						// si ens passen el porcentatge i la posicio, fixem el
						// lloc i el porcentatge
						porcentatgeEscalatComposicioX = porcentatgeEscalat;
						porcentatgeEscalatComposicioY = porcentatgeEscalat;
					}
					float[] posicioEscalat = { posicioEscalatX, posicioEscalatY };
					posicioEscalatComposicio = mourePosicioEscalatFixadaUsuari(posicioEscalat, originalSize,
							porcentatgeEscalatComposicioX, porcentatgeEscalatComposicioY, actualRectangle);
				} else if ((posicioEscalatX == null || posicioEscalatY == null) && porcentatgeEscalat != null) {
					// si ens passen el porcentatge,redimensionem la posicio a
					// partir de la posicio centrada de la plantilla
					posicioEscalatComposicio = redimensionatPosicioEscalat(porcentatgeEscalatComposicioX,
							porcentatgeEscalatComposicioY, porcentatgeEscalat, posicioEscalatComposicio, originalSize);
					porcentatgeEscalatComposicioX = porcentatgeEscalat;
					porcentatgeEscalatComposicioY = porcentatgeEscalat;
				}

				// estampem el background
				estampatPDF.estamparPDF(stampPlantilla, parametresString, parametresImatge, i, n);
				PdfImportedPage pageEstampada = writer.getImportedPage(new PdfReader(bufferPlantilla.toByteArray()), 1);
				fusionatPDF.fusioPDF(cb, pageEstampada, 1);

				// escalem l'original
				PdfImportedPage pageAEscalar = writer.getImportedPage(pdfReader, i);
				escalatPDF.escalarPDF(cb, pageAEscalar, porcentatgeEscalatComposicioX, porcentatgeEscalatComposicioY,
						posicioEscalatComposicio, pdfReader, i);

				// fusionem amb plantilla de les marques d'aigua
				float opacitat = getOpacitat(stampPlantillaMarcaAigua);
				estampatPDF.estamparPDF(stampPlantillaMarcaAigua, parametresString, parametresImatge, i, n);
				PdfImportedPage pageMarcaAiguaAFusionar = writer
						.getImportedPage(new PdfReader(bufferPlantillaMarcaAigua.toByteArray()), 1);
				fusionatPDF.fusioPDF(cb, pageMarcaAiguaAFusionar, opacitat);
				logger.debug("[ComposicioPDFImpl][composarPDF] Pagina Tractada OK");

			}
			afageixPropietatsDocument(document, parametresString);

			// Afegim els annexos al nou document
			if (annexos != null && annexos.getAnnexos().getValue() != null) {
				List<Annex> listAnnexos = annexos.getAnnexos().getValue().getAnnex();
				if (listAnnexos != null) {
					for (Iterator<Annex> iterator = listAnnexos.iterator(); iterator.hasNext();) {
						Annex annex = iterator.next();
						writer.addFileAttachment(null, annex.getContingut().getValue(), null,
								annex.getNomArxiu().getValue());
					}
				}
			}

			tancaDocument(document);

			logger.debug("[ComposicioPDFImpl][composarPDF] " + LOG_FINAL_OPERACIO);
		} catch (IOException e) {
			logger.error("ERROR de lectura de fitxer ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR de lectura de fitxer");
		} catch (DocumentException e) {
			logger.error("ERROR de creaci� de fitxer ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR de creaci� de fitxer");
		} catch (EscalatException e) {
			logger.error("ERROR d'escalat ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESCALANT_DOCUMENT,
					"ERROR d'escalat");
		} catch (FusionatException e) {
			logger.error("ERROR de fusionat ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FUSIONANT_DOCUMENT,
					"ERROR de fusionat");
		} catch (EstampatException e) {
			logger.error("ERROR de l'estampat ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR de l'estampat");
		}
	}

	private Map<String, StringSegell> initParametresString(Map<String, StringSegell> parametresString) {
		Map<String, StringSegell> parametresStringRetorn = parametresString;
		if (parametresStringRetorn == null) {
			parametresStringRetorn = new HashMap<>();
		}
		return parametresStringRetorn;
	}

	/**
	 * @param porcentatgeEscalatComposicio
	 * @param porcentatgeEscalat
	 * @param posicioEscalatComposicio
	 * @param originalSize
	 * @return
	 */
	private float[] redimensionatPosicioEscalat(float porcentatgeEscalatOriginalX, float porcentatgeEscalatOriginalY,
			float porcentatgeEscalatFinal, float[] posicioEscalatComposicio, Rectangle originalSize) {
		float longitudWidthdOriginal = originalSize.getWidth() * porcentatgeEscalatOriginalX;
		float longitudHeightOriginal = originalSize.getHeight() * porcentatgeEscalatOriginalY;

		float longitudWidthFinal = originalSize.getWidth() * porcentatgeEscalatFinal;
		float longitudHeightFinal = originalSize.getHeight() * porcentatgeEscalatFinal;

		float redimensioWidth = longitudWidthFinal - longitudWidthdOriginal;
		float redimensioHeight = longitudHeightFinal - longitudHeightOriginal;

		posicioEscalatComposicio[0] = posicioEscalatComposicio[0] - (redimensioWidth / 2);
		posicioEscalatComposicio[1] = posicioEscalatComposicio[1] - (redimensioHeight / 2);
		posicioEscalatComposicio[2] = posicioEscalatComposicio[2] - (redimensioWidth / 2);
		posicioEscalatComposicio[3] = posicioEscalatComposicio[3] - (redimensioHeight / 2);

		return posicioEscalatComposicio;
	}

	/**
	 * @param posicioEscalat
	 * @param originalSize
	 * @param porcentatgeEscalatComposicio
	 * @return
	 */
	private float[] mourePosicioEscalatFixadaUsuari(float[] posicioEscalat, Rectangle originalSize,
			float porcentatgeEscalatComposicioX, float porcentatgeEscalatComposicioY, Rectangle currentSize) {
		// |A----B|
		// | |
		// |C----D|

		float xA = posicioEscalat[0];
		float yA = currentSize.getHeight() - posicioEscalat[1];

		float xC = xA;
		float yC = yA - originalSize.getHeight() * porcentatgeEscalatComposicioY;

		float xB = xC + originalSize.getWidth() * porcentatgeEscalatComposicioX;
		float yB = yA;
		return new float[] { xC, yC, xB, yB };
	}

	/**
	 * @param porcentatgeEscalat
	 * @throws ComposicioPDFException
	 */
	private void comprovarPorcentatgeEscalat(Float porcentatgeEscalat) throws ComposicioPDFException {
		if (porcentatgeEscalat != null && (porcentatgeEscalat < 0 || porcentatgeEscalat > 1)) {
			logger.error("ERROR el porcentatge d'escalat est� fora de rang");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR el porcentatge d'escalat est� fora de rang");
		}
	}

	/**
	 * @param stampPlantilla
	 * @param originalSize
	 * @param currentSize
	 * @return
	 * @throws ComposicioPDFException
	 */
	private float[] getPosicioEscalatPlantilla(PdfStamper stampPlantilla, Rectangle originalSize,
			Float porcentatgeEscalat, Rectangle currentSize) throws ComposicioPDFException {
		// posicio origen baix esquerra
		float[] resultat = { 0, 0, currentSize.getWidth(), currentSize.getHeight() };// en
																						// principi
																						// tot
																						// el
																						// document

		String nomCamp = getNomCampTag(stampPlantilla, ComposicioPDFConstants.EXPRESSIO_REGULAR_TAG_DOCUMENT);
		if (nomCamp != null) {
			FieldPosition fieldPosition = stampPlantilla.getAcroFields().getFieldPositions(nomCamp).get(0);
			float[] posicio = new float[] { fieldPosition.page, fieldPosition.position.getLeft(), fieldPosition.position.getBottom(),
					fieldPosition.position.getRight(), fieldPosition.position.getTop() };
			if (posicio != null) {
				// System.arraycopy(src, srcPos, dest, destPos, length) [OLD]
				// Mejora del rendimiento
				for (int i = 1; i < posicio.length; i++) {
					resultat[i - 1] = posicio[i];
				}
			}
			resetCamp(stampPlantilla, nomCamp);// un cop obtingut el valor
												// elimien el valor del form
		}
		return resultat;
	}

	/**
	 * @param resultat
	 * @param originalSize
	 * @param porcentatgeEscalat
	 * @return
	 */
	private float[] centrarPosicioEscalat(float[] resultat, Rectangle originalSize, Float porcentatgeEscalatX,
			Float porcentatgeEscalatY) {
		float longitudWidthPosicioEscalar = resultat[2] - resultat[0];
		float longitudHeightPosicioEscalar = resultat[3] - resultat[1];

		float longitudWidthOriginalEscalat = originalSize.getWidth() * porcentatgeEscalatX;
		float longitudHeightOriginalEscalat = originalSize.getHeight() * porcentatgeEscalatY;

		if (longitudPosicioEscalarSuperiorOriginalEscalat(longitudWidthPosicioEscalar, longitudWidthOriginalEscalat)) {
			resultat[0] = resultat[0] + (longitudWidthPosicioEscalar - longitudWidthOriginalEscalat) / 2;
			resultat[2] = resultat[2] - (longitudWidthPosicioEscalar - longitudWidthOriginalEscalat) / 2;
		}
		if (longitudPosicioEscalarSuperiorOriginalEscalat(longitudHeightPosicioEscalar,
				longitudHeightOriginalEscalat)) {
			resultat[1] = resultat[1] + (longitudHeightPosicioEscalar - longitudHeightOriginalEscalat) / 2;
			resultat[3] = resultat[3] - (longitudHeightPosicioEscalar - longitudHeightOriginalEscalat) / 2;
		}

		return resultat;
	}

	/**
	 * @param longitudPosicioEscalar
	 * @param longitudOriginalEscalat
	 * @return
	 */
	private boolean longitudPosicioEscalarSuperiorOriginalEscalat(float longitudPosicioEscalar,
			float longitudOriginalEscalat) {
		return longitudPosicioEscalar > longitudOriginalEscalat;
	}

	/**
	 * @param stampPlantilla
	 * @param expresioRegular
	 * @return
	 */
	private String getNomCampTag(PdfStamper stampPlantilla, String expresioRegular) {
		String nomCampRetorn = null;

		Map<String, Item> camps = stampPlantilla.getAcroFields().getFields();
		if (camps != null) {
			boolean trobat = false;
			for (Iterator it = camps.keySet().iterator(); it.hasNext() && !trobat;) {
				String nomCamp = (String) it.next();
				String valorCamp = stampPlantilla.getAcroFields().getField(nomCamp);
				if (matches(valorCamp, expresioRegular)) {
					nomCampRetorn = nomCamp;
					trobat = true;
				}
			}
		}

		return nomCampRetorn;
	}

	/**
	 * 
	 * @param valorCamp
	 * @param expressioRegular
	 * @return
	 */
	private boolean matches(String valorCamp, String expressioRegular) {
		boolean resultat = false;

		Pattern pattern = Pattern.compile(expressioRegular, Pattern.DOTALL);// amb
																			// DOTALL
																			// el
																			// .
																			// fa
																			// match
																			// amb
																			// qualsevol
																			// caracter
																			// incl�s
																			// retorns
																			// de
																			// linia
		Matcher matcher = pattern.matcher(valorCamp);
		resultat = matcher.matches();

		return resultat;
	}

	/**
	 * @param stampPlantilla
	 * @return
	 * @throws ComposicioPDFException
	 */
	private float getOpacitat(PdfStamper stampPlantilla) throws ComposicioPDFException {
		float opacitat = 1;

		String nomCamp = getNomCampTag(stampPlantilla, ComposicioPDFConstants.EXPRESSIO_REGULAR_TAG_MARCAAIGUA);
		if (nomCamp != null) {
			String linia = stampPlantilla.getAcroFields().getField(nomCamp);

			String opacitatString = getValorAtribut(linia, ComposicioPDFConstants.ATRIBUT_OPACITAT_TAG);

			opacitat = comprovarAtributOpacitat(opacitatString);

			resetCamp(stampPlantilla, nomCamp);// un cop obtingut el valor
												// elimien el valor del form
		}

		return opacitat;
	}

	/**
	 * @param stampPlantilla
	 * @param nomCamp
	 * @throws ComposicioPDFException
	 */
	private void resetCamp(PdfStamper stampPlantilla, String nomCamp) throws ComposicioPDFException {
		try {
			stampPlantilla.getAcroFields().setField(nomCamp, "");
		} catch (IOException e) {
			logger.error("ERROR resetejant el camp ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR resetejant el camp");
		} catch (DocumentException e) {
			logger.error("ERROR resetejant el camp ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR resetejant el camp");
		}
	}

	/**
	 * @param linia
	 * @param nomAtribut
	 * @return
	 */
	private String getValorAtribut(String linia, String nomAtribut) {

		int i = linia.indexOf(nomAtribut);
		String atribut = linia.substring(i);

		i = atribut.indexOf("\"");
		String opacitatString = atribut.substring(i + 1);

		i = opacitatString.indexOf("\"");
		return opacitatString.substring(0, i);
	}

	/**
	 * @param opacitatString
	 * @return
	 * @throws ComposicioPDFException
	 */
	private float comprovarAtributOpacitat(String opacitatString) throws ComposicioPDFException {
		float opacitat = 1;
		try {
			opacitat = Float.valueOf(opacitatString);

			if (opacitat > 1 || opacitat < 0) {
				logger.error("ERROR la opacitat est� fora de rang");
				throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						"ERROR la opacitat est� fora de rang");
			}

		} catch (Exception e) {
			logger.error("ERROR l'atribut opacitat no �s un n�mero ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR l'atribut opacitat no �s un n�mero");
		}
		return opacitat;
	}

	/**
	 * @param posicioEscalat
	 * @param currentSize
	 * @return
	 */
	private float getPorcentatgeEscalatX(float posicioEscalat[], Rectangle currentSize) {
		float porcentatge = 1;// en principi al 100%

		if (posicioEscalat != null) {
			float widthEscalat = posicioEscalat[2] - posicioEscalat[0];

			porcentatge = getPorcentatgeEscalat(widthEscalat, currentSize.getWidth());

		}

		return porcentatge;
	}

	private float getPorcentatgeEscalatY(float posicioEscalat[], Rectangle currentSize) {
		float porcentatge = 1;// en principi al 100%

		if (posicioEscalat != null) {
			float heightEscalat = posicioEscalat[3] - posicioEscalat[1];
			porcentatge = getPorcentatgeEscalat(heightEscalat, currentSize.getHeight());
		}

		return porcentatge;
	}

	/**
	 * @param document
	 * @param parametresString
	 */
	private void afageixPropietatsDocument(Document document, Map<String, StringSegell> parametresString) {

		StringSegell assumptePropietats = parametresString.get(CLAU_ASSUMPTE_PROPIETATS);
		if (assumptePropietats != null && assumptePropietats instanceof StringSegell) {
			document.addSubject(assumptePropietats.getText());
		}

		StringSegell autorPropietats = parametresString.get(CLAU_AUTOR_PROPIETATS);
		if (autorPropietats != null && autorPropietats instanceof StringSegell) {
			document.addAuthor(autorPropietats.getText());
		}

		StringSegell paraulesClauPropietats = parametresString.get(CLAU_PARAULES_CLAU_PROPIETATS);
		if (paraulesClauPropietats != null && paraulesClauPropietats instanceof StringSegell) {
			document.addKeywords(paraulesClauPropietats.getText());
		}

		StringSegell titolPropietats = parametresString.get(CLAU_TITOL_PROPIETATS);
		if (titolPropietats != null && titolPropietats instanceof StringSegell) {
			document.addTitle(titolPropietats.getText());
		}

	}

	/**
	 * @param document
	 */
	private void tancaDocument(Document document) {
		document.close();
	}

	/**
	 * @param document
	 */
	private void obreDocument(Document document) {
		document.open();
	}

	/**
	 * @param longitudTotal
	 * @param longitudEscalat
	 * @return
	 */
	private float getPorcentatgeEscalat(float longitudTotal, float longitudEscalat) {
		float resultat = 1;// no estirem, es a dir si es mes gran que el tamany
							// total no s'expandira
		if (longitudTotal < longitudEscalat) {
			resultat = longitudTotal / longitudEscalat;
		}

		return resultat;
	}

	/**
	 * @param inputStream
	 * @throws ComposicioPDFException
	 */
	private void comprovarInputStream(InputStream inputStream) throws ComposicioPDFException {
		try {
			if (inputStream == null) {
				logger.error(ERROR_NO_FITXER_ENTRADA);
				throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FILE_NOT_FOUND,
						ERROR_NO_FITXER_ENTRADA);
			}
			// PdfReader pdfReader = new PdfReader(inputStream);
			// pdfReader.close();
		} catch (Exception e) {
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FILE_NOT_FOUND,
					ERROR_NO_FITXER_ENTRADA);
		}
	}

	/**
	 * @param inputStreamPlantilla
	 * @throws ComposicioPDFException
	 */
	private void comprovarPlantilla(InputStream inputStreamPlantilla) throws ComposicioPDFException {
		if (inputStreamPlantilla == null) {
			logger.error("ERROR PDF plantilla null");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"PDF plantilla null");
		}
	}

	/**
	 * @param inputStreamPlantillaMarcaAigua
	 * @throws ComposicioPDFException
	 */
	private void comprovarPlantillaMarcaAigua(InputStream inputStreamPlantillaMarcaAigua)
			throws ComposicioPDFException {
		if (inputStreamPlantillaMarcaAigua == null) {
			logger.error("ERROR la marca d'aigua de la plantilla null");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"La marca d'aigua de la plantilla null");
		}
	}

	/**
	 * @param parametresString
	 * @param parametresImatge
	 * @throws ComposicioPDFException
	 */
	private void comprovarParametres(Map<String, StringSegell> parametresString,
			Map<String, ImatgeSegell> parametresImatge) throws ComposicioPDFException {
		comprovarParametresNull(parametresString, parametresImatge);
		comprovarVariablesReservades(parametresString, parametresImatge);
	}

	/**
	 * @param parametres
	 * @throws ComposicioPDFException
	 */
	private void comprovarVariablesReservades(Map<String, StringSegell> parametresString,
			Map<String, ImatgeSegell> parametresImatge) throws ComposicioPDFException {

		if (parametresString != null) {
			if (parametresString.get(ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_NUMERO_PAGINA) != null) {
				logger.error(ERROR_VARIABLE + ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_NUMERO_PAGINA
						+ TXT_RESERVADA);
				throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						ERROR_VARIABLE + ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_NUMERO_PAGINA
								+ TXT_RESERVADA);
			}
			if (parametresString.get(ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_TOTAL_PAGINES) != null) {
				logger.error(ERROR_VARIABLE + ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_TOTAL_PAGINES
						+ TXT_RESERVADA);
				throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						ERROR_VARIABLE + ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_TOTAL_PAGINES
								+ TXT_RESERVADA);
			}
		}
		if (parametresImatge != null) {
			if (parametresImatge.get(ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_NUMERO_PAGINA) != null) {
				logger.error(ERROR_VARIABLE + ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_NUMERO_PAGINA
						+ TXT_RESERVADA);
				throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						ERROR_VARIABLE + ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_NUMERO_PAGINA
								+ TXT_RESERVADA);
			}
			if (parametresImatge.get(ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_TOTAL_PAGINES) != null) {
				logger.error(ERROR_VARIABLE + ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_TOTAL_PAGINES
						+ TXT_RESERVADA);
				throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						ERROR_VARIABLE + ComposicioPDFConstants.NOM_VARIABLE_RESERVADA_TOTAL_PAGINES
								+ TXT_RESERVADA);
			}
		}
	}

	/**
	 * 
	 * @param parametresString
	 * @param parametresImatge
	 * @throws ComposicioPDFException
	 */
	private void comprovarParametresNull(Map<String, StringSegell> parametresString,
			Map<String, ImatgeSegell> parametresImatge) throws ComposicioPDFException {
		if (parametresString == null && parametresImatge == null) {
			logger.error("ERROR parametres a insertar al PDF null");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR parametres a insertar al PDF null");
		}
	}

}
