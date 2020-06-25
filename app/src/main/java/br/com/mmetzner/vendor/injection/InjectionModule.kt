package br.com.mmetzner.vendor.injection

import br.com.mmetzner.vendor.admin.map.MapViewModel
import br.com.mmetzner.vendor.admin.newclient.NewClientViewModel
import br.com.mmetzner.vendor.login.LoginViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val injectionModule = module {
    viewModel { LoginViewModel() }
    viewModel { MapViewModel() }
    viewModel { NewClientViewModel() }
}