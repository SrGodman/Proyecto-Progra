/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.proyecto_progra;

/**
 *
 * @author erick
 */
public class Proyecto_Progra {

    public static void main(String[] args) {
        DataBaseManager.inicializarDB();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                login LOGIN = new login();
                LOGIN.setVisible(true);
            }
        });
    }
}
