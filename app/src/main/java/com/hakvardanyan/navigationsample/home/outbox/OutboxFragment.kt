package com.hakvardanyan.navigationsample.home.outbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.hakvardanyan.navigationsample.BaseFragment
import com.hakvardanyan.navigationsample.R
import com.hakvardanyan.navigationsample.databinding.FragmentOutboxBinding
import com.hakvardanyan.navigationsample.home.HomeGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class OutboxFragment : BaseFragment<FragmentOutboxBinding>() {

    override val bindingInitializer: (LayoutInflater) -> ViewBinding =
        FragmentOutboxBinding::inflate

    private val homeGraphViewModel: HomeGraphViewModel by viewModel(ownerProducer = {
        requireActivity().findNavController(R.id.app_nav_host_container).getBackStackEntry(R.id.homeFragment)
//        findHomeFragment(parentFragment) ?: this
    })

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val navController =
                requireNotNull(binding).nestedOutboxNavigationHost.getFragment<NavHostFragment>().navController
            navController.popBackStack(navController.graph.findStartDestination().id, false)
        }
    }

//    private fun findHomeFragment(currentFragment: Fragment?): HomeFragment? =
//        if (currentFragment is HomeFragment) {
//            currentFragment
//        } else {
//            findHomeFragment(currentFragment?.parentFragment)
//        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.apply {
            val navController =
                nestedOutboxNavigationHost.getFragment<NavHostFragment>().navController
            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
            addDestinationChangeListener(navController)

            sampleText.setOnClickListener {
                homeGraphViewModel.submitValue(Random.nextDouble().toString())
            }
        }
    }

    private fun addDestinationChangeListener(navController: NavController) {
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
                        when (it.id) {
                            controller.graph.findStartDestination().id -> onBackPressedCallback.isEnabled = false
                            controller.graph.id -> Unit
                            else -> onBackPressedCallback.isEnabled = true
                        }
                    }
                }
            })
    }
}
