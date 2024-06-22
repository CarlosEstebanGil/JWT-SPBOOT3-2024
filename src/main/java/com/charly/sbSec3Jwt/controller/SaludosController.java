package com.charly.sbSec3Jwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saludos")
public class SaludosController {

	@GetMapping("/saludarPublico")
	public String saludarPublico() {
		return ("Saludo desde endPoint saludarPublico from app SpringSecurity3JWT");
	}
	
	@GetMapping("/saludarProtegido")
	public String saludarProtegido() {
		return ("Saludo desde endPoint saludarProtegido from app SpringSecurity3JWT");
	}
}
