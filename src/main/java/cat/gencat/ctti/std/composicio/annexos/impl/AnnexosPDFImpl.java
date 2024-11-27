package cat.gencat.ctti.std.composicio.annexos.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cat.gencat.ctti.canigo.eforms.IServeisSOAPv1;
import cat.gencat.ctti.canigo.eforms.ServeisSOAPImplv1Service;
import cat.gencat.ctti.std.composicio.annexos.AnnexosPDF;
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.forms.webservice.ResultAnnexos;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.commons.io.FileUtils;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;

@Component
public class AnnexosPDFImpl implements AnnexosPDF {

	private final Logger logger = LoggerFactory.getLogger(AnnexosPDFImpl.class);

	@Autowired
	private IServeisSOAPv1 serviceEfomularis;

	@Override
	public ResultAnnexos getAnnexos(ConfigCall config, InputStream inputStream) throws ComposicioPDFException {
		ResultAnnexos annexos = null;
		logger.debug("[ComposicioPDFServiceImpl][getAnnexos] Inici get Annexos");

		try {
			if (teAnnexos(inputStream)) {
				// Extraiem els annexos
				annexos = extreureAnnexos(config, inputStream);
			}
		} catch (Exception ex) {
			if (!(ex instanceof ComposicioPDFException)) {
				logger.error("ERROR en l'aplanat ", ex);
				throw new ComposicioPDFException(ex, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						"ERROR en l'aplanat");
			} else {
				throw (ComposicioPDFException) ex;
			}
		} finally {
			logger.debug("[ComposicioPDFServiceImpl][getAnnexos] Fi get Annexos");
			try {
				inputStream.reset();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return annexos;
	}

	private boolean teAnnexos(InputStream inputStream) throws IOException {

		return true;
		/*
		 * PdfReader reader = new PdfReader(inputStream);
		 * PdfDictionary root = reader.getCatalog();
		 * PdfDictionary names = root.getAsDict(PdfName.NAMES);
		 * 
		 * if (names == null)
		 * return false;
		 * 
		 * PdfDictionary embeddedFiles = names.getAsDict(PdfName.EMBEDDEDFILES);
		 * 
		 * if (embeddedFiles == null)
		 * return false;
		 * 
		 * boolean exists = embeddedFiles.size() > 0;
		 * return exists;
		 */
	}

	/*
	 * private boolean teAnnexos(InputStream inputStream) throws
	 * InvalidPasswordException, IOException {
	 * boolean result = false;
	 * logger.debug("[ComposicioPDFServiceImpl][teAnnexos] Inici te Annexos");
	 * 
	 * PDDocument document = null;
	 * try {
	 * document = PDDocument.load(inputStream,
	 * MemoryUsageSetting.setupTempFileOnly().setTempDir(FileUtils.getTempDirectory(
	 * )));
	 * PDDocumentNameDictionary namesDictionary = new PDDocumentNameDictionary(
	 * document.getDocumentCatalog() );
	 * PDEmbeddedFilesNameTreeNode efTree = namesDictionary.getEmbeddedFiles();
	 * 
	 * if (efTree != null){
	 * Map<String, PDComplexFileSpecification> names = efTree.getNames();
	 * if (names != null) {
	 * result = true;
	 * } else {
	 * List<PDNameTreeNode<PDComplexFileSpecification>> kids = efTree.getKids();
	 * if (kids != null){
	 * for (PDNameTreeNode<PDComplexFileSpecification> node : kids) {
	 * names = node.getNames();
	 * if (names != null){
	 * result = true;
	 * }
	 * }
	 * }
	 * }
	 * }
	 * 
	 * if(!result) {
	 * // extract files from annotations
	 * for (PDPage page : document.getPages()) {
	 * for (PDAnnotation annotation : page.getAnnotations()) {
	 * if (annotation instanceof PDAnnotationFileAttachment) {
	 * PDAnnotationFileAttachment annotationFileAttachment =
	 * (PDAnnotationFileAttachment) annotation;
	 * PDFileSpecification fileSpec = annotationFileAttachment.getFile();
	 * if (fileSpec instanceof PDComplexFileSpecification) {
	 * result = true;
	 * }
	 * }
	 * }
	 * }
	 * }
	 * } finally {
	 * logger.debug("[ComposicioPDFServiceImpl][teAnnexos] Fi te Annexos");
	 * 
	 * inputStream.reset();
	 * 
	 * if(document != null) {
	 * document.close();
	 * }
	 * }
	 * 
	 * 
	 * return result;
	 * 
	 * }
	 */

	/**
	 * 
	 * @param config
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private ResultAnnexos extreureAnnexos(ConfigCall config, InputStream inputStream) throws IOException {
		ResultAnnexos annexos = null;
		logger.debug("[ComposicioPDFServiceImpl][extreureAnnexos] Inici extreure Annexos");

		try {
			annexos = serviceEfomularis.extreureAnnexos(UUID.randomUUID().toString(), config.getAmbit(),
					config.getAplicacio(), IOUtils.toByteArray(inputStream));
			ClientProxy.getClient(serviceEfomularis).getResponseContext().clear();
		} finally {
			logger.debug("[ComposicioPDFServiceImpl][extreureAnnexos] Fi extreure Annexos");
			inputStream.reset();
		}

		return annexos;
	}

}
