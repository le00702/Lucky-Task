package com.example.luckytask

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.LuckyTaskTopAppBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import com.example.luckytask.sensor.ShakeListener

class MockDiceActivity : ComponentActivity() {
    private lateinit var shakeListener: ShakeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shakeListener = ShakeListener(this) {
            Log.d("[SENSOR]", "Test message")
        }

        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    LuckyTaskTopAppBar(
                        stringResource(id = R.string.app_name)
                    )
                }) { innerPadding ->
                    MockDiceApp(
                        modifier = Modifier.padding(innerPadding)
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

@Composable
fun MockDiceApp(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    var zoomed by remember { mutableStateOf(false) }

    /*** Upscale the image when it is zoomed to 1.5 of original size
     *   --> if it is not zoomed, it should stay at its original size
     *   --> set the animation time for zooming to 250ms
     *   --> using tween for time-based animation ***/
    val zoomFactor by animateFloatAsState(
        targetValue = if (zoomed) 1.5f else 1.0f,
        animationSpec = tween(250)
    )
    /*** Rotate image only, when it is zoomed
     *   --> set the animation time for rotating to 500ms
     *   --> using tween for time-based animation
     *   --> use easing for more natural acceleration of animation
     *   --> not moving at constant speed!
     *   --> LinearOutSlowIn: Start normal, then slow down***/
    val rotation by animateFloatAsState(
        targetValue = if(zoomed) 2 * 360f else 0f,
        animationSpec = tween(500, easing = LinearOutSlowInEasing)
        )

    /*** Organize elements in column ***/
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(
            text = "Roll the dice!",
            fontSize = 30.sp,
            color = colorResource(R.color.header_color)
        )
        Image(
            painter = painterResource(R.drawable.dice),
            contentDescription = "Dice Image",
            modifier = Modifier
                .size(200.dp)
                .clickable {
                    zoomed = showAnimation(context, zoomed) }
                /*** Scale image size based on current zoom factor ***/
                .graphicsLayer {
                    scaleX = zoomFactor
                    scaleY = zoomFactor
                    rotationZ = rotation
                }
        )
    }
}

private fun showAnimation(context: Context, zoomed: Boolean): Boolean{
    Toast.makeText(context, "Dice clicked!", Toast.LENGTH_SHORT).show()
    return !zoomed
}

