package cat.gencat.ctti.std.composicio.estampat.qr;

/**
 * 
 * @author CS Canig�
 *
 */
public interface QuickRevisionCodes {
 
	
	/**
	 * M�tode que genera en una imatge, el codi QR que representa les dades emmagatzemades en nom
	 * @param nom
	 * @param tipus
	 * @return
	 */
	public byte[] qrGeneration(String nom, String tipus);
}
