package com.example.sensorsapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.sensorsapp.ui.theme.SensorsAppTheme
import java.util.Vector
import kotlin.math.floor

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        enableEdgeToEdge()
        setContent {
            SensorsAppTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)) { innerPadding ->
                    App(modifier = Modifier.padding(innerPadding), sensorManager)
                }
            }
        }
    }
}

@Composable
fun App(modifier: Modifier, sensorManager: SensorManager) {
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    var lux by remember { mutableFloatStateOf(0f) }
    var accVector by remember { mutableStateOf(Vector3(0f, 0f, 0f)) }

    var bgColor by remember { mutableStateOf(Color.Blue) }
    var rotate by remember { mutableStateOf(0f) }

    var previousZ = 0f;

    val listener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if(event != null) {
                lux = event.values[0] / 10_000

                bgColor = if(lux < 1f) Color.Black
                else if(lux < 2f) Color.Blue
                else if(lux < 3f) Color.Red
                else if(lux < 4f) Color.Green
                else Color.White
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }
    val accListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if(event != null) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                rotate = z * 180;

                accVector = Vector3(x, y, z)
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }
    }
    sensorManager.registerListener(listener, lightSensor, Sensor.TYPE_LIGHT)
    sensorManager.registerListener(accListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.rotate(rotate)
        ) {
            MyText("${floor((lux * 10_000).toDouble())} [lux]")
        }
    }
}

@Composable
fun MyText(text: String) {
    Text(text,
        modifier = Modifier
            .background(Color.LightGray)
            .padding(10.dp),
        fontWeight = FontWeight.SemiBold,
        fontSize = 4.em
    )
}