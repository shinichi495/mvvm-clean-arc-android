package tech.central.showcase.post

import android.os.Build
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import tech.central.showcase.base.BaseTest
import tech.central.showcase.base.BaseTestHolderActivity
import tech.central.showcase.base.model.PostInfor
import tech.central.showcase.post.wrapper.Event
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import kotlin.test.assertEquals


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class PostViewModelTest : BaseTest() {
    override val isMockServerEnabled: Boolean
        get() = true

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Data Member
    private lateinit var activity: ActivityScenario<BaseTestHolderActivity>
    private lateinit var viewModel: PostViewModel
    private var shouldMockFailResponse by Delegates.notNull<Boolean>()

    override fun configureMockServer() {
        super.configureMockServer()
        if (isMockServerEnabled) {
            mockServer.dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    return when (val path = request.path) {
                        null -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                        else -> {
                            when {
                                path.contains("posts") -> {
                                    when {
                                        !shouldMockFailResponse -> mockResponseFromFile(
                                                fileName = "posts_whenSuccess.json",
                                                responseCode = HttpURLConnection.HTTP_OK
                                        )
                                        else -> mockResponseFromFile(
                                                fileName = "posts_whenFailed.json",
                                                responseCode = HttpURLConnection.HTTP_OK
                                        )
                                    }
                                }
                                path.contains("users") -> {
                                    when {
                                        !shouldMockFailResponse -> mockResponseFromFile(
                                                fileName = "users_whenSuccess.json",
                                                responseCode = HttpURLConnection.HTTP_OK
                                        )
                                        else -> mockResponseFromFile(
                                                fileName = "users_whenFailed.json",
                                                responseCode = HttpURLConnection.HTTP_OK
                                        )
                                    }
                                }
                                else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                            }
                        }
                    }
                }
            }
        }
    }

    @Before
    override fun setUp() {
        super.setUp()
        this.activity = ActivityScenario.launch(BaseTestHolderActivity::class.java)
        this.activity.onActivity { baseTestHolderActivity ->
            this.viewModel = ViewModelProvider(
                    baseTestHolderActivity,
                    baseTestHolderActivity.mViewModelFactory
            )
                    .get(PostViewModel::class.java)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun loadPost_whenSuccess() {
        shouldMockFailResponse = false

        // Observe
        this.viewModel.postsLiveData.observeForever { }

        // Check Initial
        assertEquals(
                null,
                this.viewModel.postsLiveData.value,
                "Before perform test initial state should be null."
        )

        // Perform
        this.viewModel.loadPosts()

        // Check result
        TimeUnit.SECONDS.sleep(1);
        assertEquals(
                true,
                this.viewModel.postsLiveData.value?.isNotEmpty(),
                "Post infor object should not be empty"
        )
    }

    @Test
    fun loadPost_whenFailed() {
        shouldMockFailResponse = true

        // Observe
        this.viewModel.postsLiveData.observeForever { }

        // Check Initial
        assertEquals(
                null,
                this.viewModel.postsLiveData.value,
                "Before perform test initial state should be null."
        )

        // Perform
        this.viewModel.loadPosts()
        TimeUnit.SECONDS.sleep(1);
        // Check result
        assertEquals(
                true,
                this.viewModel.postsLiveData.value?.isEmpty(),
                "Post should be empty"
        )
    }

    @Test
    fun sortAscending_whenLoadPostSuccess() {
        val expectedSort = mutableListOf<PostInfor>()
        val mockPostInfor1 = PostInfor(21, "asperiores ea ipsam voluptatibus modi minima quia sint", "repellat aliquid praesentium dolorem quo\nsed totam minus non itaque\nnihil labore molestiae sunt dolor eveniet hic recusandae veniam\ntempora et tenetur expedita sunt", "Clementine Bauch", "Nathan@yesenia.net")
        val mockPostInfor2 = PostInfor(11, "et ea vero quia laudantium autem", "delectus reiciendis molestiae occaecati non minima eveniet qui voluptatibus\n" +
                "accusamus in eum beatae sit\n" +
                "vel qui neque voluptates ut commodi qui incidunt\n" +
                "ut animi commodi", "Ervin Howell", "Shanna@melissa.tv")
        val mockPostInfor3 = PostInfor(1, "sunt aut facere repellat provident occaecati excepturi optio reprehenderit", "quia et suscipit\n" +
                "suscipit recusandae consequuntur expedita et cum\n" +
                "reprehenderit molestiae ut ut quas totam\n" +
                "nostrum rerum est autem sunt rem eveniet architecto", "Leanne Graham", "Sincere@april.biz")
        expectedSort.add(mockPostInfor1)
        expectedSort.add(mockPostInfor2)
        expectedSort.add(mockPostInfor3)
        loadPost_whenSuccess()
        this.viewModel.sort()
        // Check result
        assertEquals(
                "asperiores ea ipsam voluptatibus modi minima quia sint",
                this.viewModel.postsLiveData.value?.get(0)?.title,
                "Sort is wrong"
        )

        assertEquals(
                "et ea vero quia laudantium autem",
                this.viewModel.postsLiveData.value?.get(1)?.title,
                "Sort is wrong"
        )

        assertEquals(
                "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                this.viewModel.postsLiveData.value?.get(2)?.title,
                "Sort is wrong"
        )
    }

    @Test
    fun sortDescending_whenLoadPostSuccess() {
        val expectedSort = mutableListOf<PostInfor>()
        val mockPostInfor1 = PostInfor(21, "asperiores ea ipsam voluptatibus modi minima quia sint", "repellat aliquid praesentium dolorem quo\nsed totam minus non itaque\nnihil labore molestiae sunt dolor eveniet hic recusandae veniam\ntempora et tenetur expedita sunt", "Clementine Bauch", "Nathan@yesenia.net")
        val mockPostInfor2 = PostInfor(11, "et ea vero quia laudantium autem", "delectus reiciendis molestiae occaecati non minima eveniet qui voluptatibus\n" +
                "accusamus in eum beatae sit\n" +
                "vel qui neque voluptates ut commodi qui incidunt\n" +
                "ut animi commodi", "Ervin Howell", "Shanna@melissa.tv")
        val mockPostInfor3 = PostInfor(1, "sunt aut facere repellat provident occaecati excepturi optio reprehenderit", "quia et suscipit\n" +
                "suscipit recusandae consequuntur expedita et cum\n" +
                "reprehenderit molestiae ut ut quas totam\n" +
                "nostrum rerum est autem sunt rem eveniet architecto", "Leanne Graham", "Sincere@april.biz")
        expectedSort.add(mockPostInfor3)
        expectedSort.add(mockPostInfor2)
        expectedSort.add(mockPostInfor1)
        loadPost_whenSuccess()
        this.viewModel._sortType.value = Event("ascending")
        this.viewModel.sort()
        // Check result
        assertEquals(
                "asperiores ea ipsam voluptatibus modi minima quia sint",
                this.viewModel.postsLiveData.value?.get(2)?.title,
                "Sort is wrong"
        )

        assertEquals(
                "et ea vero quia laudantium autem",
                this.viewModel.postsLiveData.value?.get(1)?.title,
                "Sort is wrong"
        )

        assertEquals(
                "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                this.viewModel.postsLiveData.value?.get(0)?.title,
                "Sort is wrong"
        )
    }
}
