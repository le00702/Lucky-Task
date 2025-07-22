package com.example.luckytask

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import com.example.luckytask.sensor.ShakeListener
import com.example.luckytask.ui.theme.elements.Dice

class MockDiceActivity : ComponentActivity() {
    private lateinit var shakeListener: ShakeListener
    private val TAG = "[SENSOR]"

    /*** Use this variable to keep track of animation ***/
    private var triggerAnimation = mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*** Pass the code for onShake() for shakeListener --> trigger animation ***/
        shakeListener = ShakeListener(this) {
            Log.d(TAG, "Shake detected!")
            triggerAnimation.value = true
        }

        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                AppWithDrawer(
                    currentActivityName = "MockDiceActivity",
                    topBarTitle = "Dice Game"
                ) {
                    Dice(
                        modifier = Modifier,
                        triggerAnimation = triggerAnimation,
                        isMock = true
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeListener.start()
    }

    override fun onPause() {
        super.onPause()
        shakeListener.stop()
    }

}

