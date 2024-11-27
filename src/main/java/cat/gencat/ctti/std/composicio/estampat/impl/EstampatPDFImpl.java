package cat.gencat.ctti.std.composicio.estampat.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfStamper;

import cat.gencat.ctti.std.composicio.constants.ComposicioPDFConstants;
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.estampat.EstampatPDF;
import cat.gencat.ctti.std.composicio.estampat.dto.ImatgeSegell;
import cat.gencat.ctti.std.composicio.estampat.dto.StringSegell;
import cat.gencat.ctti.std.composicio.estampat.exceptions.EstampatException;
import cat.gencat.ctti.std.composicio.estampat.qr.QuickRevisionCodes;
import noNamespace.QrDocument;
import noNamespace.QrDocument.Qr;

/**
 * @author CSCanig�
 * 
 */
@Component
public class EstampatPDFImpl implements EstampatPDF {

	@Autowired
	QuickRevisionCodes qrCodes;

	private static final Logger logger = LoggerFactory.getLogger(EstampatPDFImpl.class);
	
	public static final String ERROR_TXT = "ERROR al tractar el valor del camp del document ";
	public static final String ERROR_PARAMETRES = "ERROR parametres a insertar al PDF null";
	public static final String ERROR_IMAGES = "ERROR al insertar la imatge al document ";
	
	public void estamparPDF(PdfStamper stampPlantilla, Map<String, StringSegell> parametresString,
			Map<String, ImatgeSegell> parametresImatge, int numeroPaginaActual, int numeroPaginesTotal)
			throws EstampatException {
		logger.debug("[EstampatPDFImpl][estamparPDF] Inici operacio");
		try {
			// Es comprova que les dades passades siguin correctes
			comprovarPdfStamper(stampPlantilla);
			comprovarParametres(parametresString, parametresImatge);
			comprovarNumeroPagines(numeroPaginaActual, numeroPaginesTotal);

			Map<String, Item> camps = stampPlantilla.getAcroFields().getFields();

			if (camps != null) {
				Object[] arrayKeySet = camps.keySet().toArray();
				for (int i = 0; i < arrayKeySet.length; i++) {
					tractarValorCamp(stampPlantilla, (String) arrayKeySet[i], parametresString, parametresImatge,
							numeroPaginaActual, numeroPaginesTotal);
				}

				// OLD: STF-2619 comentado para evitar la exception de
				// ConcurrentModificationException
				// for(Iterator it = camps.keySet().iterator();it.hasNext();){
				//
				// String nomCamp = (String)it.next();
				// tractarValorCamp(stampPlantilla,nomCamp,parametres,numeroPaginaActual,numeroPaginesTotal);
				//
				// }
			}

			stampPlantilla.setFormFlattening(true);
			stampPlantilla.close();
			logger.debug("[EstampatPDFImpl][estamparPDF] Fi operacio");
		} catch (DocumentException e) {
			logger.error("ERROR al estampar el document ", e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR al estampar el document");
		} catch (IOException e) {
			logger.error("ERROR al estampar el document ", e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR al estampar el document");
		}

		// logger.debug("FI estamparPDF");
	}

	/**
	 * @param numeroPaginaActual
	 * @param numeroPaginesTotal
	 * @throws EstampatException
	 */
	private void comprovarNumeroPagines(int numeroPaginaActual, int numeroPaginesTotal) throws EstampatException {
		if (numeroPaginaActual <= 0) {
			logger.error("ERROR numeroPaginaActual incorrecte");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR numeroPaginaActual incorrecte");
		}
		if (numeroPaginaActual > numeroPaginesTotal) {
			logger.error("ERROR numeroPaginesTotal inferior a numeroPaginaActual");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR numeroPaginesTotal inferior a numeroPaginaActual");
		}
	}

	/**
	 * @param stampPlantilla
	 * @param nomCamp
	 * @param parametresString
	 * @param parametresImatge
	 * @throws EstampatException
	 */
	private void tractarValorCamp(PdfStamper stampPlantilla, String nomCamp, Map<String, StringSegell> parametresString,
			Map<String, ImatgeSegell> parametresImatge, int numeroPaginaActual, int numeroPaginesTotal)
			throws EstampatException {
		logger.debug("[EstampatPDFImpl][tractarValorCamp] Inici operacio");
		try {
			comprovarNomCamp(nomCamp);

			String valorCamp = stampPlantilla.getAcroFields().getField(nomCamp);

			if (matches(valorCamp, ComposicioPDFConstants.EXPRESSIO_REGULAR_TAG_TEXT)) {
				logger.debug("[EstampatPDFImpl][tractarValorCamp] Es TAG TEXT ");
				String valorCampTractat = tractarTagsText(nomCamp, valorCamp, stampPlantilla, parametresString);
				valorCamp = (!valorCampTractat.equals(valorCamp)) ? valorCampTractat : "";// sino
																							// s'ha
																							// modificat
																							// el
																							// contingut
																							// eliminem
																							// el
																							// tag
																							// en
																							// el
																							// form
				// una vegada modificar el valor del camp ho insertem al
				// formulari
				stampPlantilla.getAcroFields().setField(nomCamp, valorCamp);
			}
			if (matches(valorCamp, ComposicioPDFConstants.EXPRESSIO_REGULAR_TAG_CODIBARRES)) {
				logger.debug("[EstampatPDFImpl][tractarValorCamp] Es TAG CODIBARRES ");
				String valorCampTractat = tractarTagCodiBarres(valorCamp, parametresString);
				valorCamp = (!valorCampTractat.equals(valorCamp)) ? valorCampTractat : "";// sino
																							// s'ha
																							// modificat
																							// el
																							// contingut
																							// eliminem
																							// el
																							// tag
																							// en
																							// el
																							// form
				// una vegada modificar el valor del camp ho insertem al
				// formulari
				stampPlantilla.getAcroFields().setField(nomCamp, valorCamp);
			}
			if (matches(valorCamp, ComposicioPDFConstants.EXPRESSIO_REGULAR_TAG_IMATGE)) {
				logger.debug("[EstampatPDFImpl][tractarValorCamp] Es TAG IMATGE ");
				String valorCampTractat = tractarTagImatge(nomCamp, valorCamp, stampPlantilla, parametresImatge,
						numeroPaginaActual, numeroPaginesTotal);
				valorCamp = (!valorCampTractat.equals(valorCamp)) ? valorCampTractat : "";// sino
																							// s'ha
																							// modificat
																							// el
																							// contingut
																							// eliminem
																							// el
																							// tag
																							// en
																							// el
																							// form
				// una vegada modificar el valor del camp ho insertem al
				// formulari
				stampPlantilla.getAcroFields().setField(nomCamp, valorCamp);
			}

			// OLD: Millora, us de codis QR amb zxing
			if (esTagQR(valorCamp)) {
				logger.debug("[EstampatPDFImpl][tractarValorCamp] Es TAG QR ");
				QrDocument doc = QrDocument.Factory.parse(valorCamp);
				Qr tagQr = doc.getQr();
				String nom = tagQr.getNom().getStringValue();
				logger.debug("[EstampatPDFImpl][tractarValorCamp] Nom del tag QR: " + nom);
				String tipus = tagQr.getTipus().getStringValue();
				logger.debug("[EstampatPDFImpl][tractarValorCamp] Tipus del tag QR: " + tipus);

				byte[] imageQR = tractarTagsQR(nomCamp, valorCamp, stampPlantilla, parametresString, nom, tipus);
				substituirTagxQR(stampPlantilla, imageQR, nomCamp, tipus);
			}

			// OLD: Millora, us de codis Barcode 39 amb zxing
			// if(esTagBarcode39(valorCamp)){
			// BarcodeDocument doc = BarcodeDocument.Factory.parse(valorCamp);
			// Barcode tagBarcode = doc.getBarcode();
			// String nom = tagBarcode.getNom().getStringValue();
			// String longitud = tagBarcode.getLongitud().getStringValue();
			// String amplada = tagBarcode.getAmplada().getStringValue();
			// //String tipus = tagBarcode.getTipus().getStringValue();
			//
			// byte[] imageBarcode = tractarTagsBarcode39(nomCamp, valorCamp,
			// stampPlantilla, parametres, nom,
			// null, longitud, amplada);
			// substituirTagxBarceode39(stampPlantilla, imageBarcode, nomCamp,
			// null, longitud, amplada);
			// }

			// una vegada modificar el valor del camp ho insertem al formulari
			// STF-2619 <- esto deberia de ir fuera
			// stampPlantilla.getAcroFields().setField(nomCamp, valorCamp);
		} catch (IOException e) {
			logger.error(ERROR_TXT, e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_TXT);
		} catch (DocumentException e) {
			logger.error(ERROR_TXT, e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_TXT);
		} catch (Exception ex) {
			logger.error("ERROR al tractar l'Xml dels tags del document ", ex);
			throw new EstampatException(ex, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_TXT);
		}
	}

	/**
	 * M�tode que genera un codi de barres amb llenguatge de tipus 39 a partir
	 * d'un valor (nom) i amb una longitud i amplada determinats
	 * 
	 * @param nomCamp
	 * @param valorCamp
	 * @param stampPlantilla
	 * @param parametres
	 * @param nom
	 * @param tipus
	 * @param longitud
	 * @param amplada
	 * @return
	 */
	// private byte[] tractarTagsBarcode39(String nomCamp, String valorCamp,
	// PdfStamper stampPlantilla, Map<String, SegellBase> parametres,
	// String nom, String tipus, String longitud, String amplada) throws
	// EstampatException {
	//
	// comprovarValorCamp(valorCamp);
	// comprovarParametres(parametres);
	// Barcode39Codes barcodes = null;
	// try {
	// //Extracci� de la dada a transforma en QR (nom)
	// StringSegell segell = (StringSegell) parametres.get(nom);
	// //Generaci� del QR
	// barcodes = new Barcode39Codes();
	// byte[] imageBytes = barcodes.barcode39Generation(segell.getText(), tipus,
	// longitud, amplada);
	// return imageBytes;
	// } catch (Exception e) {
	// logger.error("ERROR al tractar el valor del camp del document ",e);
	// throw new EstampatException(e,"ERROR al tractar el valor del camp del
	// document",
	// ErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT);
	// }
	// }

	/**
	 * M�tode que genera el valor de la imatge del codi QR a partir del valor
	 * del tag recuperat de la petici�.
	 * 
	 * @param nomCamp
	 * @param valorCamp
	 * @param stampPlantilla
	 * @param parametresString
	 * @return
	 * @throws EstampatException
	 */
	private byte[] tractarTagsQR(String nomCamp, String valorCamp, PdfStamper stampPlantilla,
			Map<String, StringSegell> parametresString, String nom, String tipus) throws EstampatException {

		logger.debug("[EstampatPDFImpl][tractarTagsQR] Inici operacio ");
		comprovarValorCamp(valorCamp);
		logger.debug("[EstampatPDFImpl][tractarTagsQR] Valor camp comprovat");
		comprovarParametresString(parametresString);
		logger.debug("[EstampatPDFImpl][tractarTagsQR] Valor parametresString comprovat");
		try {
			// Extracci� de la dada a transforma en QR (nom)
			StringSegell segell = parametresString.get(nom);

			byte[] imageBytes = null;
			if (segell != null) {
				// Generaci� del QR
				imageBytes = qrCodes.qrGeneration(segell.getText(), tipus);
				logger.debug("[EstampatPDFImpl][tractarTagsQR] Fi operacio ");
			}
			return imageBytes;
		} catch (Exception e) {
			logger.error(ERROR_TXT, e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_TXT);
		}
	}

	/**
	 * M�tode que modifica el PdfStamper per inserir el QR all� on es trobi el
	 * tag original
	 * 
	 * @param stampPlantilla
	 * @param imageQR
	 * @param nomCamp
	 */
	private void substituirTagxQR(PdfStamper stampPlantilla, byte[] imageQR, String nomCamp, String tipus)
			throws EstampatException {
		logger.debug("[EstampatPDFImpl][substituirTagxQR] Inici operacio ");
		try {
			FieldPosition fieldPosition = stampPlantilla.getAcroFields().getFieldPositions(nomCamp).get(0);
			float[] posicio = new float[] { fieldPosition.page, fieldPosition.position.getLeft(), fieldPosition.position.getBottom(),
					fieldPosition.position.getRight(), fieldPosition.position.getTop() };
			stampPlantilla.getAcroFields().removeField(nomCamp);
			if (imageQR != null) {
				Image qrImage = Image.getInstance(imageQR);

				int anchura = 0;
				int altura = 0;
				if (Integer.parseInt(tipus) == 0) {
					anchura = 50;
					altura = 50;
				} else if (Integer.parseInt(tipus) == 1) {
					anchura = 100;
					altura = 100;
				} else if (Integer.parseInt(tipus) == 2) {
					anchura = 200;
					altura = 200;
				} else if (Integer.parseInt(tipus) == 3) {
					anchura = 300;
					altura = 300;
				} else {
					anchura = 50;
					altura = 50;
				}

				stampPlantilla.getOverContent(1).addImage(qrImage, anchura, 0, 0, altura, posicio[1], posicio[2], true);
			}
			logger.debug("[EstampatPDFImpl][substituirTagxQR] Fi operacio ");

		} catch (Exception e) {
			logger.error(ERROR_TXT, e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_TXT);
		}

	}

	/**
	 * M�tode que inserta un codi de barres representat per un array de bytes
	 * amb una amplada i una longitud
	 * 
	 * @param stampPlantilla
	 * @param imageBarcode
	 * @param nomCamp
	 * @param tipus
	 * @param longitud
	 * @param amplada
	 * @throws EstampatException
	 */
	// private void substituirTagxBarceode39(PdfStamper stampPlantilla,
	// byte[] imageBarcode, String nomCamp, String tipus, String longitud,
	// String amplada) throws EstampatException {
	// logger.debug("[EstampatPDFImpl][substituirTagxBarceode39] Inici operacio
	// ");
	// try {
	// float[] posicio =
	// stampPlantilla.getAcroFields().getFieldPositions(nomCamp);
	// stampPlantilla.getAcroFields().removeField(nomCamp);
	// Image barcodeImage = Image.getInstance(imageBarcode);
	//
	// stampPlantilla.getOverContent(1).addImage(barcodeImage,
	// Float.parseFloat(longitud), 0, 0,
	// Float.parseFloat(amplada), posicio[1], posicio[2], true);
	// logger.debug("[EstampatPDFImpl][substituirTagxBarceode39] Fi operacio ");
	// } catch (Exception e) {
	// logger.error("ERROR al tractar el valor del camp del document ",e);
	// throw new EstampatException(e,"ERROR al tractar el valor del camp del
	// document",
	// ErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT);
	// }
	//
	// }

	/**
	 * M�tode que determina si un tag es de tipus barcode39
	 * 
	 * @param valorCamp
	 * @return
	 */
	// private boolean esTagBarcode39(String valorCamp) {
	// try {
	// BarcodeDocument doc = BarcodeDocument.Factory.parse(valorCamp);
	// return true;
	// } catch (Exception e) {
	// return false;
	// }
	// }

	/**
	 * M�tode que determina si un tag es de tipus QR
	 * 
	 * @param valorCamp
	 * @param expressioRegularTagQr
	 * @return
	 */
	private boolean esTagQR(String valorCamp) {
		try {
			QrDocument doc = QrDocument.Factory.parse(valorCamp);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param valorCamp
	 * @param parametresString
	 * @return
	 * @throws EstampatException
	 */
	private String tractarTagCodiBarres(String valorCamp, Map<String, StringSegell> parametresString)
			throws EstampatException {
		logger.debug("[EstampatPDFImpl][tractarTagCodiBarres] Inici operacio ");

		comprovarValorCamp(valorCamp);
		logger.debug("[EstampatPDFImpl][tractarTagCodiBarres] Valor camp comprovat");
		comprovarParametresString(parametresString);
		logger.debug("[EstampatPDFImpl][tractarTagCodiBarres] Valor parametresString comprovat");

		String valorCampRetorn = valorCamp;

		int i = valorCamp.indexOf(ComposicioPDFConstants.OBERTURA_TAG_CODIBARRES);
		int l = getIndexOfFinalTag(valorCamp, i);

		String tag = valorCamp.substring(i, l);
		String nomParametre = getNomParameteTag(tag);

		StringSegell segell = parametresString.get(nomParametre);

		if (segell != null) {
			if (!conteCaractersValidsBarcodeC39(segell.getText())) {
				logger.error("ERROR el text del segell amb nom " + nomParametre
						+ " conte caracters invalids, caracters valids " + ComposicioPDFConstants.CHAR_SET_CODI_BARRES);
				throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
						"ERROR el text del segell amb nom " + nomParametre
								+ " conte caracters invalids, caracters valids "
								+ ComposicioPDFConstants.CHAR_SET_CODI_BARRES);
			}

			valorCampRetorn = ComposicioPDFConstants.CARACTER_OBERTURA_CODI_BARRES + ((StringSegell) segell).getText()
					+ ComposicioPDFConstants.CARACTER_TANCAMENT_CODI_BARRES;
		} else {
			valorCampRetorn = valorCamp.substring(0, i) + valorCamp.substring(l);
		}
		logger.debug("[EstampatPDFImpl][tractarTagCodiBarres] Valor camp retorn= " + valorCampRetorn);
		logger.debug("[EstampatPDFImpl][tractarTagCodiBarres] Fi operacio");
		return valorCampRetorn;
	}

	/**
	 * 
	 * @param valorCamp
	 * @param indexIniciTag
	 * @return
	 */
	private int getIndexOfFinalTag(String valorCamp, int indexIniciTag) {
		
		
		return valorCamp.substring(indexIniciTag).indexOf(ComposicioPDFConstants.TANCAMENT_TAG)
				+ ComposicioPDFConstants.TANCAMENT_TAG.length() + indexIniciTag; //NOSONAR
		
	}

	/**
	 * @param valorCamp
	 * @return
	 * @throws EstampatException
	 */
	private boolean conteCaractersValidsBarcodeC39(String valorCamp) throws EstampatException {
		comprovarValorCamp(valorCamp);

		boolean retorn = false;

		boolean noTrobatCaracterInvalid = false;
		int total = 0;
		CharacterIterator it = new StringCharacterIterator(valorCamp);
		for (char ch = it.current(); ch != CharacterIterator.DONE && !noTrobatCaracterInvalid; ch = it.next()) {
			int charValue = ComposicioPDFConstants.CHAR_SET_CODI_BARRES.indexOf(ch);
			if (charValue == -1) {
				// caracter inv�lid
				noTrobatCaracterInvalid = true;
			}
			total += charValue;
		}
		retorn = !noTrobatCaracterInvalid;

		return retorn;
	}

	/**
	 * @param nomCamp
	 * @param valorCamp
	 * @param stampPlantilla
	 * @param parametresString
	 * @return
	 * @throws EstampatException
	 */
	private String tractarTagsText(String nomCamp, String valorCamp, PdfStamper stampPlantilla,
			Map<String, StringSegell> parametresString) throws EstampatException {
		logger.debug("[EstampatPDFImpl][tractarTagsText] Inici operacio");

		comprovarValorCamp(valorCamp);
		logger.debug("[EstampatPDFImpl][tractarTagsText] Valor camp comprovat");
		comprovarParametresString(parametresString);
		logger.debug("[EstampatPDFImpl][tractarTagsText] Valor parametresString comprovat");

		String valorCampAnterior = valorCamp;
		do {
			valorCampAnterior = valorCamp;
			boolean match = matches(valorCamp, ComposicioPDFConstants.EXPRESSIO_REGULAR_TAG_TEXT);

			if (match) {
				// Si hi ha tags de text per tractar
				valorCamp = tractarTagText(valorCamp, parametresString);
			}
		} while (!valorCamp.equals(valorCampAnterior));

		return valorCamp;
	}

	private void comprovarParametresString(Map<String, StringSegell> parametresString) throws EstampatException {
		if (parametresString == null) {
			logger.error(ERROR_PARAMETRES);
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_PARAMETRES);
		}
	}

	/**
	 * 
	 * @param valorCamp
	 * @param expressioRegular
	 * @return
	 */
	private boolean matches(String valorCamp, String expressioRegular) {
		boolean resultat = false;

		if (valorCamp != null && expressioRegular != null) {
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
		}

		return resultat;
	}

	/**
	 * @param nomCamp
	 * @param valorCamp
	 * @param stampPlantilla
	 * @param parametresImatge
	 * @return
	 * @throws EstampatException
	 */
	private String tractarTagImatge(String nomCamp, String valorCamp, PdfStamper stampPlantilla,
			Map<String, ImatgeSegell> parametresImatge, int numeroPaginaActual, int numeroPaginesTotal)
			throws EstampatException {
		logger.debug("[EstampatPDFImpl][tractarTagImatge] Inici operacio");

		comprovarValorCamp(valorCamp);
		logger.debug("[EstampatPDFImpl][tractarTagImatge] Valor camp comprovat");
		comprovarParametresImatge(parametresImatge);
		logger.debug("[EstampatPDFImpl][tractarTagsText] Valor parametresString comprovat");

		String valorCampRetorn = valorCamp;

		int i = valorCamp.indexOf(ComposicioPDFConstants.OBERTURA_TAG_IMATGE);
		int l = getIndexOfFinalTag(valorCamp, i);

		String tag = valorCamp.substring(i, l);

		String nomParametre = getNomParameteTag(tag);
		String rotacio = getValorAtribut(tag, ComposicioPDFConstants.ATRIBUT_ROTACIO_TAG);
		String pagines = getValorAtribut(tag, ComposicioPDFConstants.ATRIBUT_PAGINES_TAG);
		if (parametresImatge != null) {
			ImatgeSegell segell = parametresImatge.get(nomParametre);

			if (segell != null) {
				if (insertarImatgeSegonsPagines(pagines, numeroPaginaActual, numeroPaginesTotal)) {
					insertarImatgeEnElFormulari(stampPlantilla, nomCamp, segell, rotacio);
				}

				valorCampRetorn = "";// insertem a tot el formulari la imatge
			} else {
				valorCampRetorn = valorCamp.substring(0, i) + valorCamp.substring(l);// sin�
																						// s'ha
																						// trobat
																						// valor
																						// per
																						// la
																						// imatge,
																						// eliminem
																						// el
																						// tag
																						// i
																						// deixem
																						// la
																						// resta
																						// de
																						// text
																						// del
																						// formulari
			}
		} else {
			valorCampRetorn = valorCamp.substring(0, i) + valorCamp.substring(l);
		}
		logger.debug("[EstampatPDFImpl][tractarTagImatge] Valor camp retorn= " + valorCampRetorn);
		logger.debug("[EstampatPDFImpl][tractarTagImatge] Fi operacio");
		return valorCampRetorn;
	}

	private void comprovarParametresImatge(Map<String, ImatgeSegell> parametresImatge) throws EstampatException {
		if (parametresImatge == null) {
			logger.error(ERROR_PARAMETRES);
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_PARAMETRES);
		}
	}

	/**
	 * @param pagines
	 * @param numeroPaginaActual
	 * @param numeroPaginesTotal
	 * @return
	 */
	private boolean insertarImatgeSegonsPagines(String pagines, int numeroPaginaActual, int numeroPaginesTotal) {
		boolean resultat = false;

		if (pagines == null) {
			resultat = true;// si latribut no esta informat
		} else if (pagines.equals(ComposicioPDFConstants.ATRIBUT_PAGINES_PRIMERA) && numeroPaginaActual == 1) {
			resultat = true;
		} else if (pagines.equals(ComposicioPDFConstants.ATRIBUT_PAGINES_ULTIMA)
				&& numeroPaginaActual == numeroPaginesTotal) {
			resultat = true;
		}

		return resultat;
	}

	/**
	 * @param valorCamp
	 * @param parametresString
	 * @return
	 * @throws EstampatException
	 */
	private String tractarTagText(String valorCamp, Map<String, StringSegell> parametresString)
			throws EstampatException {
		logger.debug("[EstampatPDFImpl][tractarTagText] Inici operacio");

		String valorCampRetorn = valorCamp;

		int i = valorCamp.indexOf(ComposicioPDFConstants.OBERTURA_TAG_TEXT);
		int l = getIndexOfFinalTag(valorCamp, i);

		String tag = valorCamp.substring(i, l);
		String nomParametre = getNomParameteTag(tag);

		StringSegell segell = parametresString.get(nomParametre);

		if (segell != null) {
			valorCampRetorn = valorCamp.substring(0, i) + segell.getText() + valorCamp.substring(l);
		} else {
			valorCampRetorn = valorCamp.substring(0, i) + valorCamp.substring(l);
		}
		logger.debug("[EstampatPDFImpl][tractarTagText] Valor camp retorn= " + valorCampRetorn);
		logger.debug("[EstampatPDFImpl][tractarTagText] Fi operacio");
		return valorCampRetorn;
	}

	/**
	 * @param tag
	 * @return
	 * @throws EstampatException
	 */
	private String getNomParameteTag(String tag) throws EstampatException {
		comprovarTag(tag);

		return getValorAtribut(tag, ComposicioPDFConstants.ATRIBUT_NOM_TAG);
	}

	/**
	 * @param linia
	 * @param nomAtribut
	 * @return
	 * @throws EstampatException
	 */
	private String getValorAtribut(String linia, String nomAtribut) throws EstampatException {
		String resultat = null;

		comprovarTag(linia);
		comprovarNomAtribut(nomAtribut);

		int i = linia.indexOf(nomAtribut);
		if (i > 0) {
			String atribut = linia.substring(i);

			i = atribut.indexOf("\"");
			String opacitatString = atribut.substring(i + 1);

			i = opacitatString.indexOf("\"");
			resultat = opacitatString.substring(0, i);
		}
		return resultat;
	}

	/**
	 * @param stampPlantilla
	 * @param clauCamp
	 * @param imatgeSegell
	 * @param rotacioString
	 * @throws EstampatException
	 */
	private void insertarImatgeEnElFormulari(PdfStamper stampPlantilla, String clauCamp, ImatgeSegell imatgeSegell,
			String rotacioString) throws EstampatException {
		logger.debug("[EstampatPDFImpl][insertarImatgeEnElFormulari] Inici operacio");
		try {
			comprovarPdfStamper(stampPlantilla);
			logger.debug("[EstampatPDFImpl][insertarImatgeEnElFormulari] PDF stamper comprovat");
			comprovarClauCamp(clauCamp);
			logger.debug("[EstampatPDFImpl][insertarImatgeEnElFormulari] Clau camp comprovat");
			comprovarImatgeSegell(imatgeSegell);
			logger.debug("[EstampatPDFImpl][insertarImatgeEnElFormulari] Imatge segell comprovat");

			float rotacio = getRotacio(rotacioString);
			logger.debug("[EstampatPDFImpl][insertarImatgeEnElFormulari] Rotacio recuperada");

			float[] posicioCamp = getPosicioCamp(stampPlantilla, clauCamp);
			logger.debug("[EstampatPDFImpl][insertarImatgeEnElFormulari] Posicio camp recuperada");
			Rectangle rect = new Rectangle(posicioCamp[1], posicioCamp[2], posicioCamp[3], posicioCamp[4]);

			Image img = Image.getInstance(imatgeSegell.getContingut());
			img.setRotationDegrees(rotacio);
			if (esImatgeMesGranTamany(img, rect))
				img.scaleToFit(rect.getWidth(), rect.getHeight());
			img.setAbsolutePosition(posicioCamp[1] + (rect.getWidth() - img.getScaledWidth()) / 2,
					posicioCamp[2] + (rect.getHeight() - img.getScaledHeight()) / 2);

			PdfContentByte cb = stampPlantilla.getOverContent((int) posicioCamp[0]);
			cb.addImage(img);
			logger.debug("[EstampatPDFImpl][insertarImatgeEnElFormulari] Image adicionada");
			logger.debug("[EstampatPDFImpl][insertarImatgeEnElFormulari] Fi operacio");

		} catch (BadElementException e) {
			logger.error(ERROR_IMAGES, e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_TXT);
		} catch (MalformedURLException e) {
			logger.error(ERROR_IMAGES, e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_TXT);
		} catch (IOException e) {
			logger.error(ERROR_IMAGES, e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_TXT);
		} catch (DocumentException e) {
			logger.error(ERROR_IMAGES, e);
			throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_TXT);
		}

	}

	/**
	 * 
	 * @param rotacioString
	 * @return
	 * @throws EstampatException
	 */
	private float getRotacio(String rotacioString) throws EstampatException {
		float resultat = 0;
		if (rotacioString != null) {
			try {
				resultat = Float.valueOf(rotacioString);

				if (resultat < 0 || resultat > 360) {
					logger.error("ERROR rotacio de la imatge fora de rang");
					throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
							"ERROR rotacio de la imatge fora de rang");
				}
			} catch (Exception e) {
				logger.error("ERROR al obtenir el el valor de la rotacio de la imatge ", e);
				throw new EstampatException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
						"ERROR al obtenir el el valor de la rotacio de la imatge");
			}
		}
		return resultat;
	}

	/**
	 * @param img
	 * @param rect
	 * @return
	 * @throws EstampatException
	 */
	private boolean esImatgeMesGranTamany(Image img, Rectangle rect) throws EstampatException {
		comprovarImatge(img);
		comprovarRectagle(rect);

		return (img.getWidth() > rect.getWidth() || img.getHeight() > rect.getHeight());
	}

	/**
	 * @param stampPlantilla
	 * @param clauCamp
	 * @return
	 * @throws EstampatException
	 */
	private float[] getPosicioCamp(PdfStamper stampPlantilla, String clauCamp) throws EstampatException {
		comprovarPdfStamper(stampPlantilla);
		comprovarClauCamp(clauCamp);

		FieldPosition fieldPosition = stampPlantilla.getAcroFields().getFieldPositions(clauCamp).get(0);
		return new float[] { fieldPosition.page, fieldPosition.position.getLeft(), fieldPosition.position.getBottom(),
				fieldPosition.position.getRight(), fieldPosition.position.getTop() };
		
	}

	/**
	 * @param rect
	 * @throws EstampatException
	 */
	private void comprovarRectagle(Rectangle rect) throws EstampatException {
		if (rect == null) {
			logger.error("ERROR rectangle null");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR rectangle null");
		}
	}

	/**
	 * @param img
	 * @throws EstampatException
	 */
	private void comprovarImatge(Image img) throws EstampatException {
		if (img == null) {
			logger.error("ERROR imatge null");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR imatge null");
		}
	}

	/**
	 * @param imatgeSegell
	 * @throws EstampatException
	 */
	private void comprovarImatgeSegell(ImatgeSegell imatgeSegell) throws EstampatException {
		if (imatgeSegell == null) {
			logger.error("ERROR imatge segell null");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR imatge segell null");
		}
	}

	/**
	 * @param clauCamp
	 * @throws EstampatException
	 */
	private void comprovarClauCamp(String clauCamp) throws EstampatException {
		if (clauCamp == null || (clauCamp != null && "".equals(clauCamp.trim()))) {
			logger.error("ERROR clau camp null");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR clau camp null");
		}
	}

	/**
	 * @param tag
	 * @throws EstampatException
	 */
	private void comprovarTag(String tag) throws EstampatException {
		if (tag == null) {
			logger.error("ERROR valor del tag null");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR valor del tag null");
		}
	}

	/**
	 * @param valorCamp
	 * @throws EstampatException
	 */
	private void comprovarValorCamp(String valorCamp) throws EstampatException {
		if (valorCamp == null) {
			logger.error("ERROR valor del camp null");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR valor del camp null");
		}
	}

	/**
	 * @param parametresString
	 * @param parametresImatge
	 * @throws EstampatException
	 */
	private void comprovarParametres(Map<String, StringSegell> parametresString,
			Map<String, ImatgeSegell> parametresImatge) throws EstampatException {
		if (parametresString == null && parametresImatge == null) {
			logger.error(ERROR_PARAMETRES);
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					ERROR_PARAMETRES);
		}
	}

	/**
	 * @param stampPlantilla
	 * @throws EstampatException
	 */
	private void comprovarPdfStamper(PdfStamper stampPlantilla) throws EstampatException {
		if (stampPlantilla == null) {
			logger.error("ERROR stampPlantilla null");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESTAMPANT_DOCUMENT,
					"ERROR stampPlantilla null");
		}
	}

	/**
	 * @param nomAtribut
	 * @throws EstampatException
	 */
	private void comprovarNomAtribut(String nomAtribut) throws EstampatException {
		if (nomAtribut == null || (nomAtribut != null && "".equals(nomAtribut.trim()))) {
			logger.error("ERROR nom del atribut null");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR nom del atribut null");
		}
	}

	/**
	 * @param nomCamp
	 * @throws EstampatException
	 */
	private void comprovarNomCamp(String nomCamp) throws EstampatException {
		if (nomCamp == null || (nomCamp != null && "".equals(nomCamp.trim()))) {
			logger.error("ERROR nom del camp null");
			throw new EstampatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR nom del camp null");
		}
	}

}
