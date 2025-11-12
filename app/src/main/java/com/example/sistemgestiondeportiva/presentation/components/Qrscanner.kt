package com.example.sistemgestiondeportiva.presentation.components

import android.Manifest
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    onQRCodeScanned: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var hasScanned by remember { mutableStateOf(false) }
    var scanStatus by remember { mutableStateOf("Esperando QR...") }

    // Log inicial
    LaunchedEffect(Unit) {
        Log.d("QR_SCANNER", "=== QRScannerScreen INICIADO ===")
        Log.d("QR_SCANNER", "Permiso cámara: ${cameraPermissionState.status.isGranted}")
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            Log.d("QR_SCANNER", "Solicitando permiso de cámara...")
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Escanear QR") },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d("QR_SCANNER", "Botón atrás presionado")
                        onBackClick()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
                    Log.d("QR_SCANNER", "Permiso otorgado, mostrando cámara")
                    CameraPreview(
                        onQRCodeScanned = { qrCode ->
                            if (!hasScanned) {
                                Log.d("QR_SCANNER", "¡QR DETECTADO! Contenido: $qrCode")
                                hasScanned = true
                                scanStatus = "QR detectado, validando..."
                                onQRCodeScanned(qrCode)
                            } else {
                                Log.d("QR_SCANNER", "QR ya fue escaneado, ignorando")
                            }
                        },
                        onStatusChange = { status ->
                            scanStatus = status
                            Log.d("QR_SCANNER", "Estado: $status")
                        }
                    )

                    // Overlay con instrucciones
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            com.example.sistemgestiondeportiva.presentation.components.GlassCard() {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Apunta la cámara al código QR",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = scanStatus,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            if (hasScanned) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
                else -> {
                    Log.d("QR_SCANNER", "Permiso NO otorgado")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Necesitamos permiso para usar la cámara",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                            onClick = {
                                Log.d("QR_SCANNER", "Solicitando permiso de cámara nuevamente")
                                cameraPermissionState.launchPermissionRequest()
                            },
                            outline = true
                        ) {
                            Text("Otorgar permiso")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    onQRCodeScanned: (String) -> Unit,
    onStatusChange: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var isProcessing by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        Log.d("QR_SCANNER", "CameraPreview iniciado")
        onDispose {
            Log.d("QR_SCANNER", "CameraPreview destruido")
        }
    }

    AndroidView(
        factory = { ctx ->
            Log.d("QR_SCANNER", "Creando PreviewView")
            val previewView = PreviewView(ctx)
            val executor = Executors.newSingleThreadExecutor()
            val barcodeScanner = BarcodeScanning.getClient()

            cameraProviderFuture.addListener({
                try {
                    Log.d("QR_SCANNER", "CameraProvider listo")
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(executor) { imageProxy ->
                                if (isProcessing) {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }

                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val image = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )

                                    isProcessing = true
                                    barcodeScanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            if (barcodes.isNotEmpty()) {
                                                Log.d("QR_SCANNER", "Códigos detectados: ${barcodes.size}")
                                                barcodes.forEach { barcode ->
                                                    Log.d("QR_SCANNER", "Tipo: ${barcode.format}, Valor: ${barcode.rawValue}")
                                                }
                                            }

                                            barcodes.firstOrNull()?.let { barcode ->
                                                if (barcode.format == Barcode.FORMAT_QR_CODE) {
                                                    barcode.rawValue?.let { qrCode ->
                                                        Log.d("QR_SCANNER", "✅ QR Code válido encontrado: $qrCode")
                                                        onQRCodeScanned(qrCode)
                                                        onStatusChange("QR detectado!")
                                                    }
                                                } else {
                                                    Log.d("QR_SCANNER", "No es un QR Code, es: ${barcode.format}")
                                                }
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("QR_SCANNER", "Error al procesar imagen", e)
                                            onStatusChange("Error: ${e.message}")
                                        }
                                        .addOnCompleteListener {
                                            isProcessing = false
                                            imageProxy.close()
                                        }
                                } else {
                                    isProcessing = false
                                    imageProxy.close()
                                }
                            }
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                        Log.d("QR_SCANNER", "Cámara vinculada exitosamente")
                        onStatusChange("Cámara lista")
                    } catch (e: Exception) {
                        Log.e("QR_SCANNER", "Error al vincular cámara", e)
                        onStatusChange("Error: ${e.message}")
                    }
                } catch (e: Exception) {
                    Log.e("QR_SCANNER", "Error al obtener CameraProvider", e)
                    onStatusChange("Error: ${e.message}")
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}