package com.charly.sbSec3Jwt.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{

	@Autowired
	private final UserDetailsService userDetailsService;
	
	@Autowired
	private final JwtService jwtService;
	
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull  HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;

		if (authHeader == null || !authHeader.startsWith("Bearer")) {
			filterChain.doFilter(request, response); // ejecutamos el dofilter sin poner nada adentro x lo q No JWT token present 
													 //ent continue to the next filter
			return;
		}
		
		jwt = authHeader.substring(7);
		
		userEmail = jwtService.getUserName(jwt); // !! ABSTRAERME EL COMO !! -> me devuelve el usuario desde el token q le paso q saqué del req 
													// SABER: Hasta acá aun solo me traje el claim user del jwt del req xero no validé nada aun**
		
//recordar el entity User implements UserDetails qayuda al userDetailService qlo usaxa esa entidad ysu data(estado) a chequear en db y controlar
		
		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null ) { //qen este req yanoestes autenticado(already) 
	
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); // userdetailsService va a db chequea  
																							 // email exista en db

			// ** validacion del token (jwt) (del req) 
			//si el token es valido ( funcion mia valida token usando mi userdetails ya cargado con los datos de la entidad),
			if (jwtService.validateToken(jwt, userDetails)) {   // ent continuamos el proceso (ponemos el authenticationtoken en el context) 
																//	 			( sino se devolverá 403 )

				//a) q el obj xa el token de authorizacion tnga la data del userdetails de mi entity desde db
				
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken 
																				(	userDetails, 
																					null,
																					userDetails.getAuthorities()
																				);   
				//b) y q tmb los detalles vienen dentro del req
				
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); 
				
				//c) seteamos el authenticationToken al Context: recordar q el context esel area donde estan en mem 
				//											las authenticaciones actuales ( los authenticationTokens .. )  
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			}
		} 

		filterChain.doFilter(request, response); //ejecutamos el filtro para verificar este token valido pero no authenticado aun x el sist 
				//  habiendo o no cargado un authenticationtoken en el secutiry context y ent siguiendo el proceso o no (403) respectivamente. 
	} 
}
