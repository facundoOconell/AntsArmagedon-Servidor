# Changelog

Todos los cambios importantes en este proyecto serán documentados en este archivo.

El formato esta basado en [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.0.1] - 2025-11-3

# Added
- Clase lobby para esperar las configuraciones de los clientes.
- Eventos de GameController del juego.

### Changed
- Los antiguos eventos a nuevos eventos correspondientes al juego.
- Mensajes de red para saber que esta pasando en la parte red.

## [0.0.0] - 2025-11-1

# Added
- Clase Client para representar al cliente.
- Interfaz GameController para las acciones que se puedan realizar.
- Clase GameMessage para los mensajes del servidor.
- Clase ServerThread para la comunicacion en red.

### Changed
- Se cambiaron clases para que funcione en modo cliente servidor.
- Se cambio el game screen y gestor juego para funcionar con las nuevas clases red.
- Todas las cuestiones visuales del proyecto se eliminaron.
