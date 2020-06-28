package tech.central.showcase.post.controller.model

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxrelay2.Relay
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.list_item_pod.view.*
import tech.central.showcase.R
import tech.central.showcase.base.epoxy.EpoxyBaseViewHolder
import tech.central.showcase.base.model.PostInfor

@EpoxyModelClass(layout = R.layout.list_item_pod)
abstract class PostModel : EpoxyModelWithHolder<EpoxyBaseViewHolder>() {
    @EpoxyAttribute
    lateinit var post: PostInfor

    @EpoxyAttribute
    lateinit var detailRelay: Relay<PostInfor>
    override fun bind(holder: EpoxyBaseViewHolder) {
        holder.itemView.apply {
            with(post) {
                txtEmail.text = email
                txtTittle.text = title
                txtUser.text = name
            }

            itemContent.clicks()
                    .map { post }
                    .subscribeBy(
                            onNext = detailRelay::accept
                    )
        }
    }
}