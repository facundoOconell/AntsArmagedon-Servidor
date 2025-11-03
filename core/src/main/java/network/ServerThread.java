package network;

import partida.ConfiguracionPartida;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class ServerThread extends Thread {

    private DatagramSocket socket;
    private final int serverPort = 5555;
    private boolean end = false;

    private static final int MAX_CLIENTS = 2;
    private int connectedClients = 0;
    private final ArrayList<Client> clients = new ArrayList<>();

    private final GameController gameController;

    private ConfiguracionPartida configJ1;
    private ConfiguracionPartida configJ2;

    public ServerThread(GameController gameController) {
        this.gameController = gameController;

        try {
            socket = new DatagramSocket(serverPort);
            socket.setBroadcast(true);
            System.out.println("[SERVIDOR] Iniciado en puerto " + serverPort);
        } catch (SocketException e) {
            System.err.println("[SERVIDOR] Error iniciando servidor UDP: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (!end) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);
                processMessage(packet);
            } catch (IOException ignored) {}
        }
        System.out.println("[SERVIDOR] Hilo detenido.");
    }

    private void processMessage(DatagramPacket packet) {
        GameMessage msg = new GameMessage(new String(packet.getData(), 0, packet.getLength()).trim());
        int index = findClientIndex(packet);

        switch (msg.getType()) {
            case "CONNECT" -> manejarConexion(packet, index);
            case "CONFIG" -> { if (index != -1) manejarConfiguracion(clients.get(index), msg); }
            case "MOVE" -> { if (index != -1) gameController.mover(clients.get(index).getNum(), msg.getFloatArg(0, 0f)); }
            case "JUMP" -> { if (index != -1) gameController.saltar(clients.get(index).getNum()); }
            case "AIM" -> { if (index != -1) gameController.apuntar(clients.get(index).getNum(), msg.getIntArg(0, 0)); }
            case "SHOOT" -> {
                if (index != -1)
                    gameController.disparar(
                        clients.get(index).getNum(),
                        msg.getFloatArg(0, 0f),
                        msg.getFloatArg(1, 0f)
                    );
            }
            case "CHANGE_WEAPON" -> { if (index != -1) gameController.cambiarMovimiento(clients.get(index).getNum(), msg.getIntArg(0, 0)); }
            case "USE" -> { if (index != -1) gameController.usarMovimiento(clients.get(index).getNum()); }
            case "TIMEOUT" -> gameController.timeOut();
            default -> System.out.println("[SERVIDOR] Mensaje desconocido: " + msg);
        }
    }

    private void manejarConexion(DatagramPacket packet, int index) {
        InetAddress ip = packet.getAddress();
        int port = packet.getPort();

        if (index != -1) {
            sendMessage("AlreadyConnected", ip, port);
            return;
        }

        if (connectedClients >= MAX_CLIENTS) {
            sendMessage("Full", ip, port);
            return;
        }

        connectedClients++;
        Client newClient = new Client(connectedClients, ip, port);
        clients.add(newClient);

        sendMessage("Connected:" + connectedClients, ip, port);
        System.out.println("[SERVIDOR] Cliente #" + connectedClients + " conectado (" + ip + ":" + port + ")");

        if (connectedClients == MAX_CLIENTS) {
            sendMessageToAll("Start");
            System.out.println("[SERVIDOR] Ambos jugadores conectados. Esperando configuraciones...");
        }
    }

    private void manejarConfiguracion(Client client, GameMessage msg) {
        ConfiguracionPartida config = ConfiguracionPartida.desdeString(msg.getArg(0));

        if (client.getNum() == 1) {
            configJ1 = config;
            System.out.println("[SERVIDOR] Recibida configuración del Jugador 1.");
        } else {
            configJ2 = config;
            System.out.println("[SERVIDOR] Recibida configuración del Jugador 2.");
        }

        if (configJ1 != null && configJ2 != null) {
            System.out.println("[SERVIDOR] Ambas configuraciones recibidas. Iniciando partida sincronizada...");

            ConfiguracionPartida finalConfig = fusionarConfiguraciones(configJ1, configJ2);
            sendMessageToAll("StartGame:" + finalConfig.toNetworkString());

            System.out.println("[SERVIDOR] Configuración final enviada a los clientes: " +
                finalConfig.toNetworkString());
            System.out.println("[SERVIDOR] El servidor no inicia partida local (modo dedicado).");
        }
    }

    private ConfiguracionPartida fusionarConfiguraciones(ConfiguracionPartida c1, ConfiguracionPartida c2) {
        ConfiguracionPartida finalC = new ConfiguracionPartida();
        Random r = new Random();

        finalC.setMapa(r.nextBoolean() ? c1.getIndiceMapa() : c2.getIndiceMapa());
        finalC.setTiempoTurnoPorIndice(buscarIndiceTiempo(r.nextBoolean() ? c1.getTiempoTurno() : c2.getTiempoTurno()));
        finalC.setFrecuenciaPowerUpsPorIndice(buscarIndiceFrecuencia(r.nextBoolean() ? c1.getFrecuenciaPowerUps() : c2.getFrecuenciaPowerUps()));

        finalC.getEquipoJugador1().addAll(c1.getEquipoJugador1());
        finalC.getEquipoJugador2().addAll(c2.getEquipoJugador2());
        return finalC;
    }

    public void sendMessage(String message, InetAddress clientIp, int clientPort) {
        try {
            byte[] data = message.getBytes();
            socket.send(new DatagramPacket(data, data.length, clientIp, clientPort));
        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error al enviar mensaje: " + e.getMessage());
        }
    }

    public void sendMessageToAll(String message) {
        for (Client c : clients)
            sendMessage(message, c.getIp(), c.getPort());
    }

    private int findClientIndex(DatagramPacket packet) {
        String id = packet.getAddress().toString() + ":" + packet.getPort();
        for (int i = 0; i < clients.size(); i++)
            if (clients.get(i).getId().equals(id)) return i;
        return -1;
    }

    public void terminate() {
        end = true;
        socket.close();
        interrupt();
    }

    public void disconnectClients() {
        sendMessageToAll("Disconnect");
        clients.clear();
        connectedClients = 0;
    }

    private int buscarIndiceTiempo(int tiempo) {
        for (int i = 0; i < ConfiguracionPartida.OPCIONES_TIEMPO_TURNO.length; i++) {
            if (ConfiguracionPartida.OPCIONES_TIEMPO_TURNO[i] == tiempo) return i;
        }
        return 0;
    }

    private int buscarIndiceFrecuencia(int freq) {
        for (int i = 0; i < ConfiguracionPartida.OPCIONES_FRECUENCIA_PU.length; i++) {
            if (ConfiguracionPartida.OPCIONES_FRECUENCIA_PU[i] == freq) return i;
        }
        return 0;
    }
}

/*
Conceptos de redes:

1-UDP (User Datagram Protocol): Es un protocolo de comunicacion que se usa para enviar datos entre computadoras
sin conexion previa. Es muy rapida pero no tiene garantia, osea puede perder paquetes. Para nuestro juego
usamos este, ya que necesitamos velocidad antes que consistencia por las caracteristicas de nuestro juego.
La alternativa seria TCP, pero ese protocolo se usa mas cuando preferis consistencia a velocidad, por ejemplo
en un sistema de mensajeria, no te importa mucha la velocidad como si la consistencia, ya que si un mensaje tarda
mas o menos en enviarse no es tan grave como tener un mensaje que le falta una letra o que tiene una cambiada, esto
arruina el mensaje y el proposito del sistema.

2-DatagramSocket: Canal de comunicacion UDP del servidor. Permite enviar y recibir datagramas.
3-serverPort: Es el puerto en el que el servidor recibe mensajes, esta fijo porque estamos en una red local.

4-Extends Thread: La clase extiende de Thread, osea, representa un hilo de ejecucion independiente.
5-Thread: Un Thread o Hilo, es una linea de ejecucion paralela. Cuando nosotros creamos un pograma de java, y hacemos
    public static void main(String[] args) {, estamos haciendo un hilo, este seria el hilo principal de nuestro
programa, si despues hacemos una clase que extiende de Thread, estamos creando un hilo aparte que se ejecuta
en paralelo al original. En este caso lo utilizamos para recibir mensajes de red de los clientes.

Para que se vea, este es el metodo que ejecuta al juego:

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

Y nuestro hilo de ServerThread se ejecuta asi en el game screen:
        serverThread = new ServerThread(this);
        serverThread.start();

6-Constructor: El constructor de la clase, la cual recibe el GameController para poder ejecutar sus metodos.
E inicializamos el datagram socket en el puerto 5555, que es uno que esta libre.

7-Run: Es un bucle infinito que escucha red, y procesa los mensajes que llegan al servidor. La rutina se ejecuta
con el serverThread.start();
El metodo se corre mientras end sea falso, lo cual dejara el hilo activo hasta que el programa lo cierre.
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024); Es el contenedor del UDP, que almacenara
los datos recibidos. Y:

             socket.receive(packet);

Hace recibir el paquete en el socket de UDP. Este paquete tiene datos adentro, que se pueden obtener y seran
utiles obtener su ip o informacion: String message = (new String(packet.getData())).trim();

             processMessage(packet);

LLama al metodo que procesa, obtienee y traduce en una accion el mensaje.

8- Ejemplo de paquete:

Cliente manda un mensaje: clientThread.sendMessage("Move:1");

El servidor recibe un paquete UDP que contiene datos, por ejemplo:

DatagramPacket {
    data = "Move:1"
    length = 6
    address = /192.168.0.23
    port = 60532
}
 */

/*Arreglar despuues, para que la partida solo empiece cuando ambos jugadores enviaron su  config.*/
