package com.bestapp.zipbab.ui.profilepostimageselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.bestapp.zipbab.data.repository.GalleryRepository
import com.bestapp.zipbab.data.repository.UserRepository
import com.bestapp.zipbab.model.toPostGalleryState
import com.bestapp.zipbab.model.toSelectUiState
import com.bestapp.zipbab.ui.profilepostimageselect.model.PostGalleryUiState
import com.bestapp.zipbab.ui.profilepostimageselect.model.SelectedImageUiState
import com.bestapp.zipbab.ui.profilepostimageselect.model.SubmitInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostImageSelectViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
) : ViewModel() {

    private val pagingDataFlow: Flow<PagingData<PostGalleryUiState>> = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, initialLoadSize = ITEMS_PER_PAGE * 2),
        pagingSourceFactory = { galleryRepository.galleryPagingSource() }
    ).flow
        .map { pagingData ->
            pagingData.map {
                it.toPostGalleryState()
            }
        }.cachedIn(viewModelScope)

    // 각 아이템의 클릭된 순서는 입력된 아이템 순서를 따르도록 하기 위해 LinkedMap을 사용했다.
    // LinkedMap을 타입으로 지정하면 객체가 변하지 않아서 combine 함수에서 소스 변화를 감지하지 못해서 타입은 Map으로 지정했다.
    private val _selectedImageStatesFlow = MutableStateFlow<Map<String, SelectedImageUiState>>(linkedMapOf())
    val selectedImageStatesFlow: StateFlow<Map<String, SelectedImageUiState>> = _selectedImageStatesFlow.asStateFlow()

    val imageStatePagingDataFlow = combine(pagingDataFlow, _selectedImageStatesFlow) { paging, selectedState ->
        paging.map { postState ->
            val order = selectedState.keys.indexOfFirst {
                it == postState.uri.path
            }
            return@map if (order == -1) {
                postState.copy(order = PostGalleryUiState.NOT_SELECTED_ORDER)
            } else {
                postState.copy(order = order+1)
            }
        }
    }

    private val _submitInfo = MutableSharedFlow<SubmitInfo>()
    val submitInfo: SharedFlow<SubmitInfo> = _submitInfo.asSharedFlow()

    fun submit(userDocumentID: String) {
        viewModelScope.launch {
            _submitInfo.emit(SubmitInfo(
                userDocumentID,
                selectedImageStatesFlow.value.map {
                    it.value.uri.toString()
                }
            ))
        }
    }

    fun update(state: PostGalleryUiState) {
        viewModelScope.launch {
            _selectedImageStatesFlow.update {
                val data = it.toMutableMap()

                val path = state.uri.path
                if (path in data) {
                    data.remove(path)
                } else if (path != null){
                    data[path] = state.toSelectUiState()
                }
                data.toMap()
            }
        }
    }

    companion object {
        private const val ITEMS_PER_PAGE = 27
    }
}