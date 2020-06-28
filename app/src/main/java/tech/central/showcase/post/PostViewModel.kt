package tech.central.showcase.post

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import tech.central.showcase.base.SchedulersFacade
import tech.central.showcase.base.model.Post
import tech.central.showcase.base.model.PostInfor
import tech.central.showcase.post.usecase.LoadPostUseCase
import javax.inject.Inject

class PostViewModel @Inject constructor(
        application: Application,
        private val loadPostUseCase: LoadPostUseCase,
        private val schedulersFacede: SchedulersFacade
) : AndroidViewModel(application) {
    companion object {
        private val TAG = PostViewModel::class.java.simpleName
    }

    private val postsLiveData by lazy { MutableLiveData<List<PostInfor>>(null) }

    val sortTypeLiveData by lazy { MutableLiveData<String>("ascending") }

    val completedLoad by lazy { MutableLiveData<Boolean>(false) }

    private val diposables by lazy { CompositeDisposable() }

    fun loadPosts(): LiveData<List<PostInfor>> {
        if (postsLiveData.value.isNullOrEmpty()) {
            diposables += loadPostUseCase.execute()
                    .subscribeOn(schedulersFacede.io)
                    .observeOn(schedulersFacede.ui)
                    .subscribeBy(
                            onError = {
                                postsLiveData.value = emptyList()
                            },
                            onNext = { post ->
                                postsLiveData.value = post
                                completedLoad.value = true
                            }, onComplete = {
                        completedLoad.value = false
                    })
        }
        return postsLiveData
    }

    fun sort(type: String) {
        if (postsLiveData.value != null) {
            val lstPost = postsLiveData.value
            postsLiveData.value = if (type.equals("ascending")) lstPost?.sortedBy { it.title } else lstPost?.sortedByDescending { it.title }
        }
    }

    fun sort() {
        sortTypeLiveData.value = if (sortTypeLiveData.value.equals("ascending")) "descending" else "ascending"
    }


    override fun onCleared() {
        diposables.clear()
        super.onCleared()
    }
}