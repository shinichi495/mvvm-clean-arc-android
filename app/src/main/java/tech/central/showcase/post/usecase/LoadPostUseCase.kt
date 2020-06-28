package tech.central.showcase.post.usecase

import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import tech.central.showcase.base.model.Post
import tech.central.showcase.base.model.PostInfor
import tech.central.showcase.base.model.User
import tech.central.showcase.base.provider.MockServiceProvider
import javax.inject.Inject

class LoadPostUseCase @Inject constructor(
        private val mockServiceProvider: MockServiceProvider
) {
    fun execute(): Flowable<List<PostInfor>> {

        val posts = mockServiceProvider.posts()

        val users = mockServiceProvider.users()

        return Flowable.zip(posts, users, BiFunction<List<Post>, List<User>, List<PostInfor>> { lstPost, lstUser ->
            combine(lstPost, lstUser)
        })

    }

    private fun combine(posts: List<Post>, users: List<User>): List<PostInfor> {
        val userById : Map<Int,User> = users.associateBy { user -> user.id }
        return  posts.filter {
            lstPost -> userById[lstPost.userId] != null
        }.map {post ->
            userById[post.userId].let {user ->
                PostInfor(post.id,post.title,post.body,user!!.name,user.email)
            }
        }
    }
}