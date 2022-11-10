package it.disim.bpd.example.soap;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

	@Bean
	public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean<>(servlet, "/MyService/*");
	}

	@Bean(name = "myserviceinterface")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema simpleServiceSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("MyServicePort");
		wsdl11Definition.setLocationUri("/MyService");
		wsdl11Definition.setTargetNamespace("http://bpd.disim.univaq.it/");
		wsdl11Definition.setSchema(simpleServiceSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema simpleServiceSchema() {
		return new SimpleXsdSchema(new ClassPathResource("schema/myServiceSchema.xsd"));
	}
}
