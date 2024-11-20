package com.es.seguridadsession.controller;

import com.es.seguridadsession.dto.UsuarioDTO;
import com.es.seguridadsession.dto.UsuarioInsertDTO;

import com.es.seguridadsession.model.Usuario;
import com.es.seguridadsession.service.UsuarioService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // CR
    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(
            @RequestBody UsuarioDTO userLogin,
            HttpServletResponse response
    ) {

        // 1º Asegurarnos que userLogin no viene null
        if(userLogin == null || userLogin.getNombre() == null || userLogin.getPassword() == null) {
            // Lanzamos una excepcion
        }

        // 2º Comprobar usuario y contraseña en el service y obtener token
        String token = usuarioService.login(userLogin);

        // 3º añado la cookie a la respuesta
        Cookie cookie = new Cookie("tokenSession", token);
        response.addCookie(cookie);

        return new ResponseEntity<>(userLogin, HttpStatus.OK);
    }


    // INSERT
    @PostMapping("/")
    public ResponseEntity<?> insert(
            @RequestBody UsuarioInsertDTO nuevoUser
    ) {
        // Validar si las contraseñas coinciden
        if (nuevoUser.getPassword1() == null || nuevoUser.getPassword2() == null ||
                !nuevoUser.getPassword1().equals(nuevoUser.getPassword2())) {
            return new ResponseEntity<>("El usuario no debe de ser nulo",HttpStatus.BAD_REQUEST);
        }

        // Verificar si el nombre de usuario ya existe
        if (usuarioService.existeUsuario(nuevoUser.getNombre())) {
            return new ResponseEntity<>("El usuario ya existe",HttpStatus.BAD_REQUEST);
        }

        if (nuevoUser.getRol() == null || (!nuevoUser.getRol().equalsIgnoreCase("User") &&
                !nuevoUser.getRol().equalsIgnoreCase("Admin"))) {
            return new ResponseEntity<>("El rol debe de ser 'User' o 'Admin'",HttpStatus.BAD_REQUEST);
        }

        // Crear el nuevo usuario
        Usuario nuevoUsuario = usuarioService.insertUsuario(nuevoUser);

        // Devolver el DTO del nuevo usuario
        return new ResponseEntity<>(nuevoUser, HttpStatus.CREATED);
    }


}
