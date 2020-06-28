package tech.central.showcase.post.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import tech.central.showcase.base.epoxy.view.epoxyLoadingView
import tech.central.showcase.base.model.PostInfor
import tech.central.showcase.post.controller.model.post
import javax.inject.Inject

class PostController @Inject constructor() : TypedEpoxyController<List<PostInfor>>() {

    companion object {
        @JvmStatic
        private val TAG = PostController::class.java.simpleName
    }

    private val detailRelay by lazy { PublishRelay.create<PostInfor>() }

    override fun buildModels(data: List<PostInfor>?) {
        if (data == null) {
            epoxyLoadingView {
                id("loading")
            }
        } else if (data.isNotEmpty()) {
            data.forEach { post ->
                post {
                    id(post.id)
                    post(post)
                    detailRelay(detailRelay)
                }
            }
        }
    }


    fun bindDetailRelay(): Observable<PostInfor> = detailRelay
}