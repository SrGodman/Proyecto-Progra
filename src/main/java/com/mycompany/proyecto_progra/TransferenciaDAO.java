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

    public static boolean registrar(Usuario emisor, String destinoUsername, double monto, String descripcion) {
    Usuario destinatario = UsuarioDAO.buscarPorUsername(destinoUsername);
    if (destinatario == null) return false;
    if (emisor.getUsername().equalsIgnoreCase(destinoUsername)) return false;

    String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    String sqlTransferencia = """ 
                              INSERT INTO transferencias (usuario_id, origen, destino, monto, fecha, descripcion)
                              VALUES(?, ?, ?, ?, ?, ?)
    """;

    try (Connection conn = DataBaseManager.conectar()) {
        conn.setAutoCommit(false);

        try (PreparedStatement pstmt = conn.prepareStatement(sqlTransferencia)) {

            // Registro emisor
            pstmt.setInt(1, emisor.getId());
            pstmt.setString(2, emisor.getUsername());
            pstmt.setString(3, destinoUsername);
            pstmt.setDouble(4, monto);
            pstmt.setString(5, fecha);
            pstmt.setString(6, descripcion);
            pstmt.executeUpdate();

            // Registro destinatario
            pstmt.setInt(1, destinatario.getId());
            pstmt.setString(2, emisor.getUsername());
            pstmt.setString(3, "RECIBIDO");
            pstmt.setDouble(4, monto);
            pstmt.setString(5, fecha);
            pstmt.setString(6, descripcion);
            pstmt.executeUpdate();
        }

        // Actualizar saldos dentro de la misma transacción
        try (PreparedStatement pstmtSaldo = conn.prepareStatement(
                "UPDATE usuarios SET saldo = ? WHERE id = ?")) {

            double nuevoSaldoEmisor       = emisor.getSaldo() - monto;
            double nuevoSaldoDestinatario = destinatario.getSaldo() + monto;

            pstmtSaldo.setDouble(1, nuevoSaldoEmisor);
            pstmtSaldo.setInt(2, emisor.getId());
            pstmtSaldo.executeUpdate();

            pstmtSaldo.setDouble(1, nuevoSaldoDestinatario);
            pstmtSaldo.setInt(2, destinatario.getId());
            pstmtSaldo.executeUpdate();
        }

        conn.commit(); // Todo junto en una sola transacción

        emisor.setSaldo(emisor.getSaldo() - monto); // Actualiza memoria
        return true;

    } catch (SQLException e) {
        System.out.println("Error en transferencia: " + e.getMessage());
        return false;
    }
}

    public static List<Object[]> obtenerPorUsuario(int usuarioId) {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT origen, destino, monto, fecha, descripcion
            FROM transferencias
            WHERE usuario_id = ?
            ORDER BY fecha DESC
        """;

        try (Connection conn = DataBaseManager.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String origen  = rs.getString("origen");
                String destino = rs.getString("destino");
                double monto   = rs.getDouble("monto");
                String fecha   = rs.getString("fecha");
                String desc    = rs.getString("descripcion");

                // Determina si fue enviada o recibida
                String leyenda;
                if (destino.equals("RECIBIDO")) {
                    leyenda = "📥 Recibido de: " + origen;
                } else {
                    leyenda = "📤 Enviado a: " + destino;
                }

                lista.add(new Object[]{
                    leyenda,
                    String.format("Q%.2f", monto),
                    fecha,
                    desc != null ? desc : ""
                });
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener historial: " + e.getMessage());
        }
        return lista;
    }
}
