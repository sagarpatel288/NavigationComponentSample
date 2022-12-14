package com.hakvardanyan.navigationsample.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.hakvardanyan.navigationsample.BaseFragment
import com.hakvardanyan.navigationsample.R
import com.hakvardanyan.navigationsample.databinding.FragmentHomeContainerBinding
import com.hakvardanyan.navigationsample.ext.findRootNavController
import com.hakvardanyan.navigationsample.main.MainGraphViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeContainerFragment : BaseFragment<FragmentHomeContainerBinding>() {

    override val bindingInitializer: (LayoutInflater) -> ViewBinding = FragmentHomeContainerBinding::inflate

    private val mainGraphViewModel: MainGraphViewModel by viewModel(ownerProducer = {
        findRootNavController().getBackStackEntry(R.id.mainFragment)
    })

    private val navController by lazy {
        requireNotNull(binding).nestedHomeNavigationHost.getFragment<NavHostFragment>().navController
    }

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navController.popBackStack(navController.graph.findStartDestination().id, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
        addDestinationChangeListener()
        mainGraphViewModel.toolbarBackEvent
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { navController.popBackStack() }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun addDestinationChangeListener() {
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    if (binding == null) {
                        controller.removeOnDestinationChangedListener(this)
                        return
                    }

                    destination.hierarchy.forEach {
                        submitToolbarTitle(it.id)
                        configureBackNavigation(it.id)
                    }
                }
            })
    }

    private fun submitToolbarTitle(destinationId: Int) = when (destinationId) {
        R.id.tradingFragment -> R.string.home
        R.id.homeFragmentChild1 -> R.string.home_child_1
        R.id.homeFragmentChild2 -> R.string.home_child_2
        R.id.homeFragmentChild3 -> R.string.home_child_3
        R.id.coinDetailFragment -> R.string.coin_details
        else -> null
    }?.let(mainGraphViewModel::submitToolbarTitle)

    private fun configureBackNavigation(destinationId: Int) = when (destinationId) {
        navController.graph.findStartDestination().id -> {
            onBackPressedCallback.isEnabled = false
            mainGraphViewModel.showToolbarBackButton(false)
        }
        navController.graph.id -> Unit
        else -> {
            onBackPressedCallback.isEnabled = true
            mainGraphViewModel.showToolbarBackButton(true)
        }
    }
}
