package cl.duoc.huertoescolar.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.duoc.huertoescolar.MainActivity
import cl.duoc.huertoescolar.R

// RECURSO NATIVO 2: notificaciones locales del teléfono.
// Se usan para los recordatorios (riegos, tareas atrasadas, cosechas, etc.).
// Son solo avisos: no activan riego ni ningún equipo.

private const val ID_CANAL = "recordatorios_huerto"

// Android 8+ exige crear un "canal" antes de mostrar notificaciones
fun crearCanalNotificaciones(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val canal = NotificationChannel(
            ID_CANAL,
            "Recordatorios del huerto",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        canal.description = "Riegos, tareas atrasadas, cosechas y mantenimientos"
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }
}

// Android 13+ pide permiso para mostrar notificaciones
fun tienePermisoNotificaciones(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

// Muestra la notificación. Devuelve false si no hay permiso.
// (El permiso se revisa al inicio con tienePermisoNotificaciones, por eso se suprime el aviso de lint)
@SuppressLint("MissingPermission")
fun mostrarNotificacion(context: Context, titulo: String, texto: String): Boolean {
    if (!tienePermisoNotificaciones(context)) return false

    // Al tocar la notificación se abre la app
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val notificacion = NotificationCompat.Builder(context, ID_CANAL)
        .setSmallIcon(R.drawable.ic_notificacion)
        .setContentTitle(titulo)
        .setContentText(texto)
        .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    try {
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notificacion)
    } catch (e: SecurityException) {
        return false
    }
    return true
}
