package com.patika.getir_lite.feature.listing

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.patika.getir_lite.ProductViewModel
import com.patika.getir_lite.model.BaseResponse
import com.patika.getir_lite.databinding.FragmentListingBinding
import com.patika.getir_lite.feature.BaseFragment
import com.patika.getir_lite.feature.listing.adapter.ProductAdapter
import com.patika.getir_lite.util.decor.GridSpacingItemDecoration
import com.patika.getir_lite.util.decor.MarginItemDecoration
import com.patika.getir_lite.util.ext.scopeWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ListingFragment : BaseFragment<FragmentListingBinding>() {

    private val productViewModel: ProductViewModel by activityViewModels()
    private val viewModel: ListingViewModel by viewModels()
    private lateinit var suggestedProductAdapter: ProductAdapter
    private lateinit var productAdapter: ProductAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListingBinding =
        FragmentListingBinding.inflate(inflater, container, false)

    override fun FragmentListingBinding.initializeViews() {
        setupAdapter()
        observeRemoteChanges()
    }

    private fun FragmentListingBinding.setupAdapter() {
        suggestedProductAdapter = ProductAdapter {

        }
        rvSuggestedProduct.adapter = suggestedProductAdapter
        rvSuggestedProduct.addItemDecoration(MarginItemDecoration())

        productAdapter = ProductAdapter { event -> viewModel.onEvent(event) }

        rvProduct.layoutManager = GridLayoutManager(requireContext(), 3)
        rvProduct.addItemDecoration(GridSpacingItemDecoration())
        rvProduct.adapter = productAdapter
    }

    private fun FragmentListingBinding.observeRemoteChanges() = with(productViewModel) {
        scopeWithLifecycle {
            products.collectLatest { response ->
                when (response) {
                    is BaseResponse.Error -> {}
                    BaseResponse.Loading -> {}

                    is BaseResponse.Success -> {
                        productAdapter.saveData(response.data)
                    }
                }
            }
        }

        scopeWithLifecycle {
            suggestedProducts.collectLatest { response ->
                when (response) {
                    is BaseResponse.Error -> {}
                    BaseResponse.Loading -> {
                        shimmerLayout.startShimmer()
                    }

                    is BaseResponse.Success -> {
                        delay(5000L)
                        suggestedProductAdapter.saveData(response.data)
                        shimmerLayout.run {
                            stopShimmer()
                            visibility = View.GONE
                        }
                        rvSuggestedProduct.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}
