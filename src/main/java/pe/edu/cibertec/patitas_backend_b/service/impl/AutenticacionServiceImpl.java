package pe.edu.cibertec.patitas_backend_b.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.patitas_backend_b.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_backend_b.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_backend_b.service.AutenticacionService;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

@Service
public class AutenticacionServiceImpl implements AutenticacionService {

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public Mono<String[]> validarUsuario(LoginRequestDTO loginRequestDTO) {
        return Mono.fromCallable(() -> {
            String[] datosUsuario = null;
            Resource resource = resourceLoader.getResource("classpath:usuarios.txt");

            try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(";");
                    if (loginRequestDTO.tipoDocumento().equals(datos[0]) &&
                            loginRequestDTO.numeroDocumento().equals(datos[1]) &&
                            loginRequestDTO.password().equals(datos[2])) {

                        datosUsuario = new String[2];
                        datosUsuario[0] = datos[3];
                        datosUsuario[1] = datos[4];
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al leer el archivo de usuarios: " + e.getMessage(), e);
            }

            return datosUsuario;
        });
    }

    @Override
    public Mono<Date> cerrarSesionUsuario(LogoutRequestDTO logoutRequestDTO) {
        return Mono.fromSupplier(() -> {
            Date fechaLogout = new Date();
            try {
                // Cargar el archivo de auditoría desde el classpath
                Resource resource = resourceLoader.getResource("classpath:auditoria.txt");
                Path rutaArchivo = Paths.get(resource.getFile().toURI());

                // Preparar la línea de auditoría
                StringBuilder sb = new StringBuilder();
                sb.append(logoutRequestDTO.tipoDocumento()).append(";")
                        .append(logoutRequestDTO.numeroDocumento()).append(";")
                        .append(fechaLogout);

                // Escribir en el archivo de forma no bloqueante
                Files.write(rutaArchivo, (sb.toString() + System.lineSeparator()).getBytes(),
                        StandardOpenOption.APPEND);

            } catch (IOException e) {
                throw new RuntimeException("Error al registrar auditoría: " + e.getMessage());
            }
            return fechaLogout;
        });
    }

}
