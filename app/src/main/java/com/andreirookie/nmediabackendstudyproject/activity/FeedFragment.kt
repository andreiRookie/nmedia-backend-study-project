package com.andreirookie.nmediabackendstudyproject.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.andreirookie.nmediabackendstudyproject.adapter.OnInteractionListener
import com.andreirookie.nmediabackendstudyproject.adapter.PostsAdapter
import com.andreirookie.nmediabackendstudyproject.dto.Post
import com.andreirookie.nmediabackendstudyproject.viewModel.PostViewModel
import com.andreirookie.nmediabackendstudyproject.R
import com.andreirookie.nmediabackendstudyproject.databinding.FragmentFeedBinding


class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
            // либо переписать метод c добавлением флага ->
            // viewModel.likeById(post.id, post.likedById)
                if (post.likedByMe) {
                    viewModel.dislikeById(post.id)
                } else {
                    viewModel.likeById(post.id)
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        // SwipeRefresh
        val swipeRefreshBinding = binding.swipeRefreshLayout
        swipeRefreshBinding.setColorSchemeColors(
            R.color.colorAccent,
            R.color.colorPrimary,
            R.color.colorPrimaryDark)
        swipeRefreshBinding.setOnRefreshListener {
            viewModel.loadPosts()
            swipeRefreshBinding.isRefreshing = false
        }

        return binding.root
    }
}