package com.example.kotlinmodule4

import android.Manifest
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.*
import java.util.*

@Composable
fun Task10Screen() {

    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var loading by remember { mutableStateOf(false) }
    var addressText by remember { mutableStateOf("") }
    var coordsText by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val granted = permissions.values.any { it }

        if (granted) {

            loading = true

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->

                    if (location != null) {

                        val lat = location.latitude
                        val lng = location.longitude

                        coordsText = "Lat: %.4f   Lng: %.4f".format(lat, lng)

                        try {

                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(lat, lng, 1)

                            if (!addresses.isNullOrEmpty()) {

                                val address = addresses[0]

                                addressText =
                                    "${address.thoroughfare ?: ""}\n" +
                                            "${address.locality ?: ""}\n" +
                                            "${address.countryName ?: ""}"

                            } else {

                                addressText = "Адрес не найден"
                            }

                        } catch (e: Exception) {

                            addressText = "Ошибка геокодирования"
                        }

                    } else {

                        addressText = "Не удалось получить координаты"
                    }

                    loading = false
                }

        } else {

            addressText = "Разрешение на геолокацию отклонено"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Моё местоположение",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (loading) {

            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(20.dp))

            Text("Получаем координаты...")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = addressText,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(coordsText)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {

                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )

            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Получить мой адрес")

        }
    }
}