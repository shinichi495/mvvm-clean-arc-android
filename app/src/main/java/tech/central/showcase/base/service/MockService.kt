package tech.central.showcase.base.service

import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import tech.central.showcase.base.model.Photo
import tech.central.showcase.base.model.Post
import tech.central.showcase.base.model.User

interface MockService {
    /**
     * Get list of photos from mock api
     */
    @GET("photos")
    fun photos(
    ): Single<List<Photo>>



    @GET("users")
    fun users () : Flowable<List<User>>

    @GET("posts")
    fun posts () : Flowable<List<Post>>
}