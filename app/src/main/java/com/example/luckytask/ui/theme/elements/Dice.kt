package com.example.luckytask.ui.theme.elements

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.luckytask.R
import kotlinx.coroutines.delay

@Composable
fun Dice(modifier: Modifier = Modifier, triggerAnimation: MutableState<Boolean>, isMock: Boolean) {

    /*** Use this for mocking purposes for now: If it is the mock, apply the modifier and
     *   place in middle of the WHOLE screen --> else, do not do this! ***/
    val customModifier: Modifier = if (isMock) modifier.fillMaxSize() else Modifier

    /*** Upscale the image when it is zoomed to 1.5 of original size
     *   --> if it is not zoomed, it should stay at its original size
     *   --> set the animation time for zooming to 250ms
     *   --> using tween for time-based animation ***/
    val zoomFactor by animateFloatAsState(
        targetValue = if (triggerAnimation.value) 1.5f else 1.0f, animationSpec = tween(250)
    )

    /*** Rotate image only, when it is zoomed
     *   --> set the animation time for rotating to 500ms
     *   --> using tween for time-based animation
     *   --> use easing for more natural acceleration of animation
     *   --> not moving at constant speed!
     *   --> LinearOutSlowIn: Start normal, then slow down ***/
    val rotation by animateFloatAsState(
        targetValue = if (triggerAnimation.value) 2 * 360f else 0f,
        animationSpec = tween(500, easing = LinearOutSlowInEasing)
    )

    /*** As soon as the animation is started (if triggerAnimation.value = true)
     * --> start it + add delays, and revert back to original size ***/
    LaunchedEffect(triggerAnimation.value) {
        if (triggerAnimation.value) {
            delay(250)
            triggerAnimation.value = false
            delay(250)
        }
    }

    /*** Organize elements in column ***/
    Column(
        /*** Apply custom modifier, depending on whether this is the mock dice or not ***/
        modifier = customModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.dice),
            contentDescription = "Dice Image",
            modifier = Modifier
                .size(200.dp)

                /*** Scale image size based on current zoom factor + rotate ***/
                .graphicsLayer {
                    scaleX = zoomFactor
                    scaleY = zoomFactor
                    rotationZ = rotation
                })
    }
}