# Práctica CameraApp - Jetpack Compose y CameraX

Esta práctica tiene como objetivo aprender a trabajar con **CameraX** y **Jetpack Compose** para crear una aplicación de cámara básica en Android.

---

## Objetivos de aprendizaje

- Solicitar permisos en Android usando `ActivityResultContracts`.
- Mostrar la **vista previa de la cámara** en un `PreviewView` integrado en Compose.
- Cambiar entre **cámara trasera y delantera**.
- Capturar fotos y guardarlas en la **galería del dispositivo** usando `MediaStore`.
- Entender cómo vincular los **casos de uso de CameraX** al ciclo de vida de la actividad.

---

## Estructura del proyecto

- `MainActivity.kt`:  
  Inicializa el Composable principal `CameraApp` dentro de un `Scaffold`.

- `CameraApp()`:
    - Gestiona los **permisos de cámara**.
    - Muestra la vista previa de la cámara si se concede el permiso.

- `CameraPreviewScreen()`:
    - Muestra la **vista previa** de la cámara.
    - Permite **cambiar la cámara** (delantera/trasera).
    - Botón para **capturar fotos** y guardarlas en la galería.
    - Contiene la función `startCamera()` que inicializa CameraX y vincula los casos de uso (`Preview` e `ImageCapture`) al ciclo de vida de la actividad.

- `takePhotoAndSave()`:
    - Captura la foto con CameraX.
    - Genera un nombre de archivo basado en la fecha/hora.
    - Guarda la foto en `Pictures/CameraX-Compose/` usando `MediaStore`.

---

## Tecnologías y conceptos

- **Kotlin** y **Jetpack Compose**
- **CameraX**: `Preview` e `ImageCapture`
- **Activity Result API** para permisos
- **MediaStore** para guardar imágenes
- Uso de `remember`, `LaunchedEffect` y `mutableStateOf` para gestionar estado en Compose
- Integración de `AndroidView` para mostrar `PreviewView` dentro de Compose

---

## Uso

1. Ejecutar la app en un dispositivo Android.
2. Conceder permiso de cámara al iniciar la app.
3. Visualizar la cámara en la pantalla.
4. Usar los botones:
    - **Cambiar cámara**: alterna entre la cámara trasera y delantera.
    - **Sacar foto**: captura la imagen y la guarda en la galería.

---

## Notas para los alumnos

- Es recomendable usar un dispositivo físico para probar la cámara.
- La práctica sirve para entender la integración de componentes tradicionales de Android (`PreviewView`) con **Compose**.
- Se trabaja el ciclo de vida de CameraX para que los recursos se gestionen correctamente.
