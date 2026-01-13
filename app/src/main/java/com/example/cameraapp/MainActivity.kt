package com.example.cameraapp

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.rememberLifecycleOwner
import coil3.size.Size

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold {
                val modifier = Modifier.padding(it)
                CameraApp(modifier)
            }
        }
    }
}

/**
 * Composable que gestiona el permiso de cámara y muestra la vista de cámara si está concedido.
 *
 * @param modifier Modificador de Compose que se aplica al contenedor.
 */
@Composable
fun CameraApp(modifier: Modifier = Modifier) {
    // Obtenemos el contexto actual
    val context = LocalContext.current

    // Inicializamos el estado comprobando si ya tenemos el permiso
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Lanzador de permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    // UI principal
    if (hasPermission) {
        // Si hay permiso, muestra la cámara
        CameraPreviewScreen(modifier)
    } else {
        // Si no hay permiso, mostramos mensaje y botón para solicitarlo
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Se necesita permiso de cámara para continuar")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Lanza la solicitud de permiso al hacer clic
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            ) {
                Text("Conceder permiso e iniciar cámara")
            }
        }
    }
}

@Composable
fun CameraPreviewScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

    /**
     * Inicializa la cámara con la lente seleccionada.
     */
    fun startCamera() {

        // Obtenemos el CameraProvider que nos permite acceder y controlar las cámaras del dispositivo
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        // Configuramos la vista previa
        val preview = androidx.camera.core.Preview.Builder().build()
        preview.setSurfaceProvider(previewView?.surfaceProvider)

        // Desvinculamos cualquier caso de uso anterior
        cameraProvider.unbindAll()

        // Vinculamos la cámara al ciclo de vida de la actividad
        cameraProvider.bindToLifecycle(
            context as ComponentActivity,
            CameraSelector.Builder().requireLensFacing(lensFacing).build(),
            preview,
            imageCapture
        )
    }

    // Reinicia la cámara cada vez que cambia la lente
    LaunchedEffect(lensFacing) {
        startCamera()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Vista previa de la cámara (80% de la pantalla)
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.8f),
            factory = { ctx ->
                PreviewView(ctx).also { previewView = it }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botones debajo de la cámara
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                    // Camara delantera
                        CameraSelector.LENS_FACING_FRONT
                    else
                    // Camara trasera
                        CameraSelector.LENS_FACING_BACK
                }
            ) {
                Text("Cambiar cámara")
            }

            Button(
                onClick = { takePhotoAndSave(context, imageCapture) }
            ) {
                Text("Sacar foto")
            }
        }
    }
}


/**
 * Toma una foto usando CameraX y la guarda en la galería del dispositivo.
 *
 * @param context Contexto de la aplicación necesario para acceder a ContentResolver.
 * @param imageCapture Instancia de ImageCapture que maneja la captura de la cámara.
 */
fun takePhotoAndSave(context: android.content.Context, imageCapture: ImageCapture) {
    // Genera un nombre de archivo basado en la fecha y hora actual
    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    // Define los metadatos y la ubicación de almacenamiento en MediaStore
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Compose/")
    }

    // Configura las opciones de salida para la foto
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()

    // Toma la foto y maneja los callbacks de éxito o error
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(e: ImageCaptureException) {
                // Maneja errores de captura
                e.printStackTrace()
                Toast.makeText(context, "Error al capturar la foto", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // La foto se guardó correctamente en la galería
                Toast.makeText(context,"Foto guardada en la galería", Toast.LENGTH_SHORT).show()
            }
        }
    )
}