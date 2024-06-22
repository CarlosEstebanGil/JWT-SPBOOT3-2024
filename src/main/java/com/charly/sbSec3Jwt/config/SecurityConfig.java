package com.charly.sbSec3Jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // importante! -> Esta anotacion nos permite habilitar la seguridad a nivel de metodos de nuestra app xa poder poner @secured
@EnableMethodSecurity					//				etc en nuetsro api rest..
@RequiredArgsConstructor
public class SecurityConfig { //acá va lo del filter chain ( ABSTRACCION : recordar solo q aca se configuran los filtros y punto ) 
							 //	( config de filtros se seguridad , politicas etc y ejecutarlo (y x lo tanto nuestro mecanismo!) 

	//ACA ES DONDE NOSOTROS VAMOS A LANZAR COMO TAL NUESTRO FILTRO DE JWT: 
	
	@Autowired
	private final JwtFilter jwtFilter;
	
	@Autowired
	private final AuthenticationProvider authenticationProvider; // este bean lo tengo q crear en x ej mi AppConfig xq sino no existiria una 
				//impl coincidente con la inyeccion q reclamo ( recordar q los beans defs en @config es simil a tener una clase aparte suelta, 
				//con ese nombre, y marcarla con @component. 
	
	// IMPORTANTE -> SABER -> aca definimos la cadena de seguridad q debe ser marcada como bean para usarse (inyectarse) en toda nuestra app xa 
	//							q este bean esta cadena de filtros sea ejecutada en el momento de que se realice 
	//								cualquier peticion (requests a nuestro servidor api rest)!!!
	// ____________________________
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{ 

		// HttpSecurity.csrf(). .. (lo configuro pero con expresiones lambda ya q la forma vieja ya no se usa)  

		// RECORDAR LAMBDAs: q las expr lambda q se esperan en un metodo son apuntadas con algo (1 interface funcional ) 
		//	y esta ya define un metodo con mas q nada el formato de lo q espera q es la firma en si , tipos, args, return etc!
		
		httpSecurity.csrf(csrf ->  csrf.disable())
					.authorizeHttpRequests(auth -> auth.requestMatchers(publicEndpoints())
													    	.permitAll()
													    	.anyRequest()
													    	.authenticated()
													    	)
					.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authenticationProvider(authenticationProvider)
					.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		return httpSecurity.build(); // y aqui ya tenemos nuestra cadena de seguridad 
		//( ahora falta hacer los controllers pàa registrarse, autenticarse y demas apis publicos o secured (privados) <-- !!!!!!!!!! 
	}
	
	private RequestMatcher publicEndpoints() {
		return new OrRequestMatcher ( new AntPathRequestMatcher("/api/saludos/saludarPublico"),
									  new AntPathRequestMatcher("/api/auth/register"), 
									  new AntPathRequestMatcher("/api/auth/authenticate")  
									  );															  
	}				
//	// el ** indicaria q todo el controlardor (todos sus endpoints)van a ser publicos. PERO NO ME FUNCA! ASI Q DE A UNO X UNO!!
}
