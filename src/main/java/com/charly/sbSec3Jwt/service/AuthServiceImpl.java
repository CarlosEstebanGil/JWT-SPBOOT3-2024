package com.charly.sbSec3Jwt.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.charly.sbSec3Jwt.config.JwtService;
import com.charly.sbSec3Jwt.entity.Role;
import com.charly.sbSec3Jwt.entity.User;
import com.charly.sbSec3Jwt.models.AuthResponse;
import com.charly.sbSec3Jwt.models.AuthenticationRequest;
import com.charly.sbSec3Jwt.models.RegisterRequest;
import com.charly.sbSec3Jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/** 
 * Este bean @service servicio de Registracuon va a tener un metodo xa usar el cual efectua el registro y ent recibe el dao de regdel req y 
 * basicamente va a crear un User osea un entity usando el propio builder de la clase User (lombok) con los datos del req de reg dao
 * luego va a usar el repo para esa entidad para este user que construyó con la data del req de reg y lo va a grabar en DB, y además, va a 
 * crear un token jwt ( usando el api qya tenemos creado xapoder hacerlo (osea usando el bean jwtService qes nuestro servicio jwt xa todo esto)
 *   
 * yl idem xa la authenticacion .. (xa esta parte voy a tener q inyectar una <i> de springSecurity llamada AuthenticationManager, OBVIO QUE 
 * SABER -> Debo ir a mi clase de conf de mi app y crear una impl xa esta interface ..  
 * */


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	@Autowired
	private final UserRepository userRepository;
	
	@Autowired
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private final JwtService jwtService;
	
	@Autowired
	private final AuthenticationManager authenticationManager; // saber -> defino la impl en appconfig usando el api nomas me la crea el fmk
																//			pero yo tenia q definirlo como bean eso x eso.
	@Override
	public AuthResponse register(RegisterRequest request) {

		User user = User.builder()
						.firstName(request.getFirstName())
						.lastName(request.getLastName())
						.email(request.getEmail())
						.password(passwordEncoder.encode(request.getPassword()))
						.role(Role.USER)
						.build();
		
		userRepository.save(user);
		
		var jwtToken = jwtService.generateToken(user);  
		
		return AuthResponse.builder()
							.token(jwtToken)
							.build();
	}

	@Override
	public AuthResponse authenticate(AuthenticationRequest request) { //SABER: necesito el email pero from db xa poder crear el token a devolver
																		//	( no debo tomarlo de la propia peticion! ) obvio antes se registró
																		//		asi q deberia existir en db y de paso revalidar la coincidencia
																		//		(x lo tanto existencia en db de ese mail) ademas de authenticados
																		//		los username y password q simplemente lo hace el api del fmk **
		authenticationManager.authenticate(	//**
					new UsernamePasswordAuthenticationToken(request.getEmail(), 
															request.getPassword())
				);
		
		//el chabon ponia var user y listo .. (es lo mismo ) (con var el sistema deduce ( el mismo compi tmb , no en ejec sino ya el compi sabe )
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow(); // el email lo busco en la db otra vez pero es logico q antes 
																					// el token era correcto xq debió registrarse y ent lo busco
																					// en db x email y con eso armo el nuevo token q le voy a 
																					//	devolver desde ya la authenticacion xa q pueda usarlo de
																					//  ahora en mas ensus requests (hasta q expire o se invalide)
		
		//recordemos q usamos el email como un identificar unico como si fuera nuestro username en si xa el sistema xa unicidad usé mail y listo.
		
		var jwtToken = jwtService.generateToken(user);
		
		return AuthResponse.builder().token(jwtToken).build(); // armo la respuesta ( el dto de rta ) y lo retorno.
		
		//FIN! SOLO QDA PROBARLO!!! ( solo falta ir a los controllers y a los privados ponerte @secured y configurar en mi clase de 
		//								conf de seguridad securityconfig configurar nuestra lista blanca whitelist ( ir ahgi y ver, esto lo hice
		//								con un metodo publicEndPoinbs q hice pero hay <>s formas de impl esto.
	}

}
