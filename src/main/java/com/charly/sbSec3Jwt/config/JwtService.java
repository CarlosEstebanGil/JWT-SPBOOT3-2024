package com.charly.sbSec3Jwt.config;

/** ( ESTA CLASE LO EXPLICA TODOO TODO EL MECANISMO DE TODO ) */

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * 
 * Curso -> https://www.youtube.com/watch?v=KBvBY5qyfEM				( ESTA CLASE LO EXPLICA TODOO TODO EL MECANISMO DE TODO )
 * 
 * @author Charly san: ( ver al final de este file la explicacion de todo ) <-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
 * 
 *					   ( 	Obs -> dicha explicacion arranca explicando q falta aun la creacion del token xq el tutorial siguió ese orden; pero
 *								  en este mismo file luego se implementó la creacion tmb (siguiendo con el tutorial) asi q ya contiene todo acá.
 *-----------------------
 */							

@Service	// Esta es una clase de utilidad mia ( componente bean mio de utilidad xa jwt : obtener usuario del token, validar el token etc)
public class JwtService {	// ( googlear secret key generator base64 compatible ) (ej https://generate.plus/en/base64 )    <-- ESA FUNCA !!!
							
	// x practicidad la hardocdeo aca pero deberia estar en vars de entorno o mediante herramientas la genero con una app web q me genera un 
	private static final String SECRET_KEY = "BmzOxHhgghP3yw6YLJIy2Q8dUq6PWS7zmx5RgyOay0Y="; // esta no eran compatibles los chars! -> "zm*@e8uopac7%s)#6#oa0jkz!4t20s2c$2qz3fd9$4e&#%+jjv"; // string de 256 bits ( 32 bytes ) 
																    // ( xq voy a trabajar c/el algoritmo HS256 (256 bits = 32 bytes) )
	//aca continuando viene ya la parte de creacion del token q en el tuto la implementa despues de la imple de verificacion x eso sigo el orden

	//solo Usa los datos del usuario sin extra data claims : solo quiero generar el token a partir de los userdetails
		
	public String generateToken(UserDetails userDetails) { 
		return generateToken(new HashMap<>(), userDetails); //reuso elmas completo(mas generico) pasandole un hashmap vacio osea sin extraclaims  
	} 
			
	//Esta vers (sobrecargop) usa los datos del usuario (userdetails qesla entidad tmb en si recordar q mi entity de user implementa userdetails )
	public String generateToken( Map<String,Object> extraClaims, UserDetails userDetails) {	// un map x si tengo dta (claims) extra 

		//creo el token con la data del usuario desde db : (issuedAt es la fecha de creacion y el exp lo seteo en 24hs en este ejemplo )
		
		return Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) 
				.signWith(getSignInKey(),SignatureAlgorithm.HS256)
				.compact(); // compact es xa que todo eso se haga un string 
	}
	
	
	
	public String getUserName(String token) { // este metodo usa el metodo obtenerclaim y le enviamos el token q nos viene del request y 
											// xa obtener el usuario del token usamos la <i> xa el getclaim el cual crea un objeto de tipo claims
										// q usa el metodo getallclaims q lo q hace es es tomar la secretkey , convertirla a base 64 
									// y devolver esta nueva codificacion de la clave. ent ahi si podemos completar el getAllclaims y asi 
								//	obtentenemos todos los keys y ahi si con estos puedo tomar el claim de usuario desde el token q recibimos!!! 
		
		return getClaim(token, Claims::getSubject); // getClaim func mia generica tmb q la saco a metodo aparte xa generealizar y sirva xa cualquier 
		//		( claim (claim es cada trib del payload ej user, ide, exp, role, etc) ) 
		//	-> NECESITO AGREGAR DEPENDENCIAS NUEVAS:   
		//			->  jjwt-api , jjw-impl , jjwt-jackson ( la 1era tiene las defs (solo <Is> , la segunda tiene las imple de la 1ra y la ulti 
		//						se agrega xa la serializacion y deserialziacion de jwt tokens a json utilizando las librerias de jackson))
	}
	
	public <T> T getClaim( String token, Function<Claims,T> claimsResolver) {
		final Claims claims = getAllClaims(token); // getAllClaims metodo mio q me devuelve todos los claims como tal usando el api de jwt token 
		return claimsResolver.apply(claims);
	}
	
	private Claims getAllClaims(String token) { // se implementa así y punto no entrar tanto en detalle ni reinventar ruedas (abstraccion!!!) 
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey()) // metodo mio q tmb debo impol usando el api de jwt)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	private Key getSignInKey() {	//aca vamos a tomar nuestra secret key y vamos a convertirla en base 64 
		
		byte[] KeyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(KeyBytes); //aca vamos a retornar una firma (key) hmac usando el api
	}

	// SABER! -> VALIDAR TOKEN : TOMA EL USER NAME claim email del jwt del req y lo compara con el username de la db! 
										//	( obs: en realidad en su impl dev el email) x lo q todo username api lo uso con el email
	public boolean validateToken(String token, UserDetails userDetails) {
		
		final String userName = getUserName(token); //1ero tomo el nombre de usuario (reuso el metodo) 
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token)); // y de paso verifico q tmpoco el toquen no haya expirado 
																			// (debo impl, abstraerme igual)
	}
	
	private boolean isTokenExpired(String token) { return getExpiration(token).before(new Date()); }
	
	private Date getExpiration(String token) { return getClaim(token, Claims::getExpiration);}
	
	// VER QUE LA INTERFASE CLAIMS ES MUY UTIL XA OBTENER TODO TIPO DE INFO (CLAIMS) DESDE EL TOKEN 

	// (TMB SABER ESTO: todo lo de abajo todo ok yl la creacion del token todo ok, PERO SABER QUE LUEGO FALTA AUN IMPLEMENTAR TODO LO QUE ES
	//		USAR ESTA INFO PARA EL SERVICIO DE AUTENTICACION ( osea hemos hecho los metodos de generar token xa el cli y de validar luego c/DB)
	//	validaciones necesarias EL username dentro del token q me envia el cli en el req y q no haya ninguna autenticacion previa en el 
	//	contexto de nuestra aplicacion Pero FALTA terminar (implementar) cosas xa VALIDAR QUE EXISTA el user en db , nmo es magico, necesitamos 
	//  explicitamente implementar el extraer del usuario del repo q me traje el email (q es mi username xa autenticacion)
	//  oara esto voy a crear una clase de configuracion @configuration llamada AppConfig. 
	//  ( osea leer lo de abajo y luego leer esto e ir a Appconfig) osea lo q hará esta appconfig y q faltaba es precargar en mi sistema 
	//	un bean de util con una imple de que le paso un username y q me devuelve su entity from db o exception si no existe.
	
	//  una vez ya con lo del appconfig q faltaba q necesitaba ya tenemos imple todo el proceso de valid y autent pero no lo estamos lanzando
	//	en ningun momento, para esto luego voy a crear una nueva clase q la voy a llamar SecurityConfig x ejemplo chota
	
	// ------------------ SABER ESTO:
	// HASTA ACA YA HEMOS VALIDADO EL TOKEN INCLUSO CONTRA DB! ( pero antes debe existir ese token valid ogenerado x mi app y puesto en el context)
	//( SABER: OJO, 1ERO YO AL USER EN SU SOLICITUD DE INSCRIPCION DEBO CREARLE UN TOKEN! QUE VA A IR FIRMADO CON MI SECRET KEY X LO TANTO CON 
	// MI PK , LUEGO EL USER ME VA A MANDAR ESE TOKEN Y YO VOY A SABER Q ES VALIDO XQ DECOMPRIME PARA MI KEY ( INTERNAMENTE USA EL JUEGO XA 
	//DECOMPRIMIR SUPONGOO ) PERO LA COSA ES Q FUé GENERADO POR MI PREVIAMENTE ESE TOKEN Q ME PASA Y CON MI SECRET KEY X ESO ES VALIDO Y MIO
	// saber q mi api deberia tener un controller public de registro y reenviar un email con un token generado xa efectos de verif email y 
	//	confirmacion x parte del usuario. al usuario darle click en su mail se envia la confirmacion que concatena el token 
	// (yo se la armé ya toda asi la url) a un controller de confirmacion que toma el token y verifica que sea valido 
	//(q exista en el contexto ya creado ese mismo token ya q cada token es <> xa cada user xq el payload (claims) obvio tienen user y data <>)
	// luego recien el user puede solicitar algun endpoint secured siempre attachando en el req el authorization (el token q le fué dado) pero 
	// mas bien los sistemas lo q hacen es eliminar el token de verificacion y luego esperar que el usuario se loguee y le crean un token nuevo 
	// que ahi si el mismo va a usar en cada req y le va a servir hasta q expire y le demos otro o previo a q expire puede solicitar renovacion
	// por lo que si no se loguea no va a haber nada en el context para el ( no va a estar ese user en el context ) solo en db verificado mail y
	// x lo tanto dado de alta como usuario; pero para usarlo 1ero debe loguearse (autenticarse) pone user y pass y confirma, el sistema chequea 
	// la data qse supone ya está en db xel proceso inicial de registracion y si user y pass van cifrado con https (clave publica privada(ssl)),ej:

	//		charly san:
				
	//		pregunta, si me quiero loguear en un sistema web que implementa jwt y ya he confirmado por mail mi proceso de registracion. 
	//		luego debo ingresar a un endpoint de login para que me asigne un token para empezar a trabajar? si es asi, ese usuario y password 
	//		como viajan de manera segura hacia el endpoint de autenticacion del usuario ?

	//		ChatGPT
	//		Sí, tu comprensión del proceso es correcta. Aquí te explico el flujo completo y cómo se asegura la seguridad de las credenciales 
//			durante el proceso de autenticación:

	//		Flujo de Autenticación con JWT
	//		Registro:

	//		El usuario se registra en el sistema web proporcionando un correo electrónico y una contraseña.
	//		El sistema envía un correo electrónico de confirmación para verificar la dirección de correo del usuario.
	//		Confirmación:

	//		El usuario recibe el correo electrónico y confirma su registro haciendo clic en un enlace de verificación.
	//		Inicio de Sesión (Login):

	//		El usuario visita el endpoint de inicio de sesión (/login o similar) y proporciona sus credenciales (correo electrónico y contraseña).
	//		El servidor valida estas credenciales.
	//		Si las credenciales son correctas, el servidor genera un JWT y lo envía de vuelta al cliente.
	//		Seguridad de las Credenciales
	//		Para asegurar que las credenciales viajen de manera segura hacia el endpoint de autenticación, se deben seguir estos pasos:

	//		HTTPS (SSL/TLS):

	//		Asegúrate de que el endpoint de autenticación esté protegido por HTTPS. HTTPS encripta la comunicación entre el cliente y 
	//			el servidor, asegurando que las credenciales no puedan ser interceptadas por atacantes en la red.
	//		Un sitio web con HTTPS utiliza certificados SSL/TLS para encriptar el tráfico.
	//		Método de Envío:

	//		Las credenciales (usuario y contraseña) se suelen enviar en el cuerpo de una solicitud HTTP POST.

	//		Un ejemplo de cómo podría verse una solicitud POST para el login:

	//		http
	//		Copiar código
	//		POST /login HTTP/1.1
	//		Host: example.com
	//		Content-Type: application/json
		
	//		{
	//		  "email": "usuario@example.com",
	//		  "password": "tu_contraseña_segura"
	//		}
	//		Consideraciones Adicionales
	//		Almacenamiento Seguro de JWT:
	//		
	//		Una vez qel cliente recibe el JWT,debe almacenarlo de manera segura. 
	//			-> Es común almacenarlo en localStorage o sessionStorage en el navegador.
		
	//		Evita almacenar tokens JWT en localStorage si puedes usar cookies seguras c/las banderas HttpOnly y Secure xa mitigar ataques XSS.
	//		Tiempo de Expiración del JWT:

	//		Configura una expiración razonable para el JWT. Esto asegura q incluso si un token es comprometido, tendrá una vida útil limitada.

	//		Rotación de Claves:

	//		Implementa un mecanismo de rotación de claves para cambiar las claves de firma de JWT periódicamente.
	//		Revalidación del Token:

	//		Considera la implementación de un sistema de revalidación o refresco de tokens (refresh tokens) para mantener sesiones seguras sin 
	//			que los usuarios tengan que autenticarse continuamente.

	//		Siguiendo estas prácticas, puedes asegurar que las credenciales del usuario viajen de manera segura hacia el endpoint 
	//			de autenticación y que el sistema de autenticación basado en JWT sea robusto y seguro.

	//-------------------------------------------------------------------------
}
