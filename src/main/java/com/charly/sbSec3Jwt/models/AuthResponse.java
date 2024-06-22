package com.charly.sbSec3Jwt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Basicamente lo que vamos devolver es el token ( el jwt , como cadena de caracteres osea el token jwt como string string ) <---- !!! SABER !!!

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

	private String token; 
	
}
