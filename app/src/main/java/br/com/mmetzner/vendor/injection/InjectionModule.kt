package br.com.mmetzner.vendor.injection

import br.com.mmetzner.vendor.admin.neworder.client.SelectClientViewModel
import br.com.mmetzner.vendor.admin.map.MapViewModel
import br.com.mmetzner.vendor.admin.newclient.NewClientViewModel
import br.com.mmetzner.vendor.admin.neworder.product.SelectProductViewModel
import br.com.mmetzner.vendor.admin.newpayment.NewPaymentViewModel
import br.com.mmetzner.vendor.admin.newproduct.NewProductViewModel
import br.com.mmetzner.vendor.helper.detail.DetailViewModel
import br.com.mmetzner.vendor.helper.order.OrderViewModel
import br.com.mmetzner.vendor.helper.route.StartRouteViewModel
import br.com.mmetzner.vendor.login.LoginViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val injectionModule = module {
    viewModel { LoginViewModel() }
    viewModel { MapViewModel() }
    viewModel { NewClientViewModel() }
    viewModel { NewProductViewModel() }
    viewModel { NewPaymentViewModel() }
    viewModel { SelectClientViewModel() }
    viewModel { SelectProductViewModel() }
    viewModel { OrderViewModel() }
    viewModel { DetailViewModel() }
    viewModel { StartRouteViewModel() }
}