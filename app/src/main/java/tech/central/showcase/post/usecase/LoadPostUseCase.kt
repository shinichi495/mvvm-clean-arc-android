package tech.central.showcase.post.usecase

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import tech.central.showcase.base.model.Post
import tech.central.showcase.base.model.PostInfor
import tech.central.showcase.base.model.User
import tech.central.showcase.base.provider.MockServiceProvider
import javax.inject.Inject


/*
*   Order to load multi network parallel I have 2 suggestion from chavit with this link
*   https://android.jlelse.eu/rxjava-parallelization-concurrency-zip-operator-fe87a36441ff
*   and from research with this link
*   https://proandroiddev.com/rxjava-2-parallel-multiple-network-call-made-easy-1e1f14163eef
*   I will use Observable (https://android.jlelse.eu/rxjava-parallelization-concurrency-zip-operator-fe87a36441ff)
*   and try with Flowable in an other one
*
* */

class LoadPostUseCase @Inject constructor(
        private val mockServiceProvider: MockServiceProvider
) {

    fun execute(): Observable<List<PostInfor>> {

        val posts = mockServiceProvider.posts()

        val users = mockServiceProvider.users()

        return Observable.zip(
                posts.subscribeOn(Schedulers.io()),
                users.subscribeOn(Schedulers.io()),
                BiFunction { lstPost, lstUser -> combine(lstPost, lstUser) }
        )

    }

    private fun combine(posts: List<Post>, users: List<User>): List<PostInfor> {
        val userById: Map<Int, User> = users.associateBy { user -> user.id }
        return posts.filter { lstPost ->
            userById[lstPost.userId] != null
        }.map { post ->
            userById[post.userId].let { user ->
                if (user != null) {
                    PostInfor(post.id, post.title, post.body, user.name, user.email)
                } else PostInfor(post.id, post.title, post.body, "", "")

            }
        }
    }
}