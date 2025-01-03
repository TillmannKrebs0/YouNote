package com.example.notesapp.view.CategoryMenu

import Sidebar
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.notesapp.viewmodel.CategoriesUiState
import com.example.notesapp.viewmodel.CategoryViewModel
import com.example.notesapp.viewmodel.NotesScreenState
import com.example.notesapp.viewmodel.ScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class SidebarConfiguration(
    val width: Dp,
    val maxOffset: Float,
    val offset: Animatable<Float, AnimationVector1D>
)


@Composable
fun rememberSidebarConfiguration(
    sidebarWidth: Dp = 260.dp
): SidebarConfiguration {
    val density = LocalDensity.current
    val maxSidebarOffset = with(density) { sidebarWidth.toPx() }
    val sidebarOffset = remember { Animatable(-maxSidebarOffset) }

    return remember(sidebarWidth, maxSidebarOffset) {
        SidebarConfiguration(
            width = sidebarWidth,
            maxOffset = maxSidebarOffset,
            offset = sidebarOffset
        )
    }
}


@Composable
fun CategoryMenu(
    screenState: NotesScreenState,
    coroutineScope: CoroutineScope,
    sidebarConfig: SidebarConfiguration,
    screenViewModel: ScreenViewModel,
    categoryUiState: CategoriesUiState,
    categoryViewModel: CategoryViewModel
) {
    if (screenState.sideBarOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    coroutineScope.launch {
                        sidebarConfig.offset.animateTo(
                            -sidebarConfig.maxOffset,
                            animationSpec = tween(durationMillis = 300)
                        )
                        screenViewModel.toggleSideBar()
                    }
                }
        )
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(sidebarConfig.offset.value.roundToInt(), 0) }
            .width(sidebarConfig.width)
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Sidebar(
            items = categoryUiState.categories,
            onAddCategory = { screenViewModel.toggleAddCategoryBox() },
            onItemClick = { category ->
                if (category.isSecret) {
                    screenViewModel.selectCategory(category)
                    screenViewModel.toggleAskPassword()
                } else {
                    categoryViewModel.setActiveCategory(category)
                    coroutineScope.launch {
                        sidebarConfig.offset.animateTo(
                            -sidebarConfig.maxOffset,
                            animationSpec = tween(durationMillis = 300)
                        )
                        screenViewModel.toggleSideBar()
                    }
                }
            },
            onSideBarClose = {
                coroutineScope.launch {
                    sidebarConfig.offset.animateTo(
                        -sidebarConfig.maxOffset,
                        animationSpec = tween(durationMillis = 300)
                    )
                    screenViewModel.toggleSideBar()
                }
            },
            onItemLongPress = { category ->
                categoryViewModel.selectCategory(category)
                screenViewModel.toggleCategoryOptions()
            }
        )
    }
}

