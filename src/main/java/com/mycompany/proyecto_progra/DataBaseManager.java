/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_progra;

/**
 *
 * @author erick
 */
import java.sql.*;
public class DataBaseManager {

    private static final String URL = "jdbc:sqlite:banco.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    public static void inicializarDB() {
    try {
        Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
        System.out.println("Driver SQLite no encontrado: " + e.getMessage());
        return;
    }

    String crearUsuarios = """
        CREATE TABLE IF NOT EXISTS usuarios (
            id       INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT    NOT NULL UNIQUE,
            password TEXT    NOT NULL,
            saldo    REAL    NOT NULL DEFAULT 0
        );
    """;

    String crearTransferencias = """
        CREATE TABLE IF NOT EXISTS transferencias (
            id          INTEGER PRIMARY KEY AUTOINCREMENT,
            usuario_id  INTEGER NOT NULL,
            origen      TEXT    NOT NULL,
            destino     TEXT    NOT NULL,
            monto       REAL    NOT NULL,
            fecha       TEXT    NOT NULL,
            descripcion TEXT,
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        );
    """;
    
    String crearTarjetas = """
    CREATE TABLE IF NOT EXISTS tarjetas (
        id          INTEGER PRIMARY KEY AUTOINCREMENT,
        usuario_id  INTEGER NOT NULL UNIQUE,
        numero      TEXT    NOT NULL UNIQUE,
        saldo       REAL    NOT NULL DEFAULT 0,
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    );
""";

    try (Connection conn = conectar();
         Statement stmt = conn.createStatement()) {

        stmt.execute(crearUsuarios);
        stmt.execute(crearTransferencias);
        stmt.execute(crearTarjetas);
        System.out.println("Tablas listas.");

    } catch (SQLException e) {
        System.out.println("Error al crear tablas: " + e.getMessage());
    }

    insertarUsuariosPorDefecto();
}

private static void insertarUsuariosPorDefecto() {
    String verificar = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
    String insertar  = "INSERT INTO usuarios (username, password, saldo) VALUES (?, ?, ?)";

    String[][] usuarios = {
        {"Erick",     "123", "5000.0"},
        {"Jefferson", "321", "3000.0"}
    };

    try (Connection conn = conectar()) {
        for (String[] u : usuarios) {
            // Primero verifica si ya existe
            try (PreparedStatement check = conn.prepareStatement(verificar)) {
                check.setString(1, u[0]);
                ResultSet rs = check.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) continue; // Ya existe, skip
            }
            // Solo inserta si no existe, sin desperdiciar IDs
            try (PreparedStatement ins = conn.prepareStatement(insertar)) {
                ins.setString(1, u[0]);
                ins.setString(2, u[1]);
                ins.setDouble(3, Double.parseDouble(u[2]));
                ins.executeUpdate();
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al insertar usuarios: " + e.getMessage());
    }

    // Crear tarjetas para usuarios por defecto si no tienen
    String verificarTarjeta = "SELECT COUNT(*) FROM tarjetas WHERE usuario_id = ?";
    String idQuery = "SELECT id FROM usuarios WHERE username = ?";
    String[] defaultUsers = {"Erick", "Jefferson"};

    try (Connection conn = conectar()) {
        for (String username : defaultUsers) {

            int userId = -1;
            try (PreparedStatement ps = conn.prepareStatement(idQuery)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) userId = rs.getInt("id");
            }

            if (userId == -1) continue;

            try (PreparedStatement check = conn.prepareStatement(verificarTarjeta)) {
                check.setInt(1, userId);
                ResultSet rs = check.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) continue; // Ya tiene tarjeta
            }

            TarjetaDAO.crear(userId); // Crea la tarjeta
        }
    } catch (SQLException e) {
        System.out.println("Error al crear tarjetas por defecto: " + e.getMessage());
    }
}
}