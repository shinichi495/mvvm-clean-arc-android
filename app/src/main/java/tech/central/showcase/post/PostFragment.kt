package tech.central.showcase.post

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyItemSpacingDecorator
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_post.*
import tech.central.showcase.R
import tech.central.showcase.base.BaseFragment
import tech.central.showcase.post.controller.PostController
import javax.inject.Inject
import javax.inject.Provider

class PostFragment : BaseFragment() {

    //Injection
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mPostController: PostController

    @Inject
    lateinit var mLayoutManagerProvider: Provider<GridLayoutManager>

    @Inject
    lateinit var mItemDecoration: EpoxyItemSpacingDecorator

    private val mPostViewModel by activityViewModels<PostViewModel> { mViewModelFactory }
    private lateinit var mLayoutManager: GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_post, container, false)
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun init(savedInstanceState: Bundle?) {
        arguments?.apply { }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        mPostViewModel.loadPosts().observe(viewLifecycleOwner, Observer {
            mPostController.setData(it)
        })
        mPostViewModel.sortTypeLiveData.observe(viewLifecycleOwner, Observer {
            mPostViewModel.sort(it)
        })

        mPostViewModel.completedLoad.observe(viewLifecycleOwner, Observer { completed ->
                if (completed) {
                    Toast.makeText(context, getString(R.string.post_loaded), Toast.LENGTH_SHORT).show()
                }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu, menu)
        val sortAction = menu?.findItem(R.id.sortAction);
        sortAction?.setVisible(true);

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sortAction) {
            mPostViewModel.sort()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        activity.setTitle(getString(R.string.post))
        mLayoutManager = mLayoutManagerProvider.get()
        rcPosts.apply {
            adapter = mPostController.adapter
            layoutManager = mLayoutManager
            addItemDecoration(mItemDecoration)
        }
        subscriptions += mPostController.bindDetailRelay()
                .map { PostFragmentDirections.actionPostFragmentToPostDetailFragment(it) }
                .subscribeBy(
                        onError = {},
                        onNext = findNavController()::navigate
                )
    }
}