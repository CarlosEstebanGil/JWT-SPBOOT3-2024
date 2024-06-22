package com.charly.sbSec3Jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charly.sbSec3Jwt.models.AuthResponse;
import com.charly.sbSec3Jwt.models.AuthenticationRequest;
import com.charly.sbSec3Jwt.models.RegisterRequest;
import com.charly.sbSec3Jwt.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	@Autowired
	private AuthService authService;
	
	//Endpoints:
	
	@PostMapping("/register")										// SABER -> ResponseEntity me permite manejar las rtas http y va con el DTO q armo xa rta!
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) { // el dto de input (xa la entrada q me viene en el http) 
		return ResponseEntity.ok(authService.register(request)); //aca inyecto un servicio donde yo implemento la autenticacion 
																//	(donde se realice el proceso de registro) 
	}
	
	@PostMapping("/authenticate")										// SABER -> ResponseEntity me permite manejar las rtas http y va con el DTO q armo xa rta!
	public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthenticationRequest request) { // el dto de input (xa la entrada q me viene en el http) 
		return ResponseEntity.ok(authService.authenticate(request)); //aca inyecto un servicio donde yo implemento la autenticacion 
																//	(donde se realice el proceso de registro) 
	}

	// ahora voy a implementar crear un servicio de authenticacion osea el authService q aca inyecto y uso.
	
}
