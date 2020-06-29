package tech.central.showcase.post

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import tech.central.showcase.base.SchedulersFacade
import tech.central.showcase.base.model.PostInfor
import tech.central.showcase.post.usecase.LoadPostUseCase
import tech.central.showcase.post.wrapper.Event
import javax.inject.Inject

class PostViewModel @Inject constructor(
        application: Application,
        private val loadPostUseCase: LoadPostUseCase,
        private val schedulersFacede: SchedulersFacade
) : AndroidViewModel(application) {

    val postsLiveData by lazy { MutableLiveData<List<PostInfor>>(null) }

    val _sortType by lazy { MutableLiveData<Event<String>>() }

    private val completedLoad by lazy { MutableLiveData<Boolean>() }

    private val diposables by lazy { CompositeDisposable() }

    fun loadPosts(): LiveData<List<PostInfor>> {
        if (postsLiveData.value.isNullOrEmpty()) {
            diposables += loadPostUseCase.execute()
                    .observeOn(schedulersFacede.ui)
                    .subscribeBy(
                            onError = {
                                postsLiveData.postValue(emptyList())
                            },
                            onNext = { post ->
                                postsLiveData.postValue(post)
                                completedLoad.postValue(true)
                            }, onComplete = {
                                completedLoad.postValue(false)
                    })
        }
        return postsLiveData
    }

    fun sort() {
        if (postsLiveData.value != null) {
            this._sortType.value = if (_sortType.value?.peekContent().equals("ascending")) Event("descending")
                                        else Event("ascending")
            val lstPost = postsLiveData.value
            postsLiveData.value = if (_sortType.value?.peekContent().equals("ascending")) lstPost?.sortedBy { it.title }
                                    else lstPost?.sortedByDescending { it.title }
        }
    }

    fun sortType(): LiveData<Event<String>> {
        return _sortType
    }

    fun completedLoad(): LiveData<Boolean> {
        return completedLoad
    }

    override fun onCleared() {
        diposables.clear()
        super.onCleared()
    }
}