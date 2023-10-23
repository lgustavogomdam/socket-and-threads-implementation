package io.github.lgustavogomdam.diagnosticomedico.cliente_servidor;

import io.github.lgustavogomdam.diagnosticomedico.sintomas_doencas.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Cliente {

    private String servidorIP;
    private int portaServidor;

    public Cliente(String servidorIP, int portaServidor) {
        this.servidorIP = servidorIP;
        this.portaServidor = portaServidor;
    }

    public String enviarSintomas(List<Sintoma> sintomas) {
        try (Socket socket = new Socket(servidorIP, portaServidor);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            // Envia a lista de sintomas para o servidor
            output.writeObject(sintomas);

            // Recebe a resposta do servidor
            String resultadoDiagnostico = input.readObject().toString();
            return resultadoDiagnostico;
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro Aquiiiiiii na comunicação com o servidor";
        }
    }
}
