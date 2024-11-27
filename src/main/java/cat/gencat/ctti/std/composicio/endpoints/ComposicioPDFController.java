package cat.gencat.ctti.std.composicio.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.gencat.ctti.std.composicio.dto.ComposarPDFRemot;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFStream;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.composicio.services.ComposicioPDFService;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.ctti.std.dto.ResultSTD;
import cat.gencat.ctti.std.endpoints.STDBaseController;

@RestController
@RequestMapping(ComposicioPDFController.COMPOSICIO_PDF_URI_PATH)
public class ComposicioPDFController implements STDBaseController{

	@Autowired
	ComposicioPDFService composicioPDFService;

	@PostMapping(value = COMPOSAR_PDF_REMOT_URI_PATH, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResultSTD composarPDFRemot(@RequestParam(value = "uuid", required = false) String uuid,
			@RequestParam(value = "ambit") String ambit, @RequestParam(value = "aplicacio") String aplicacio,
			@RequestBody ComposarPDFRemot composarPDFRemot) throws ComposicioPDFException {
		return composicioPDFService.composarPDFRemot(new ConfigCall(uuid, ambit, aplicacio), composarPDFRemot);
	}

	@PostMapping(value = COMPOSAR_PDF_STREAM_URI_PATH, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResultSTD composarPDFStream(@RequestParam(value = "uuid", required = false) String uuid,
			@RequestParam(value = "ambit") String ambit, @RequestParam(value = "aplicacio") String aplicacio,
			@RequestBody ComposarPDFStream composarPDFStream) throws ComposicioPDFException {
		return composicioPDFService.composarPDFStream(new ConfigCall(uuid, ambit, aplicacio), composarPDFStream);
	}

}
