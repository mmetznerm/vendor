package br.com.mmetzner.vendor.injection

import br.com.mmetzner.vendor.LoginViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val injectionModule = module {
    viewModel { LoginViewModel() }
}