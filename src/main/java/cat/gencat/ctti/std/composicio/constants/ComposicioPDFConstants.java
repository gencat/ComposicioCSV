/**
 * 
 */
package cat.gencat.ctti.std.composicio.constants;

import cat.gencat.ctti.std.constants.STDConstantsBase;


/**
 * @author A163617
 *
 */
public class ComposicioPDFConstants extends STDConstantsBase {
	
	public static final String CLAU_PROPIETAT_PATH_FITXER_PLANTILLA_COMPOSICIO = "composicio.path.fitxer.plantilla";
	
	//Variables per emmagatzemar la mida màxima que poden fer els arxius segons la operació i el tipus (en Kbytes)
	public static final String CLAU_PROPIETAT_MIDA_MAX_COMPOSICIO_REMOT = "mida.max.composicio.remot"; //"1024";
	public static final String CLAU_PROPIETAT_MIDA_MAX_COMPOSICIO_STREAM = "mida.max.composicio.stream"; //"1024";
	
	
	public static final String FINAL_PLANTILLA_MARCA_AIGUA = "_marca_aigua";
	public static final String PLANTILLA_HORITZONTAL = "_horitzontal";

	public static final String NOM_VARIABLE_RESERVADA_NUMERO_PAGINA = "numeroPagina";
	public static final String NOM_VARIABLE_RESERVADA_TOTAL_PAGINES = "totalPagines";
	
	public static final String CARACTER_OBERTURA_CODI_BARRES = "*";
	public static final String CARACTER_TANCAMENT_CODI_BARRES = "*";
	public static final String CHAR_SET_CODI_BARRES = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%";
	
	public static final String OBERTURA_TAG = "<";
	public static final String TANCAMENT_TAG = "/>";
	public static final String OBERTURA_PARENTESIS = "(";
	public static final String TANCAMENT_PARENTESIS = ")";
	public static final String EXPRESSIO_REGULAR_INICI_LINIA = "^";
	public static final String EXPRESSIO_REGULAR_FINAL_LINIA = "$";
	public static final String EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC = "\\s*";
	public static final String EXPRESSIO_REGULAR_ESPAIS_BLANC = "\\s+";
	public static final String EXPRESSIO_REGULAR_POSSIBLE_CARACTERS = ".*";
	public static final String EXPRESSIO_REGULAR_ALGUN_CARACTER = ".+";
	public static final String EXPRESSIO_REGULAR_ALGUNA_PARAULA = "\\w+";
	public static final String EXPRESSIO_REGULAR_COMETESDOBLES = "\"";
	public static final String EXPRESSIO_REGULAR_IGUAL = "=";
	public static final String EXPRESSIO_REGULAR_ZERO_O_ALGUN = "*";
	public static final String EXPRESSIO_REGULAR_OR = "|";
	public static final String ATRIBUT_NOM_TAG = "nom";
	//Tag document: comen�ament de linia, possibles espais en blanc, obertura de tag <document, possibles espais en blanc,
	//				tancament de tag />, possibles espais en blanc i final de linia
	public static final String NOM_TAG_DOCUMENT = "document";
	public static final String OBERTURA_TAG_DOCUMENT = OBERTURA_TAG+NOM_TAG_DOCUMENT;
	public static final String EXPRESSIO_REGULAR_TAG_DOCUMENT = EXPRESSIO_REGULAR_INICI_LINIA+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+OBERTURA_TAG_DOCUMENT+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+TANCAMENT_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_FINAL_LINIA;
	
	//Tag marca aigua: comen�ament de linia, possibles espais en blanc, obertura de tag <marcaAigua, espais en blanc,
	//					atribut opacitat, possibles espais en blanc, un igual =, possibles espais en blanc, obertura de comentes dobles ", 
	//					1,0 o 0. algun decimal tancament d'atribut amb cometes dobles ", possibles espais en blanc, tancament de tag />, 
	//					possibles espais en blanc i final de linia
	public static final String NOM_TAG_MARCAAIGUA = "marcaAigua";
	public static final String OBERTURA_TAG_MARCAAIGUA = OBERTURA_TAG+NOM_TAG_MARCAAIGUA;
	public static final String ATRIBUT_OPACITAT_TAG = "opacitat";
	public static final String POSSIBLES_VALORS_ATRIBUT_OPACITAT_TAG = "(1|0|0.[0-9]+)";
	public static final String EXPRESSIO_REGULAR_TAG_MARCAAIGUA = EXPRESSIO_REGULAR_INICI_LINIA+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+OBERTURA_TAG_MARCAAIGUA+EXPRESSIO_REGULAR_ESPAIS_BLANC+ATRIBUT_OPACITAT_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_IGUAL+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_COMETESDOBLES+POSSIBLES_VALORS_ATRIBUT_OPACITAT_TAG+EXPRESSIO_REGULAR_COMETESDOBLES+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+TANCAMENT_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_FINAL_LINIA;
	
	//Tag text: entre alguna linia, possibles espais en blanc, obertura de tag <text, espais en blanc,
	//			atribut nom, possibles espais en blanc, un igual =, possibles espais en blanc, obertura de comentes dobles ", 
	//			alguna paraula com a m�nim una, tancament d'atribut amb cometes dobles ", possibles espais en blanc, 
	//			tancament de tag />, possibles espais en blanc, entre alguna linia
	public static final String NOM_TAG_TEXT = "text";
	public static final String OBERTURA_TAG_TEXT = OBERTURA_TAG+NOM_TAG_TEXT;
	public static final String EXPRESSIO_REGULAR_TAG_TEXT = EXPRESSIO_REGULAR_POSSIBLE_CARACTERS+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+OBERTURA_TAG_TEXT+EXPRESSIO_REGULAR_ESPAIS_BLANC+ATRIBUT_NOM_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_IGUAL+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_COMETESDOBLES+EXPRESSIO_REGULAR_ALGUNA_PARAULA+EXPRESSIO_REGULAR_COMETESDOBLES+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+TANCAMENT_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_POSSIBLE_CARACTERS;
	
	//Tag imatge: 	entre alguna linia, possibles espais en blanc, obertura de tag <img, espais en blanc,
	//				atribut nom, possibles espais en blanc, un igual =, possibles espais en blanc, obertura de comentes dobles ", 
	//				alguna paraula com a m�nim una, tancament d'atribut amb cometes dobles ", 
	//				possible atribut rotacio amb espais en blanc, atribut rotacio, possibles espais en blanc, un igual =, possibles espais en blanc, obertura de comentes dobles ", 
	//				algun n�mero com a m�nim un, tancament d'atribut amb cometes dobles ", 
	//				possible atribut pagines amb espais en blanc, atribut pagines, possibles espais en blanc, un igual =, possibles espais en blanc, obertura de comentes dobles ",
	//				valor atribut ultima o primera, tancament d'atribut amb cometes dobles ",
	//				possibles espais en blanc, tancament de tag />, possibles espais en blanc, entre alguna linia
	public static final String NOM_TAG_IMATGE = "img";
	public static final String OBERTURA_TAG_IMATGE = OBERTURA_TAG+NOM_TAG_IMATGE;
	public static final String ATRIBUT_ROTACIO_TAG = "rotacio";
	public static final String POSSIBLES_VALORS_ATRIBUT_ROTACIO_TAG = "[0-9]+";
	public static final String EXPRESSIO_REGULAR_POSSIBLE_TAG_ROTACIO = OBERTURA_PARENTESIS+EXPRESSIO_REGULAR_ESPAIS_BLANC+ATRIBUT_ROTACIO_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_IGUAL+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_COMETESDOBLES+POSSIBLES_VALORS_ATRIBUT_ROTACIO_TAG+EXPRESSIO_REGULAR_COMETESDOBLES+TANCAMENT_PARENTESIS+EXPRESSIO_REGULAR_ZERO_O_ALGUN;
	public static final String ATRIBUT_PAGINES_TAG = "pagines";
	public static final String ATRIBUT_PAGINES_ULTIMA = "ultima";
	public static final String ATRIBUT_PAGINES_PRIMERA = "primera";
	public static final String POSSIBLES_VALORS_ATRIBUT_PAGINES_TAG = ATRIBUT_PAGINES_ULTIMA+EXPRESSIO_REGULAR_OR+ATRIBUT_PAGINES_PRIMERA;
	public static final String EXPRESSIO_REGULAR_POSSIBLE_TAG_PAGINES = OBERTURA_PARENTESIS+EXPRESSIO_REGULAR_ESPAIS_BLANC+ATRIBUT_PAGINES_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_IGUAL+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_COMETESDOBLES+OBERTURA_PARENTESIS+POSSIBLES_VALORS_ATRIBUT_PAGINES_TAG+TANCAMENT_PARENTESIS+EXPRESSIO_REGULAR_COMETESDOBLES+TANCAMENT_PARENTESIS+EXPRESSIO_REGULAR_ZERO_O_ALGUN;
	public static final String EXPRESSIO_REGULAR_TAG_IMATGE = EXPRESSIO_REGULAR_POSSIBLE_CARACTERS+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+OBERTURA_TAG_IMATGE+EXPRESSIO_REGULAR_ESPAIS_BLANC+ATRIBUT_NOM_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_IGUAL+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_COMETESDOBLES+EXPRESSIO_REGULAR_ALGUNA_PARAULA+EXPRESSIO_REGULAR_COMETESDOBLES+EXPRESSIO_REGULAR_POSSIBLE_TAG_ROTACIO+EXPRESSIO_REGULAR_POSSIBLE_TAG_PAGINES+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+TANCAMENT_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_POSSIBLE_CARACTERS;
	
	//Tag codi barres: entre alguna linia, possibles espais en blanc, obertura de tag <codiBarres, espais en blanc,
	//			atribut nom, possibles espais en blanc, un igual =, possibles espais en blanc, obertura de comentes dobles ", 
	//			alguna paraula com a m�nim una, tancament d'atribut amb cometes dobles ", possibles espais en blanc, 
	//			atribut tipus, possibles espais en blanc, un igual =, possibles espais en blanc, obertura de comentes dobles ", 
	//			alguna paraula com a m�nim una, tancament d'atribut amb cometes dobles ", possibles espais en blanc, tancament de tag />, possibles espais en blanc, entre alguna linia
	public static final String NOM_TAG_CODIBARRES = "codiBarres";
	public static final String OBERTURA_TAG_CODIBARRES = OBERTURA_TAG+NOM_TAG_CODIBARRES;
	public static final String ATRIBUT_TIPUS_TAG = "tipus";
	public static final String EXPRESSIO_REGULAR_TAG_CODIBARRES = EXPRESSIO_REGULAR_POSSIBLE_CARACTERS+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+OBERTURA_TAG_CODIBARRES+EXPRESSIO_REGULAR_ESPAIS_BLANC+ATRIBUT_NOM_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_IGUAL+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_COMETESDOBLES+EXPRESSIO_REGULAR_ALGUNA_PARAULA+EXPRESSIO_REGULAR_COMETESDOBLES+EXPRESSIO_REGULAR_ESPAIS_BLANC+ATRIBUT_TIPUS_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_IGUAL+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_COMETESDOBLES+EXPRESSIO_REGULAR_ALGUNA_PARAULA+EXPRESSIO_REGULAR_COMETESDOBLES+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+TANCAMENT_TAG+EXPRESSIO_REGULAR_POSSIBLES_ESPAIS_BLANC+EXPRESSIO_REGULAR_POSSIBLE_CARACTERS;
	
}

