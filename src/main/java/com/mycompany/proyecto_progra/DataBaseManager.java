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

        String insertarErick = """
            INSERT OR IGNORE INTO usuarios (username, password, saldo)
            VALUES ('Erick', '123', 5000.00);
        """;

        String insertarJefferson = """
            INSERT OR IGNORE INTO usuarios (username, password, saldo)
            VALUES ('Jefferson', '321', 3000.00);
        """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(crearUsuarios);
            stmt.execute(crearTransferencias);
            stmt.execute(insertarErick);
            stmt.execute(insertarJefferson);
            System.out.println("Base de datos lista.");

        } catch (SQLException e) {
            System.out.println("Error al inicializar: " + e.getMessage());
        }

    }

}