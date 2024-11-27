package cat.gencat.ctti.std.csv.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.gencat.ctti.std.csv.dto.GenerarCSV;
import cat.gencat.ctti.std.csv.dto.GenerarCSVTimeStamp;
import cat.gencat.ctti.std.csv.exceptions.CodiSegurVerificacioException;
import cat.gencat.ctti.std.csv.services.CodiSegurVerificacioService;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.ctti.std.dto.ResultSTD;
import cat.gencat.ctti.std.endpoints.STDBaseController;

@RestController
@RequestMapping(CodiSegurVerificacioController.CODI_SEGUR_VERIFICACIO_URI_PATH)
public class CodiSegurVerificacioController implements STDBaseController {

	@Autowired
	CodiSegurVerificacioService codiSegurVerificacioService;

	@PostMapping(value = GENERAR_CSV_URI_PATH, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResultSTD generarCSV(@RequestParam(value = "uuid", required = false) String uuid,
			@RequestParam(value = "ambit") String ambit, @RequestParam(value = "aplicacio") String aplicacio,
			@RequestBody GenerarCSV generarCSV) throws CodiSegurVerificacioException {
		return codiSegurVerificacioService.generarCSV(new ConfigCall(uuid, ambit, aplicacio), generarCSV);
	}

	@PostMapping(value = GENERAR_CSV_TIMESTAMP_URI_PATH, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResultSTD generarCSVTimeStamp(@RequestParam(value = "uuid", required = false) String uuid,
			@RequestParam(value = "ambit") String ambit, @RequestParam(value = "aplicacio") String aplicacio,
			@RequestBody GenerarCSVTimeStamp generarCSVTimeStamp) throws CodiSegurVerificacioException {
		return codiSegurVerificacioService.generarCSVTimeStamp(new ConfigCall(uuid, ambit, aplicacio),
				generarCSVTimeStamp);
	}

}
