# SmartHealth Monitor
![Android CI](https://img.shields.io/badge/Android-API26+-green)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-MD3-blue)

Aplicación Android de monitoreo de salud personal en tiempo real.
Desarrollada como proyecto integrador — UTNG 9° Cuatrimestre 2025.

## Stack tecnológico
| Tecnología | Uso |
| --- | --- |
| Kotlin + Jetpack Compose | UI declarativa con Material Design 3 |
| Wearable Data Layer API | Comunicación reloj ↔ teléfono (BLE) |
| Health Services API | Sensor FC real en background (Wear OS) |
| Room Database | Historial persistente de lecturas FC |
| Jetpack Navigation | NavHost entre 4 pantallas |
| GitHub + Conventional Commits | Control de versiones profesional |

## Pantallas
| Pantalla | Descripción |
| --- | --- |
| LoginScreen | Autenticación con validación y State |
| DashboardScreen | FC y Pasos en tiempo real del wearable |
| HistorialScreen | Lecturas persistidas en Room con Flow reactivo |
| AlertaScreen | AlertDialog MD3 + Snackbar de confirmación |

## Capturas de pantalla

### Login
<img src="screenshots/login.jpeg" width="300" alt="Login">

### Dashboard
<img src="screenshots/dashboard.jpeg" width="300" alt="Dashboard">

### Historial
<img src="screenshots/historial.png" width="300" alt="Historial">

### Alerta
<img src="screenshots/alerta.png" width="300" alt="Alerta">

## Unidad II — Wear OS
| Pantalla | Descripción |
|---|---|
| WearDashboardScreen | FC en tiempo real con ScalingLazyColumn y TimeText |
| WearHistorialScreen | Lista con Rotary Input (corona del reloj) |
| WearAlertaScreen    | Botones circulares de confirmación |
| SmartHealth WatchFace | Hora + FC en el WatchFace nativo |
 
![WatchFace](screenshots/watchface.png)
![WearDashboard](screenshots/wear_dashboard.png)

## Autor
Brandon Gustavo Mendoza Amaro — UTNG — brandonchustivo@gmail.com

