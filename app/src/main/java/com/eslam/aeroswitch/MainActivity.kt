package com.eslam.aeroswitch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.eslam.aeroswitch.ui.theme.AeroSwitchTheme

class MainActivity : ComponentActivity() {
    private lateinit var workManager: WorkManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AeroSwitchTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AirplaneTogglerScreen(close = { onExitClick() }, { invokeWork(1L) })
                }
            }
        }
        workManager = WorkManager.getInstance(applicationContext)
        invokeWork(1L)
    }

    private fun onExitClick() {
        this.finish()
    }

    private fun invokeWork(minutes:Long){
        val inputData = Data.Builder()
            .putLong(AirplaneModeWorker.KEY_MINUTES, minutes)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        val request = OneTimeWorkRequestBuilder<AirplaneModeWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(request)
    }
}

@Composable
fun AirplaneTogglerScreen(close: ()->Unit, invoke: (minutes:Long)->Unit){
    var minutes by rememberSaveable { mutableStateOf("") }

    AirplaneTogglerView(close = { close.invoke() }, minutes = minutes, work ={invoke} )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirplaneTogglerView(modifier: Modifier = Modifier, close: ()->Unit,  minutes: String, work: (minutes:Long)->Unit) {
    var isToggled by rememberSaveable { mutableStateOf(false) }

    val airplaneIconColor =  if (isToggled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    var expandedMenu by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Airplane Mode Toggler") },
                modifier = modifier.background(MaterialTheme.colorScheme.primary),
                actions = {
                    IconButton(onClick = { expandedMenu = !expandedMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text(text = "Stop") }, onClick = { isToggled=false
                        expandedMenu=false})
                        DropdownMenuItem(text = { Text(text = "Exit") }, onClick = { close.invoke() })
                    }
                }
            )
        }
    ) {it
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.AirplanemodeActive,
                contentDescription = "Airplane Mode Icon",
                tint = airplaneIconColor,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Enter the number of minutes:",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            var x=""
            TextField(
                value = minutes,
                onValueChange = { x = it },
                label = { Text(text = "Minutes") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isToggled
            )

            Button(
                onClick = { isToggled=!isToggled
                          work.invoke(1L)},
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(width = 200.dp, height = 55.dp),
                enabled = !isToggled
            ) {
                Text(text = if(isToggled) "Disable" else "Toggle", style = MaterialTheme.typography.bodyLarge)
            }
        }

    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    AeroSwitchTheme {

    }
}