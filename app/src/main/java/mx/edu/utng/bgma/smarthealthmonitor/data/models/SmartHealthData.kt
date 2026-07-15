package mx.edu.utng.bgma.smarthealthmonitor.data.models

import mx.edu.utng.bgma.smarthealthmonitor.data.db.LecturaFC

// Datos de prueba para desarrollo (mock data)
object MockData {
    val historialFC = listOf(
        LecturaFC(1, 78, "Normal", "wear", "11:00"),
        LecturaFC(2, 82, "Normal", "wear", "10:30"),
        LecturaFC(3, 76, "Normal", "wear", "10:00"),
        LecturaFC(4, 95, "FC Alta", "wear", "09:30"), // fuera de rango
        LecturaFC(5, 71, "Normal", "wear", "09:00"),
        LecturaFC(6, 80, "Normal", "wear", "08:30"),
        LecturaFC(7, 74, "Normal", "wear", "08:00")
    )
    var fcActual = 79
    var pasosActual = 4250
}