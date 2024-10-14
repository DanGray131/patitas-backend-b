package pe.edu.cibertec.patitas_backend_b.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.cibertec.patitas_backend_b.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_backend_b.dto.LoginResponseDTO;
import pe.edu.cibertec.patitas_backend_b.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_backend_b.dto.LogoutResponseDTO;
import pe.edu.cibertec.patitas_backend_b.service.AutenticacionService;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/autenticacion")
public class AutenticacionController {

    @Autowired
    AutenticacionService autenticacionService;

    @PostMapping("/login")
    public Mono<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return autenticacionService.validarUsuario(loginRequestDTO)
                .map(datosUsuario -> {
                    if (datosUsuario == null) {
                        // Si no se encuentra el usuario, devolver un mensaje de error
                        return new LoginResponseDTO("01", "Error: Usuario no encontrado", "", "");
                    }
                    // Usuario encontrado, devolver la información correspondiente
                    return new LoginResponseDTO("00", "", datosUsuario[0], datosUsuario[1]);
                })
                .defaultIfEmpty(new LoginResponseDTO("01", "Error: Usuario no encontrado", "", ""))
                .onErrorResume(e -> {
                    // En caso de error, devolver un mensaje genérico
                    System.out.println("Error durante el proceso de login: " + e.getMessage());
                    return Mono.just(new LoginResponseDTO("99", "Error: Ocurrió un problema", "", ""));
                });
    }

    @PostMapping("/logout")
    public Mono<LogoutResponseDTO> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        return autenticacionService.cerrarSesionUsuario(logoutRequestDTO)
                .map(fechaLogout -> new LogoutResponseDTO(true, fechaLogout, ""))
                .onErrorResume(e -> {
                    System.out.println(e.getMessage());
                    return Mono.just(new LogoutResponseDTO(false, null, "Error: Ocurrió un problema"));
                });
    }

}
