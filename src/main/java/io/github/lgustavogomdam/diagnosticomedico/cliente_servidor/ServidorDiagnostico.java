package io.github.lgustavogomdam.diagnosticomedico.cliente_servidor;

import io.github.lgustavogomdam.diagnosticomedico.sintomas_doencas.Doenca;
import io.github.lgustavogomdam.diagnosticomedico.sintomas_doencas.Sintoma;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServidorDiagnostico {
    private int porta;

    public ServidorDiagnostico(int porta) {
        this.porta = porta;
    }

    public void iniciarServidor() {
        try (ServerSocket servidorSocket = new ServerSocket(porta)) {
            System.out.println("Servidor de Diagnóstico médico iniciado na porta " + porta);

            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());

                // Criando uma nova thread para lidar com o cliente
                Thread clienteThread = new Thread(() -> processarCliente(clienteSocket));
                clienteThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processarCliente(Socket clienteSocket) {
        try (ObjectInputStream entrada = new ObjectInputStream(clienteSocket.getInputStream());
             ObjectOutputStream saida = new ObjectOutputStream(clienteSocket.getOutputStream())) {
            
            // Recebendo a lista de sintomas do cliente
            List<Sintoma> sintomas = (List<Sintoma>) entrada.readObject();

            // Realizando o diagnóstico com base nos sintomas
            DiagnosticoApriori apriori = new DiagnosticoApriori();
            
            //Treinando o algoritmo
            apriori.adicionarExemploTreinamento(Doenca.D0, List.of(Sintoma.S0, Sintoma.S1, Sintoma.S2));
            apriori.adicionarExemploTreinamento(Doenca.D1, List.of(Sintoma.S0, Sintoma.S1));
            apriori.adicionarExemploTreinamento(Doenca.D2, List.of(Sintoma.S2, Sintoma.S3));
            apriori.adicionarExemploTreinamento(Doenca.D3, List.of(Sintoma.S4, Sintoma.S5));
            apriori.adicionarExemploTreinamento(Doenca.D4, List.of(Sintoma.S6, Sintoma.S7));
            apriori.adicionarExemploTreinamento(Doenca.D4, List.of(Sintoma.S7, Sintoma.S8, Sintoma.S3));
            
            double minSupport = 1.0; // Definindo o suporte mínimo conforme necessário
            
            String doencaDiagnostico = apriori.diagnosticar(sintomas, minSupport); // Diagnosticando

            // Enviando o diagnóstico de volta para o cliente
            saida.writeObject(doencaDiagnostico);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
