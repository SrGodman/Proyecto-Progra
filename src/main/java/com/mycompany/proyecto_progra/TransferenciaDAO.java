/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_progra;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransferenciaDAO {

    // Guardar una nueva transferencia en la DB
    public static void registrar(int usuarioId, String destino, double monto, String descripcion) {
        String sql = """
            INSERT INTO transferencias (usuario_id, origen, destino, monto, fecha, descripcion)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        // Tomamos la fecha y hora actual automáticamente
        String fecha = LocalDateTime.now()
                       .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conn = DataBaseManager.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            pstmt.setString(2, "Cuenta propia");  // El origen siempre es el usuario actual
            pstmt.setString(3, destino);
            pstmt.setDouble(4, monto);
            pstmt.setString(5, fecha);
            pstmt.setString(6, descripcion);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al registrar transferencia: " + e.getMessage());
        }
    }

    // Obtener SOLO las transferencias del usuario logueado
    public static List<Object[]> obtenerPorUsuario(int usuarioId) {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT destino, monto, fecha, descripcion
            FROM transferencias
            WHERE usuario_id = ?
            ORDER BY fecha DESC
        """;

        try (Connection conn = DataBaseManager.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("destino"),
                    String.format("Q%.2f", rs.getDouble("monto")),
                    rs.getString("fecha"),
                    rs.getString("descripcion")
                });
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener historial: " + e.getMessage());
        }
        return lista;
    }
}
