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
    public static void actualizarSaldo(int usuarioId, double nuevoSaldo) {
    String sql = "UPDATE usuarios SET saldo = ? WHERE id = ?";
    
    try (Connection conn = DataBaseManager.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setDouble(1, nuevoSaldo);
        pstmt.setInt(2, usuarioId);
        pstmt.executeUpdate();
        
    } catch (SQLException e) {
        System.out.println("Error al actualizar saldo: " + e.getMessage());
    }
}
    public static Usuario login(String username, String password) {
        String sql = "SELECT id, username, saldo FROM usuarios WHERE username = ? AND password = ?";
        
        try (Connection conn = DataBaseManager.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // AQUÍ ESTABA EL ERROR: Solo enviamos los 3 datos que pide tu clase Usuario
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getDouble("saldo")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error en login: " + e.getMessage());
        }
        return null;
    }
    

    /**
     * Método para registrar a un usuario con saldo de Q100 (Según el Diagrama)
     * Retorna 1 si es exitoso, 0 si ya existe, -1 si hay error.
     */
    public static int registrar(String username, String password) {
    String sql = "INSERT INTO usuarios (username, password, saldo) VALUES (?, ?, 100.0)";

    try (Connection conn = DataBaseManager.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.executeUpdate();
        

        //  Obtener el ID del usuario recién creado
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            int nuevoId = rs.getInt(1);
            TarjetaDAO.crear(nuevoId); // <- crea la tarjeta automáticamente
        }

        return 1;

    } catch (SQLException e) {
        if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
            return 0;
        }
        System.out.println("Error al registrar: " + e.getMessage());
        return -1;
    }
}
    // Busca un usuario por su nombre de usuario
public static Usuario buscarPorUsername(String username) {
    String sql = "SELECT id, username, saldo FROM usuarios WHERE username = ?";

    try (Connection conn = DataBaseManager.conectar();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return new Usuario(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getDouble("saldo")
            );
        }

    } catch (SQLException e) {
        System.out.println("Error al buscar usuario: " + e.getMessage());
    }
    return null;
}
}