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

import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.TrustManager;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import javax.xml.ws.Service;
import org.apache.cxf.transport.http.*;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cat.gencat.ctti.canigo.eforms.IServeisSOAPv1;
import cat.gencat.ctti.canigo.eforms.ServeisSOAPImplv1Service;

@Configuration
public class ComposicioConfig {

    @Value("${forms.wsdl.url}")
    private String formsWSDLUrl;

    @Bean
    public IServeisSOAPv1 getEFormsServeisSOAPv1() throws MalformedURLException {

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(IServeisSOAPv1.class);
        factory.setAddress(formsWSDLUrl);

        System.setProperty("java.net.useSystemProxies", "true");

        IServeisSOAPv1 sEform = (IServeisSOAPv1) factory.create();

        // QName SERVICE_NAME_EFORMS = new QName("http://eforms.canigo.ctti.gencat.cat",
        // "ServeisSOAPImplv1Service");
        // URL eFormWSDLURL = new URL(formsWSDLUrl);

        // Service service = Service.create(eFormWSDLURL, SERVICE_NAME_EFORMS);
        // ServeisSOAPImplv1Service sEform =
        // service.getPort(ServeisSOAPImplv1Service.class);
        // ServeisSOAPImplv1Service sEform = new ServeisSOAPImplv1Service(eFormWSDLURL,
        // SERVICE_NAME_EFORMS);

        BindingProvider bp = (BindingProvider) sEform;

        // Ensure the client is thread safe
        // (http://cxf.apache.org/faq.html#FAQ-AreJAX-WSclientproxiesthreadsafe?)
        ((BindingProvider) (sEform)).getRequestContext().put("thread.local.request.context",
                "true");
        ((BindingProvider) (sEform)).getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, formsWSDLUrl);

        Client client = ClientProxy.getClient(sEform);

        // ((HTTPConduit)
        // (client.getConduit())).getClient().setProxyServerType(ProxyServerType.SOCKS);
        // HTTPConduit http = (HTTPConduit) client.getConduit();

        /* control certificats */
        TLSClientParameters params = ((HTTPConduit) (client.getConduit())).getTlsClientParameters();

        if (params == null) {
            params = new TLSClientParameters();
            ((HTTPConduit) (client.getConduit())).setTlsClientParameters(params);
        }

        params.setSecureSocketProtocol("SSL");
        params.setTrustManagers(new TrustManager[] { new DumbX509TrustManager() });

        params.setDisableCNCheck(true);
        params.setUseHttpsURLConnectionDefaultSslSocketFactory(true);

        /* */

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();

        httpClientPolicy.setConnectionTimeout(10000);
        httpClientPolicy.setReceiveTimeout(30000);

        // httpClientPolicy.setAllowChunking(false);

        ((HTTPConduit) (client.getConduit())).setClient(httpClientPolicy);
        // http.setClient(httpClientPolicy);

        // Activem l'MTOM
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);

        return sEform;
    }

}