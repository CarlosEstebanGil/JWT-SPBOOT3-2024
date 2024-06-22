package com.charly.sbSec3Jwt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest { // simil a ladata q contiene mi entity usuario osea toda la data q me ponen en la reg 
							//	(el id no xq es autogen , y el rol tampoco xq vamos a hacer q se asigne automatico USER como role default)

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	
}
