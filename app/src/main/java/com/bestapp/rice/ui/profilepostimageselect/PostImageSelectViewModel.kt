package com.bestapp.rice.ui.profilepostimageselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.model.toPostGalleryState
import com.bestapp.rice.ui.profileimageselect.GalleryImageInfo
import com.bestapp.rice.ui.profilepostimageselect.model.PostGalleryUiState
import com.bestapp.rice.ui.profilepostimageselect.model.SelectedImageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostImageSelectViewModel @Inject constructor() : ViewModel() {

    private val _galleryImageStates = MutableStateFlow<List<PostGalleryUiState>>(emptyList())
    val galleryImageStates: StateFlow<List<PostGalleryUiState>> = _galleryImageStates.asStateFlow()

    fun submit() {
        TODO("Not yet implemented")
    }

    fun unselect(state: SelectedImageUiState) {
        TODO("Not yet implemented")
    }

    fun updateGalleryImages(images: List<GalleryImageInfo>) {
        viewModelScope.launch {
            _galleryImageStates.emit(images.mapIndexed { index, galleryImageInfo ->
                galleryImageInfo.toPostGalleryState(index + 1)
            })
        }
    }
}