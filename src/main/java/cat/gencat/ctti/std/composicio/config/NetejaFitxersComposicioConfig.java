/*******************************************************************************
 * Copyright 2016 Generalitat de Catalunya.
 *
 * The contents of this file may be used under the terms of the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence"); You may not use this work except in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1 Unless required by applicable law or agreed to in writing, software distributed under the
 * Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations under the Licence.
 *
 * Original authors: Centre de Suport Canigó Contact: oficina-tecnica.canigo.ctti@gencat.cat
 *******************************************************************************/
package cat.gencat.ctti.std.composicio.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import cat.gencat.ctti.std.neteja.fitxers.STDNetejaFitxers;
import cat.gencat.ctti.std.neteja.fitxers.STDNetejaFitxersPlanificador;
import cat.gencat.ctti.std.neteja.fitxers.impl.STDNetejaFitxersImpl;
import cat.gencat.ctti.std.neteja.fitxers.impl.STDNetejaFitxersPlanificadorImpl;

@Configuration
public class NetejaFitxersComposicioConfig {

	@Value("${neteja.fitxers.composicio.directori.base}")
	private String nomDirectoriBaseNetejaFitxers;
	@Value("#{'${neteja.fitxers.composicio.directoris.exclosos.nom}'.split(',')}")
	private List<String> directorisExclososNomNetejaFitxers;
	@Value("#{'${neteja.fitxers.composicio.directoris.inclosos.nom}'.split(',')}")
	private List<String> directorisInclososNomNetejaFitxers;
	@Value("#{'${neteja.fitxers.composicio.fitxers.exclosos}'.split(',')}")
	private List<String> fitxersExclososNetejaFitxers;
	@Value("${neteja.fitxers.composicio.temps.vida.fitxers.minuts}")
	private int tempsVidaFitxersNetejaFitxers;
	@Value("${neteja.fitxers.composicio.delete.files:false}")
	private boolean deleteFilesNetejaFitxers;

	@Value("${neteja.fitxers.composicio.path.token.planificador}")
	private String pathTokenPlanificador;
	@Value("${neteja.fitxers.composicio.temps.token.planificador}")
	private long tempsTokenPlanificador;

	@Value("${neteja.fitxers.composicio.job.concurrent:false}")
	private boolean jobConcurrent;
	@Value("${neteja.fitxers.composicio.quartz.cronExpression}")
	private String quartzCronExpression;
	@Value("${neteja.fitxers.composicio.scheduler.auto.startup:false}")
	private boolean autoStartup;

	@Bean("netejaFitxersComposicio")
	public STDNetejaFitxers getNetejaFitxersComposicio() {

		STDNetejaFitxersImpl neteja = null;
		try {
			neteja = new STDNetejaFitxersImpl(nomDirectoriBaseNetejaFitxers, directorisExclososNomNetejaFitxers,
			directorisInclososNomNetejaFitxers, fitxersExclososNetejaFitxers, tempsVidaFitxersNetejaFitxers,
			deleteFilesNetejaFitxers);
		} catch (Exception e) {}		

		return neteja;
	}

	@Bean("netejaFitxersComposicioPlanificador")
	@Autowired
    @Qualifier("netejaFitxersComposicio")
	public STDNetejaFitxersPlanificador getNetejaFitxersComposicioPlanificador(STDNetejaFitxers netejaFitxersComposicio) {
		return new STDNetejaFitxersPlanificadorImpl(pathTokenPlanificador, tempsTokenPlanificador,
				netejaFitxersComposicio);
	}
	
	@Bean("netejaFitxersComposicioJob")
	@Autowired
    @Qualifier("netejaFitxersComposicioPlanificador")
	public MethodInvokingJobDetailFactoryBean getNetejaFitxersComposicioJob(STDNetejaFitxersPlanificador netejaFitxersComposicioPlanificador) {
		MethodInvokingJobDetailFactoryBean sTDnetejaFitxersJob = new MethodInvokingJobDetailFactoryBean();
		sTDnetejaFitxersJob.setTargetObject(netejaFitxersComposicioPlanificador);
		sTDnetejaFitxersJob.setTargetMethod("iniciaNetejaFitxers");
		sTDnetejaFitxersJob.setConcurrent(jobConcurrent);
		return sTDnetejaFitxersJob;
	}

	@Bean("netejaFitxersComposicioCronTrigger")
	@Autowired
    @Qualifier("netejaFitxersComposicioJob")
	public CronTriggerFactoryBean getNetejaFitxersComposicioCronTrigger(MethodInvokingJobDetailFactoryBean netejaFitxersComposicioCronTrigger) {
		CronTriggerFactoryBean sTDnetejaFitxersCronTrigger = new CronTriggerFactoryBean();

		sTDnetejaFitxersCronTrigger.setJobDetail(netejaFitxersComposicioCronTrigger.getObject());
		sTDnetejaFitxersCronTrigger.setCronExpression(quartzCronExpression);

		return sTDnetejaFitxersCronTrigger;
	}

	@Bean("netejaFitxersComposicioScheduler")
	@Autowired
    @Qualifier("netejaFitxersComposicioJob")
	public SchedulerFactoryBean getNetejaFitxersComposicioScheduler(CronTriggerFactoryBean netejaFitxersComposicioJob) {
		SchedulerFactoryBean netejaFitxersComposicioScheduler = new SchedulerFactoryBean();

		netejaFitxersComposicioScheduler.setAutoStartup(autoStartup);
		netejaFitxersComposicioScheduler.setTriggers(netejaFitxersComposicioJob.getObject());

		return netejaFitxersComposicioScheduler;
	}

}
