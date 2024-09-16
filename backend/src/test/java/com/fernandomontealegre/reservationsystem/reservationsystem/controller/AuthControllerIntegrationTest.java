package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_ShouldReturnJwtToken() throws Exception {
        String loginRequestJson = "{\"username\":\"admin\", \"password\":\"adminpassword\"}";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequestJson))
                .andExpect(status().isOk());
    }

    @Test
    void client_ShouldNotAccessAdminProtectedRoute() throws Exception {
        // Paso 1: Crear un nuevo usuario cliente
        String registerRequestJson = "{\"username\":\"testclient\", \"password\":\"testpassword\", \"name\":\"Test Client\", \"email\":\"testclient@example.com\"}";
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequestJson))
                .andExpect(status().isOk());

        // Paso 2: Iniciar sesión con el nuevo cliente
        String clientLoginRequestJson = "{\"username\":\"testclient\", \"password\":\"testpassword\"}";
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(clientLoginRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        // Paso 3: Extraer el token JWT de la respuesta
        String loginResponse = result.getResponse().getContentAsString();
        String token = loginResponse.substring(10, loginResponse.length() - 2);

        // Paso 4: Intentar acceder a una ruta protegida por "ADMIN"
        mockMvc.perform(post("/api/hotel-rooms")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"roomNumber\": \"103\", \"description\": \"Habitación de prueba\", \"price\": 100.0, \"roomType\": \"SINGLE\"}"))
                .andExpect(status().isForbidden()); // Esperar un 403 Forbidden
    }
}