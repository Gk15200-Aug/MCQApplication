package com.gurukiran.mcqquiz.utils

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("ContextCastToActivity")
@Composable
inline fun <reified VM : ViewModel> activityViewModel(
    key: String? = null
): VM {
    val activity = LocalContext.current as? ComponentActivity
        ?: error("activityViewModel() must be called inside a ComponentActivity")
    return viewModel(
        modelClass = VM::class.java,
        viewModelStoreOwner = activity,
        key = key
    )
}
