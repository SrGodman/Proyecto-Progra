/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_progra;

public class Tarjeta {
    private int id;
    private int usuarioId;
    private String numero;
    private double saldo;

    public Tarjeta(int id, int usuarioId, String numero, double saldo) {
        this.id        = id;
        this.usuarioId = usuarioId;
        this.numero    = numero;
        this.saldo     = saldo;
    }

    public int getId()          { return id; }
    public int getUsuarioId()   { return usuarioId; }
    public String getNumero()   { return numero; }
    public double getSaldo()    { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
