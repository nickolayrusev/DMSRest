package org.mongo.config;

import java.util.Set;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {
	// While Servlet API allows to configure many servlets it is common for spring applications
    // to configure root context, that is shared across all servlets, and many dispatcher
    // servlets each with its unique context. However this application consist of only
    // one dispatcher servlet so there is no point to configure root configuration.
	@Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.scan("org.mongo.config");
        
        
        ServletRegistration.Dynamic appServlet = servletContext.addServlet("appServlet", new DispatcherServlet(dispatcherContext));
		appServlet.setLoadOnStartup(1);
		Set<String> mappingConflicts = appServlet.addMapping("/");
		
		FilterRegistration.Dynamic fr = servletContext.addFilter(
				"encodingFilter", new CharacterEncodingFilter());
		fr.setInitParameter("encoding", "UTF-8");
		fr.setInitParameter("forceEncoding", "true");
		fr.addMappingForUrlPatterns(null, true, "/*");
		
		FilterRegistration.Dynamic eTagFilter = servletContext.addFilter(
				"eTagFilter", new ShallowEtagHeaderFilter());
		eTagFilter.addMappingForUrlPatterns(null, true, "/*");
		
		

		if (!mappingConflicts.isEmpty()) {
			throw new IllegalStateException("'appServlet' cannot be mapped to '/' under Tomcat versions <= 7.0.14");
		}
    }
}
