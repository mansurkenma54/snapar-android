package kz.snapar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kz.snapar.app.ui.SnaparApp
import kz.snapar.app.ui.theme.SnaparTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnaparTheme {
                SnaparApp()
            }
        }
    }
}
