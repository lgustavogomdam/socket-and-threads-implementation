/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package io.github.lgustavogomdam.diagnosticomedico;

import io.github.lgustavogomdam.diagnosticomedico.interfaces.ClienteGUI;
import io.github.lgustavogomdam.diagnosticomedico.cliente_servidor.ServidorDiagnostico;
import static io.github.lgustavogomdam.diagnosticomedico.cliente_servidor.DiagnosticoApriori.imprimirDiagnostico;
import io.github.lgustavogomdam.diagnosticomedico.sintomas_doencas.Doenca;
import io.github.lgustavogomdam.diagnosticomedico.sintomas_doencas.Sintoma;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author lgustavogomdam
 */
public class DiagnosticoMedico {

    public static void main(String[] args) {
        
        // Criando uma thread para o servidor
        Thread servidorThread = new Thread(() -> {
            int portaServidor = 12345; // Porta do servidor
            ServidorDiagnostico servidor = new ServidorDiagnostico(portaServidor);
            servidor.iniciarServidor();
        });

        // Criando uma thread para o ClienteGUI
        Thread clienteGUIThread = new Thread(() -> {
            ClienteGUI clienteGUI = new ClienteGUI();
            clienteGUI.setVisible(true);
        });

        // Iniciando o servidor e o ClienteGUI em threads separadas
        servidorThread.start();
        clienteGUIThread.start();
    }
}
