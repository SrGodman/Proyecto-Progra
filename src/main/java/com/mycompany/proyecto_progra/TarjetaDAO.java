/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_progra;

import java.sql.*;
import java.util.Random;

public class TarjetaDAO {

    // Genera un número de tarjeta aleatorio de 16 dígitos
    // Formato: XXXX-XXXX-XXXX-XXXX
    private static String generarNumero() {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();

        for (int grupo = 0; grupo < 4; grupo++) {
            for (int digito = 0; digito < 4; digito++) {
                // El primer dígito de cada grupo nunca es 0
                if (digito == 0) {
                    sb.append(rnd.nextInt(9) + 1);
                } else {
                    sb.append(rnd.nextInt(10));
                }
            }
            if (grupo < 3) sb.append("-");
        }
        return sb.toString(); // Ejemplo: 4821-3067-9154-2738
    }

    // Crea una tarjeta nueva para el usuario con saldo inicial de Q0
    public static void crear(int usuarioId) {
        String numero = generarNumero();
        String sql = """
            INSERT INTO tarjetas (usuario_id, numero, saldo)
            VALUES (?, ?, 0.0)
        """;

        try (Connection conn = DataBaseManager.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            pstmt.setString(2, numero);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al crear tarjeta: " + e.getMessage());
        }
    }

    // Busca la tarjeta de un usuario
    public static Tarjeta obtenerPorUsuario(int usuarioId) {
        String sql = "SELECT id, usuario_id, numero, saldo FROM tarjetas WHERE usuario_id = ?";

        try (Connection conn = DataBaseManager.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Tarjeta(
                    rs.getInt("id"),
                    rs.getInt("usuario_id"),
                    rs.getString("numero"),
                    rs.getDouble("saldo")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener tarjeta: " + e.getMessage());
        }
        return null;
    }

    // Carga saldo desde la cuenta bancaria a la tarjeta
    public static boolean cargarSaldo(Usuario usuario, double monto) {

        // Validar saldo suficiente en cuenta
        if (monto > usuario.getSaldo()) return false;

        String sqlTarjeta = "UPDATE tarjetas SET saldo = saldo + ? WHERE usuario_id = ?";
        String sqlUsuario = "UPDATE usuarios SET saldo = saldo - ? WHERE id = ?";

        try (Connection conn = DataBaseManager.conectar()) {
            conn.setAutoCommit(false); // Transacción

            try (PreparedStatement pt = conn.prepareStatement(sqlTarjeta);
                 PreparedStatement pu = conn.prepareStatement(sqlUsuario)) {

                // Suma a la tarjeta
                pt.setDouble(1, monto);
                pt.setInt(2, usuario.getId());
                pt.executeUpdate();

                // Resta de la cuenta
                pu.setDouble(1, monto);
                pu.setInt(2, usuario.getId());
                pu.executeUpdate();

                conn.commit(); // ✅ Confirma ambos cambios

                // Actualiza en memoria
                usuario.setSaldo(usuario.getSaldo() - monto);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error al cargar saldo: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}
