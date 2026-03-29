/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_progra;

import java.sql.*;

public class UsuarioDAO {

    /**
     * Busca en la BD si existe un usuario con ese username y password.
     * Retorna un objeto Usuario si lo encuentra, o null si no existe.
     */
    public static Usuario login(String username, String password) {

        // La consulta usa "?" para evitar SQL Injection (buena práctica)
        String sql = "SELECT id, username, saldo FROM usuarios " +
                     "WHERE username = ? AND password = ?";

        try (Connection conn = DataBaseManager.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Reemplaza los "?" con los valores reales
            pstmt.setString(1, username);  // Primer  ?
            pstmt.setString(2, password);  // Segundo ?

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Encontró al usuario: crear y retornar el objeto
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getDouble("saldo")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error en login: " + e.getMessage());
        }

        // Si llega aquí, las credenciales no coincidieron
        return null;
    }
}
