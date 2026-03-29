/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_progra;

/**
 *
 * @author erick
 */
public class Usuario {

    // Atributos: los datos que nos importan del usuario
    private int id;
    private String username;
    private double saldo;

    // Constructor: se ejecuta cuando escribes "new Usuario(...)"
    public Usuario(int id, String username, double saldo) {
        this.id = id;
        this.username = username;
        this.saldo = saldo;
    }

    // Getters: métodos para LEER los datos desde otras clases
    public int getId()          { return id; }
    public String getUsername() { return username; }
    public double getSaldo()    { return saldo; }

    // Setter: solo el saldo puede cambiar (cuando hace transferencias)
    public void setSaldo(double saldo) { this.saldo = saldo; }
}