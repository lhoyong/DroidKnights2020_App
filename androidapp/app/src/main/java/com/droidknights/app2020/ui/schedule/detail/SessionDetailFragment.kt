package com.droidknights.app2020.ui.schedule.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import androidx.navigation.fragment.navArgs
import com.droidknights.app2020.R
import com.droidknights.app2020.base.BaseFragment
import com.droidknights.app2020.common.EventObserver
import com.droidknights.app2020.databinding.SessionDetailFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionDetailFragment : BaseFragment<SessionDetailViewModel, SessionDetailFragmentBinding>(
    R.layout.session_detail_fragment,
    SessionDetailViewModel::class.java
) {

    private val args: SessionDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSession(args.sessionId)

        //TODO : Speaker 표시

        initNestedScrollView()
        initBottomAppBar()
        initObserve()
    }

    private fun initNestedScrollView() {
        updatePaddingBottomByBottomAppBar()
    }

    private fun updatePaddingBottomByBottomAppBar() {
        binding.nsvSessionDetail.doOnPreDraw {
            val bottomAppBarHeight = binding.bottomAppBar.measuredHeight
            it.updatePadding(bottom = it.paddingBottom + bottomAppBarHeight)
        }
    }

    private fun initBottomAppBar() {
        setOnMenuItemClickListenerInBottomAppBar()
        updateHeightByBottomNavigationView()
    }

    private fun setOnMenuItemClickListenerInBottomAppBar() {
        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_qna -> {
                    viewModel.onClickQnALink()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener false
        }
    }

    private fun updateHeightByBottomNavigationView() {
        binding.bottomAppBar.doOnPreDraw {
            val bottomNavigationViewHeight = requireContext().resources.getDimensionPixelOffset(R.dimen.bottom_nav_height)
            it.updatePadding(bottom = it.paddingBottom + bottomNavigationViewHeight)
        }
    }

    private fun initObserve() {
        viewModel.videoEvent.observe(viewLifecycleOwner, EventObserver(this::openBrowser))

        viewModel.qnaEvent.observe(viewLifecycleOwner, EventObserver(this::openBrowser))

        viewModel.toastEvent.observe(viewLifecycleOwner, EventObserver(this::toastMessage))
    }

    private fun openBrowser(url: String) {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(requireContext(), url.toUri())
    }

    private fun toastMessage(@StringRes messageRes: Int) {
        Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
    }
}