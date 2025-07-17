package com.example.luckytask.ui.theme.elements

import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.R
import kotlinx.coroutines.delay

@Composable
fun Dice(modifier: Modifier = Modifier) {

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
        targetValue = if (zoomed) 2 * 360f else 0f,
        animationSpec = tween(500, easing = LinearOutSlowInEasing)
    )

    /*** As soon as the animation is started (if zoomed = true, after
     *   clicking (for now)) --> start it +  add delays, and revert back
     *   to original size ***/
    LaunchedEffect(zoomed) {
        if (zoomed) {
            delay(250)
            zoomed = false
            delay(250)
        }
    }


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

                    /*** Upon next clicking (after setting it to false in LaunchedEffect)
                     *   --> call function to revert the value back to true
                     *   --> which again calls the LaunchedEffect code ***/
                    zoomed = !zoomed
                    Log.d("[DICE]", "Call animation")
                }

                /*** Scale image size based on current zoom factor + rotate ***/
                .graphicsLayer {
                    scaleX = zoomFactor
                    scaleY = zoomFactor
                    rotationZ = rotation
                }
        )
    }
}