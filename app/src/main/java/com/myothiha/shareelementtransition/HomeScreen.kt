package com.myothiha.shareelementtransition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * @Author myothiha
 * Created 28/04/2024 at 11:19 PM.
 **/

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    SharedTransitionLayout(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = "list",
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                slideInHorizontally { it } + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally { -it } + fadeOut()
            },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
            popExitTransition = { slideOutHorizontally { -it } + fadeOut() }
        ) {
            composable(route = "list") {
                ItemListScreen(
                    animatedVisibilityScope = this@composable,
                    onItemClick = {
                        val urlEncoded = URLEncoder.encode(it, "UTF-8")
                        navController.navigate("detail/$urlEncoded")

                    }
                )
            }
            composable(route = "detail/{url}") {
                val encodedUrl = it.arguments?.getString("url")
                val decodeUrl = URLDecoder.decode(encodedUrl, "UTF-8")
                DetailScreen(
                    animatedVisibilityScope = this@composable,
                    url = decodeUrl,
                    onclickBack = { navController.popBackStack() }
                )
            }

        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ItemListScreen(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(all = 16.dp),
    ) {
        items(count = 50) {
            val width = 340 + it * 20
            val height = width * 4 / 3
            val url = "https://loremflickr.com/$width/$height"

            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = dropUnlessResumed {
                            onItemClick(url)
                        }
                    )
                    .fillMaxWidth(),
            ) {
                AsyncImage(
                    model = url,
                    modifier = Modifier
                        .size(100.dp)
                        // 3
                        .sharedElement(
                            state = rememberSharedContentState(key = "image-$url"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                Spacer(Modifier.size(16.dp))
                LoremIpsum(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = "text-$url"
                            ), animatedVisibilityScope = animatedVisibilityScope
                        ),
                    maxLines = 3,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DetailScreen(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    url: String,
    onclickBack: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onclickBack
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = url,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .sharedElement(
                    rememberSharedContentState(key = "image-$url"),
                    animatedVisibilityScope,
                ),
            contentDescription = null,
        )
        LoremIpsum(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                // 1
                .sharedBounds(
                    rememberSharedContentState(
                        key = "text-$url"
                    ),
                    animatedVisibilityScope,
                )
        )
    }

}

