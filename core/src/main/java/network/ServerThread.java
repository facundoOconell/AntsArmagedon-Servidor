package network;

import partida.ConfiguracionPartida;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

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
            System.out.println("[Servidor] Iniciado en puerto " + serverPort);
        } catch (SocketException e) {
            System.err.println("No se pudo iniciar el servidor UDP en puerto " + serverPort);
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
    }

    private void processMessage(DatagramPacket packet) {
        GameMessage msg = new GameMessage(new String(packet.getData(), 0, packet.getLength()).trim());
        int index = findClientIndex(packet);
        System.out.println("[Servidor] Mensaje recibido: " + msg);

        if (msg.getType().equals("Connect")) {
            manejarConexion(packet, index);
            return;
        }

        if (index == -1) {
            sendMessage(GameMessage.of("NotConnected"), packet.getAddress(), packet.getPort());
            return;
        }

        Client client = clients.get(index);

        switch (msg.getType()) {
            case "Config": manejarConfiguracion(client, msg); break;

            case "Move": gameController.mover(client.getNum(), msg.getFloatArg(0, 0f)); break;

            case "Jump": gameController.saltar(client.getNum()); break;

            case "Aim": gameController.apuntar(client.getNum(), msg.getIntArg(0, 0)); break;

            case "Shoot":
                gameController.disparar(
                    client.getNum(),
                    msg.getFloatArg(0, 0f),
                    msg.getFloatArg(1, 0f)
                );
                break;

            case "ChangeWeapon": gameController.cambiarMovimiento(client.getNum(), msg.getIntArg(0, 0)); break;

            case "Use": gameController.usarMovimiento(client.getNum()); break;

            case "TimeOut": gameController.timeOut(); break;

            default: System.out.println("[Servidor] Mensaje desconocido: " + msg);
        }
    }

    private void manejarConexion(DatagramPacket packet, int index) {
        InetAddress ip = packet.getAddress();
        int port = packet.getPort();

        if (index != -1) {
            sendMessage(GameMessage.of("AlreadyConnected"), ip, port);
            return;
        }

        if (connectedClients >= MAX_CLIENTS) {
            sendMessage(GameMessage.of("Full"), ip, port);
            return;
        }

        connectedClients++;
        Client newClient = new Client(connectedClients, ip, port);
        clients.add(newClient);

        sendMessage(GameMessage.of("Connected", connectedClients), ip, port);
        System.out.println("[Servidor] Cliente " + connectedClients + " conectado.");

        if (connectedClients == MAX_CLIENTS) {
            sendMessageToAll(GameMessage.of("WaitingConfig"));
        }
    }

    private void manejarConfiguracion(Client client, GameMessage msg) {
        ConfiguracionPartida config = parseConfig(msg.getArgs());
        if (client.getNum() == 1) configJ1 = config;
        else configJ2 = config;

        System.out.println("[Servidor] Recibida configuración de jugador " + client.getNum());

        if (configJ1 != null && configJ2 != null) {
            ConfiguracionPartida finalConfig = fusionarConfiguraciones(configJ1, configJ2);
            System.out.println("[Servidor] Configuración final decidida. Enviando a clientes...");

            String configMsg = buildConfigMessage(finalConfig);
            sendMessageToAll(GameMessage.of("StartGame", configMsg));

            gameController.startGame(finalConfig);
        }
    }

    private ConfiguracionPartida parseConfig(String[] parts) {
        ConfiguracionPartida config = new ConfiguracionPartida();
        try {
            if (parts.length > 0) config.setMapa(Integer.parseInt(parts[0]));
            if (parts.length > 1) config.setTiempoTurnoPorIndice(Integer.parseInt(parts[1]));
            if (parts.length > 2) config.setFrecuenciaPowerUpsPorIndice(Integer.parseInt(parts[2]));

            if (parts.length > 3) {
                String[] hormigas = parts[3].split(",");
                int jugador = (connectedClients == 1) ? 1 : 2;
                for (int i = 0; i < hormigas.length; i++) {
                    String tipo = hormigas[i].trim();
                    if (!tipo.isEmpty())
                        config.setHormiga(jugador, i, buscarIndiceHormiga(tipo));
                }
            }
        } catch (Exception e) {
            System.err.println("[Servidor] Error al parsear configuración: " + e.getMessage());
        }
        return config;
    }

    private int buscarIndiceHormiga(String tipo) {
        for (int i = 0; i < ConfiguracionPartida.TIPOS_HORMIGAS.length; i++) {
            if (ConfiguracionPartida.TIPOS_HORMIGAS[i].equals(tipo)) return i;
        }
        return 0;
    }

    private ConfiguracionPartida fusionarConfiguraciones(ConfiguracionPartida c1, ConfiguracionPartida c2) {
        ConfiguracionPartida finalC = new ConfiguracionPartida();
        Random r = new Random();

        finalC.setMapa(r.nextBoolean() ? c1.getIndiceMapa() : c2.getIndiceMapa());
        finalC.setTiempoTurnoPorIndice(buscarIndiceTiempo(
            r.nextBoolean() ? c1.getTiempoTurno() : c2.getTiempoTurno()));
        finalC.setFrecuenciaPowerUpsPorIndice(buscarIndiceFrecuencia(
            r.nextBoolean() ? c1.getFrecuenciaPowerUps() : c2.getFrecuenciaPowerUps()));

        finalC.getEquipoJugador1().addAll(c1.getEquipoJugador1());
        finalC.getEquipoJugador2().addAll(c2.getEquipoJugador2());

        return finalC;
    }

    private int buscarIndiceTiempo(int tiempo) {
        for (int i = 0; i < ConfiguracionPartida.OPCIONES_TIEMPO_TURNO.length; i++)
            if (ConfiguracionPartida.OPCIONES_TIEMPO_TURNO[i] == tiempo) return i;
        return 0;
    }

    private int buscarIndiceFrecuencia(int freq) {
        for (int i = 0; i < ConfiguracionPartida.OPCIONES_FRECUENCIA_PU.length; i++)
            if (ConfiguracionPartida.OPCIONES_FRECUENCIA_PU[i] == freq) return i;
        return 0;
    }

    private String buildConfigMessage(ConfiguracionPartida config) {
        String equipo1 = String.join(",", config.getEquipoJugador1());
        String equipo2 = String.join(",", config.getEquipoJugador2());
        return config.getIndiceMapa() + ":" +
            obtenerIndiceTiempo(config.getTiempoTurno()) + ":" +
            obtenerIndiceFrecuencia(config.getFrecuenciaPowerUps()) + ":" +
            equipo1 + ":" + equipo2;
    }

    private int obtenerIndiceTiempo(int tiempo) {
        for (int i = 0; i < ConfiguracionPartida.OPCIONES_TIEMPO_TURNO.length; i++)
            if (ConfiguracionPartida.OPCIONES_TIEMPO_TURNO[i] == tiempo) return i;
        return 0;
    }

    private int obtenerIndiceFrecuencia(int frecuencia) {
        for (int i = 0; i < ConfiguracionPartida.OPCIONES_FRECUENCIA_PU.length; i++)
            if (ConfiguracionPartida.OPCIONES_FRECUENCIA_PU[i] == frecuencia) return i;
        return 0;
    }

    public void sendMessage(GameMessage msg, InetAddress clientIp, int clientPort) {
        sendMessage(msg.toPacketString(), clientIp, clientPort);
    }

    public void sendMessage(String message, InetAddress clientIp, int clientPort) {
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, clientIp, clientPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("[Servidor] Error al enviar mensaje: " + e.getMessage());
        }
    }

    public void sendMessageToAll(GameMessage msg) {
        for (Client client : clients)
            sendMessage(msg, client.getIp(), client.getPort());
    }

    public void terminate() {
        end = true;
        socket.close();
        interrupt();
    }

    public void disconnectClients() {
        for (Client client : clients)
            sendMessage(GameMessage.of("Disconnect"), client.getIp(), client.getPort());
        clients.clear();
        connectedClients = 0;
    }

    private int findClientIndex(DatagramPacket packet) {
        String id = packet.getAddress().toString() + ":" + packet.getPort();
        for (int i = 0; i < clients.size(); i++)
            if (clients.get(i).getId().equals(id)) return i;
        return -1;
    }
}
