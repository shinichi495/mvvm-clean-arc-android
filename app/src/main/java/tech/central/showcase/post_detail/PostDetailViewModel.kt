package tech.central.showcase.post_detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.disposables.CompositeDisposable
import tech.central.showcase.base.extension.toBundle
import tech.central.showcase.base.model.PostInfor
import tech.central.showcase.di.factory.ViewModelAssistedFactory
import tech.central.showcase.post_detail.model.PostDetailViewState

class PostDetailViewModel @AssistedInject constructor(
        application: Application,
        @Assisted private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    companion object {
        private val TAG = PostDetailViewModel::class.java.simpleName
    }

    @AssistedInject.Factory
    interface Factory :
            ViewModelAssistedFactory<PostDetailViewModel>

    private val postDetailFragmentArgs by lazy {
        PostDetailFragmentArgs.fromBundle(savedStateHandle.toBundle)
    }

    // View State
    val postDetailViewState: MediatorLiveData<PostDetailViewState> by lazy {
        MediatorLiveData<PostDetailViewState>().apply {
            addSource(postLiveData) { source ->
                postDetailViewState.value = postDetailViewState.value?.copy(
                        post = source
                ) ?: PostDetailViewState(post = source)
            }
        }
    }

    // Live Data
    val postLiveData by lazy { MutableLiveData<PostInfor>() }

    // Data Members
    private val disposables by lazy { CompositeDisposable() }

    fun loadInit() {
        loadPost()
    }

    fun loadPost() {
        postLiveData.value = postDetailFragmentArgs.post
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

}