package tech.central.showcase.base.provider

import io.reactivex.Flowable
import io.reactivex.Single
import tech.central.showcase.base.model.Photo
import tech.central.showcase.base.model.Post
import tech.central.showcase.base.model.User
import tech.central.showcase.base.service.MockService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockServiceProvider @Inject constructor(
        private val mockService: MockService
) {
    companion object {
        @JvmStatic
        private val TAG = MockServiceProvider::class.java.simpleName
    }

    fun photos(): Single<List<Photo>> {
        return mockService.photos()
    }

    fun users() : Flowable<List<User>> {
        return mockService.users()
    }

    fun posts() : Flowable<List<Post>> {
        return mockService.posts()
    }
}