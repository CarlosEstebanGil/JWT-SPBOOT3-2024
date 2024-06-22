package com.charly.sbSec3Jwt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest { // PARA LO Q VIENE DEL LOGIN

	private String email;
	private String password; 
	
	
}
