package pe.edu.cibertec.patitas_backend_b.service;

import pe.edu.cibertec.patitas_backend_b.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_backend_b.dto.LogoutRequestDTO;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Date;

public interface AutenticacionService {

    Mono<String[]> validarUsuario(LoginRequestDTO loginRequestDTO);

    Mono<Date> cerrarSesionUsuario(LogoutRequestDTO logoutRequestDTO);

}
