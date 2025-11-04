🐜 AntsArmageddon — Servidor
👥 Integrantes del grupo

Facundo Adolfo O'Conell

Ezequiel García Latorre

Adrián Rojas Betancourt

🕹️ Descripción

Ants Armageddon (Servidor) es la versión servidor del videojuego multijugador Ants Armageddon, desarrollado en Java con LibGDX.
Su función es administrar toda la lógica central de la partida, coordinar las acciones entre los distintos jugadores conectados y mantener la sincronización entre los clientes mediante comunicación en red UDP.

El juego se inspira en Worms, un clásico de estrategia por turnos.
En esta versión, los protagonistas son hormigas armadas, que deben eliminar al equipo contrario utilizando distintos tipos de ataques y movimientos.
El servidor se encarga de gestionar los turnos, validar los movimientos, fusionar las configuraciones de partida y enviar las actualizaciones a los clientes.

⚙️ Funcionalidades del servidor

Manejo de conexiones de jugadores (máximo 2 clientes).

Recepción y procesamiento de mensajes UDP de los clientes.

Envío de eventos de juego a todos los clientes conectados.

Gestión de configuraciones de partida y sincronización del inicio del juego.

Ejecución de la lógica del juego mediante GameController.

Control de turnos, movimientos, disparos y condiciones de victoria.

🧰 Tecnologías utilizadas

Java 17+

LibGDX

IntelliJ IDEA

UDP (User Datagram Protocol) para la comunicación en red

🚧 Estado actual del proyecto

Servidor funcional con conexión UDP y soporte para 2 jugadores.

Implementadas las clases principales:

ServerThread → gestiona la red y los mensajes.

GameMessage → encapsula los mensajes enviados y recibidos.

Client → representa a cada jugador conectado.

Comunicación establecida con el cliente.

Próximos pasos: optimización del flujo de datos y mejoras en la sincronización del juego.

💻 Cómo compilar y ejecutar
1️⃣ Clonar el repositorio

Abrir una terminal (CMD o PowerShell) y ejecutar:

git clone https://github.com/Perritofachero/AntsArmageddon-Servidor.git

2️⃣ Abrir el proyecto en IntelliJ IDEA

Abrir IntelliJ IDEA.

En el menú principal, seleccionar File → Open...

Elegir la carpeta clonada.

Esperar a que se carguen las dependencias de Gradle.

3️⃣ Ejecutar el servidor

Abrir la clase principal:

src/main/java/Lwjgl3Launcher.java


Presionar Run ▶️ en la parte superior de IntelliJ IDEA.

El servidor iniciará y comenzará a escuchar conexiones en el puerto 5555.

⚠️ Nota: los clientes deben estar en la misma red local y configurados para conectarse al mismo puerto.

🖥️ Plataformas objetivo

PC (Escritorio – Windows, Linux, macOS)

🎥 Video demostrativo

Ver video en Google Drive

📘 Wiki del proyecto

Para más información sobre la arquitectura del juego, estructura de red y documentación técnica, visitar la
👉 Wiki del proyecto original
