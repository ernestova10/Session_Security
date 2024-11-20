package com.es.seguridadsession.service;

import com.es.seguridadsession.dto.UsuarioDTO;
import com.es.seguridadsession.dto.UsuarioInsertDTO;
import com.es.seguridadsession.model.Session;
import com.es.seguridadsession.model.Usuario;
import com.es.seguridadsession.repository.SessionRepository;
import com.es.seguridadsession.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private SessionRepository sessionRepository;

    public String login(UsuarioDTO userLogin) {

        // Comprobar si user y pass son correctos -> obtener de la BDD el usuario
        String nombreUser = userLogin.getNombre();
        String passUser = userLogin.getPassword();

        // Buscar el usuario por su nombre
        List<Usuario> users = usuarioRepository.findByNombre(nombreUser);

        // Si no se encuentra el usuario, lanzar una excepción
        Usuario u = users
                .stream()
                .filter(user -> user.getNombre().equals(nombreUser))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar la contraseña usando el BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Comparar la contraseña proporcionada con la contraseña encriptada almacenada en la base de datos
        if (!encoder.matches(passUser, u.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Si coincide -> Insertar una sesión
        // Genero un TOKEN
        String token = UUID.randomUUID().toString(); // Esto genera un token aleatorio
        System.out.println("Token generado: " + token);

        // Almaceno la Session en la base de datos
        Session s = new Session();
        s.setToken(token);
        s.setUsuario(u);
        s.setExpirationDate(LocalDateTime.now().plusDays(1));

        sessionRepository.save(s);

        return token;

    }

    public boolean existeUsuario(String nombre) {
        List<Usuario> users = usuarioRepository.findByNombre(nombre);
        return !users.isEmpty();
    }

    public Usuario insertUsuario(UsuarioInsertDTO nuevoUser) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nuevoUser.getNombre());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(nuevoUser.getPassword1());
        usuario.setPassword(hashedPassword);

        usuario.setRol(nuevoUser.getRol());

        return usuarioRepository.save(usuario);
    }

}
