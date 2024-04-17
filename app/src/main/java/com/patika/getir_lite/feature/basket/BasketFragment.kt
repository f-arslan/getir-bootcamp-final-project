package com.patika.getir_lite.feature.basket

import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.patika.getir_lite.ProductViewModel
import com.patika.getir_lite.R
import com.patika.getir_lite.data.local.model.toDomainModel
import com.patika.getir_lite.databinding.FragmentBasketBinding
import com.patika.getir_lite.databinding.ItemBasketBinding
import com.patika.getir_lite.feature.BaseFragment
import com.patika.getir_lite.feature.ProductAdapter
import com.patika.getir_lite.feature.basket.adapter.HeaderAdapter
import com.patika.getir_lite.feature.basket.adapter.ProductListAdapter
import com.patika.getir_lite.model.BaseResponse
import com.patika.getir_lite.util.decor.DividerDecoration
import com.patika.getir_lite.util.ext.formatPrice
import com.patika.getir_lite.util.ext.scopeWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BasketFragment : BaseFragment<FragmentBasketBinding>() {

    private val viewModel: BasketViewModel by viewModels()
    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var basketProductAdapter: ProductAdapter<ItemBasketBinding>
    private lateinit var productListAdapter: ProductListAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBasketBinding = FragmentBasketBinding.inflate(inflater, container, false)

    override fun FragmentBasketBinding.onMain() {
        setupTextView()
        setupRecycleViewAndAdapters()
        listenBasketWithProducts()
        listenSuggestedProducts()
    }

    private fun FragmentBasketBinding.setupRecycleViewAndAdapters() {
        basketProductAdapter = ProductAdapter(
            bindingInflater = ItemBasketBinding::inflate,
            events = productViewModel::onEvent,
        )
        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        val margin = resources.getDimensionPixelSize(R.dimen.margin_8)
        divider?.let {
            rvBasket.addItemDecoration(DividerDecoration(divider, margin))
        }

        val headerAdapter = HeaderAdapter(getString(R.string.suggested_product))
        productListAdapter = ProductListAdapter(
            events = productViewModel::onEvent,
            onProductClick = ::navigateToDetailFragment
        )

        val concatAdapter = ConcatAdapter(basketProductAdapter, headerAdapter, productListAdapter)
        rvBasket.adapter = concatAdapter
    }

    private fun navigateToDetailFragment(productId: Long) {
        if (isAdded) {
            findNavController().navigate(
                BasketFragmentDirections.actionBasketFragmentToDetailFragment(productId)
            )
        }
    }

    private fun FragmentBasketBinding.listenSuggestedProducts() = with(productViewModel) {
        scopeWithLifecycle {
            suggestedProducts.collectLatest { response ->
                when (response) {
                    is BaseResponse.Error -> Unit
                    BaseResponse.Loading -> Unit

                    is BaseResponse.Success -> {
                        productListAdapter.saveData(response.data)
                    }
                }
            }
        }
    }

    private fun FragmentBasketBinding.listenBasketWithProducts() = with(viewModel) {
        scopeWithLifecycle {
            basketWithProducts.collectLatest { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        val data = response.data
                        val price = data.order.totalPrice.formatPrice()
                        basketProductAdapter.saveData(data.products.map { it.toDomainModel() })
                        tvFinalPrice.text = price
                        SpannableString(price).apply {
                            setSpan(StrikethroughSpan(), 0, price.length, 0)
                            tvOldPrice.text = this
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun FragmentBasketBinding.setupTextView() {
        layoutToolbar.textView.text = getString(R.string.basket_title)
    }
}
