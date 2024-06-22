package com.charly.sbSec3Jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.charly.sbSec3Jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/** 
 * 
 * SABER --> ( recordar q los beans defs en @config es simil a tener una clase aparte suelta, 
				//con ese nombre, y marcarla con @component. !!!!!!!!!!!!
 *
 */

@Configuration
@RequiredArgsConstructor
public class AppConfig { //usa el repository (dao impl) de user xa buscar el user por el email 

	@Autowired
	private final UserRepository userRepository;  
	
	@Bean // xa poder inyectarlo y usarlo en nuestra aplicacion. Recordar q este tipo de def de beans lo que hace es:  
	public UserDetailsService userDetailService() { 	// crea un bean del tipo UserDetailsService q va a ser usado en mi app como bean de util 
														// q sirve xa lo que su impl (aca def con lambda) es lo que importa y es un metodo que 
														// recibe un username y usa el dao (repo) de user xa findByEmail y obtener asi el entity 
														// del usuario desde la db o si no existe ese usuario en db entonces arrojar una exception y fin.
			return username -> userRepository
									.findByEmail(username)
									.orElseThrow(() -> new UsernameNotFoundException("El Usuario no ha sido encontrado."));
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailService()); // SABER!! DI -> esto seria como una inyeccion por setter!!
																		   //						( simil a x constructor )
		authenticationProvider.setPasswordEncoder(passwordEncoder()); // esto para definir un bean encoder xa codificar mis passwords 
		return authenticationProvider;
		
	}
	
	@Bean // SABER: Recordar q los beans definidos asiu deben definirse los metodos q los proveen como este como public siempre!
	public PasswordEncoder passwordEncoder() { // uso la imple de encoder bcrypt ..  
		return new BCryptPasswordEncoder();
	} 
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	} 
	
	// ahora lo ulti q hace es configurar la whitelist o endpoints publicos (en securityconfig cre private RequestMatcher publicEndpoints() )
	// ( en ese orden en el tutorial ahora PostMan y a probar!! )
	
}
 