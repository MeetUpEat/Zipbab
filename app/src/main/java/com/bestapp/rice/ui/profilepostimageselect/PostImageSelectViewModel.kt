package com.bestapp.rice.ui.profilepostimageselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestapp.rice.data.repository.UserRepository
import com.bestapp.rice.model.toGalleryUiState
import com.bestapp.rice.model.toPostGalleryState
import com.bestapp.rice.model.toSelectUiState
import com.bestapp.rice.ui.profileimageselect.GalleryImageInfo
import com.bestapp.rice.ui.profilepostimageselect.model.PostGalleryUiState
import com.bestapp.rice.ui.profilepostimageselect.model.SelectedImageUiState
import com.bestapp.rice.ui.profilepostimageselect.model.SubmitUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostImageSelectViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _galleryImageStates = MutableStateFlow<List<PostGalleryUiState>>(emptyList())
    val galleryImageStates: StateFlow<List<PostGalleryUiState>> = _galleryImageStates.asStateFlow()

    private val _submitUiState = MutableStateFlow<SubmitUiState>(SubmitUiState.Default)
    val submitUiState: SharedFlow<SubmitUiState> = _submitUiState.asSharedFlow()

    val selectedImageStatesFlow = galleryImageStates.map { states ->
        states.filter { state ->
            state.isSelected
        }.sortedBy {
            it.order
        }.map {
            it.toSelectUiState()
        }
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.Lazily, initialValue = emptyList()
    )

    fun submit(userDocumentID: String) {
        // 이미 업로드 중이거나 성공한 경우, 요청을 거부함
        if (_submitUiState.value == SubmitUiState.Uploading || _submitUiState.value == SubmitUiState.Success) {
            return
        }
        viewModelScope.launch {
            runCatching {
                _submitUiState.emit(SubmitUiState.Uploading)

                val isSuccess = userRepository.addPost(
                    userDocumentID,
                    selectedImageStatesFlow.value.map {
                        it.uri.toString()
                    }
                )
                if (isSuccess) {
                    _submitUiState.emit(SubmitUiState.Success)
                } else {
                    _submitUiState.emit(SubmitUiState.Fail)
                }
            }.onFailure {
                _submitUiState.emit(SubmitUiState.Fail)
            }
        }
    }

    fun unselect(state: SelectedImageUiState) {
        reverseImageSelecting(state.toGalleryUiState())
    }

    // TODO : 기존 아이템 클릭한 순서는 유지해야 함
    fun updateGalleryImages(images: List<GalleryImageInfo>) {
        viewModelScope.launch {
            _galleryImageStates.emit(images.mapIndexed { index, galleryImageInfo ->
                galleryImageInfo.toPostGalleryState()
            })
        }
    }

    fun reverseImageSelecting(state: PostGalleryUiState) {
        _galleryImageStates.update {
            val states = it.toMutableList()
            val index = states.indexOf(state)
            if (index == -1) {
                return
            }

            // 기존에 선택된 아이템을 다시 누른 경우
            if (state.isSelected) {
                // 해당 아이템보다 순서가 앞에 있는 것은 그대로 두고, 뒤에 있는 것은 1씩 감소 시킨다.
                val targetOrder = state.order
                for (idx in 0 until states.size) {
                    val currentOrder = states[idx].order
                    when {
                        currentOrder == targetOrder -> states[idx] = state.copy(isSelected = false, order = PostGalleryUiState.NOT_SELECTED_ORDER)
                        currentOrder > targetOrder -> states[idx] = states[idx].copy(order = states[idx].order - 1)
                        else -> Unit // 선택되지 않은 아이템, 순서 상 먼저 누른 아이템
                    }
                }
            } else { // 아이템을 새롭게 누른 경우
                val nextOrder = states.maxOf { state ->
                    state.order
                } + 1

                states[index] = states[index].copy(isSelected = true, order = nextOrder)
            }
            states
        }
    }

    /**
     * 호출시, 기존에 작업 중이던 것들을 모두 취소하고, 처리 상태(SubmitUiState)를 기본 값으로 변경함
     */
    fun resetSubmitState() {
        _submitUiState.update {
            SubmitUiState.Default
        }
        viewModelScope.coroutineContext.cancelChildren()
    }
}