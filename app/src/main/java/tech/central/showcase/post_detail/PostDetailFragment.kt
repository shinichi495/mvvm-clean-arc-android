package tech.central.showcase.post_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.item_body.*
import kotlinx.android.synthetic.main.list_item_pod.*
import tech.central.showcase.R
import tech.central.showcase.base.BaseFragment
import tech.central.showcase.base.model.Post
import tech.central.showcase.di.factory.assisted.SavedStateViewModelFactory
import tech.central.showcase.photo_detail.PhotoDetailViewModel
import javax.inject.Inject

class PostDetailFragment : BaseFragment() {
    companion object {
        @JvmStatic
        private val TAG = PostDetailFragment::class.java.simpleName
    }

    @Inject
    lateinit var mSavedStateViewModelFactory: SavedStateViewModelFactory

    private val mPostDetailViewModel by viewModels<PostDetailViewModel> { mSavedStateViewModelFactory }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_post_detail, container, false)
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Register ViewModel
        mPostDetailViewModel.postDetailViewState
                .observe(viewLifecycleOwner) {
                    with(it.post) {
                        txtTittle.text = title
                        txtEmail.text = email
                        txtUser.text = name
                        txtBody.text = body
                    }
                }
        mPostDetailViewModel.loadInit()
    }
}