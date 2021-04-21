package pe.gob.mtpe.sivice.externo.configuraciones;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableResourceServer
public class RecursosConfiguracionServidor extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/plantrabajo/").permitAll()
		http.authorizeRequests() 
		//.antMatchers(HttpMethod.POST, "/login").permitAll()
		//.antMatchers(HttpMethod.GET,"/api/**").permitAll()
		.antMatchers(HttpMethod.GET,"/api/login").permitAll()
		.antMatchers(HttpMethod.GET,"/api/consejeros/descargar/**").permitAll()
		.antMatchers(HttpMethod.GET,"/api/informes/descargar/**").permitAll()
		.antMatchers(HttpMethod.GET,"/api/boletines/descargar/**").permitAll()
		.antMatchers(HttpMethod.GET,"/api/encargadoregion/descargar/**").permitAll()
		.antMatchers(HttpMethod.GET,"/api/plantrabajo/descargaraprobacion/**").permitAll()
		.antMatchers(HttpMethod.GET,"/api/plantrabajo/descargarplan/**").permitAll()
		.anyRequest().authenticated()
		.and().cors().configurationSource(corsConfiguracion());
		
	}
	
	@Bean
	public CorsConfigurationSource corsConfiguracion() {
		CorsConfiguration configuracion = new CorsConfiguration();
		configuracion.setAllowedOrigins(Arrays.asList("http://localhost:4200", "*"));
		configuracion.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
		configuracion.setAllowCredentials(true);
		configuracion.setAllowedHeaders(Arrays.asList("Content-Type","Authorization", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", "id_usuario", "info_regioncodigo","info_rol"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuracion);
		return source;
	}
	
	@Bean
	public FilterRegistrationBean<CorsFilter> CorsFiltro(){
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfiguracion()));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}
	
	

}
