/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.androidx.viewmodel.scope

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.androidx.viewmodel.createViewModelProvider
import org.koin.androidx.viewmodel.resolveInstance
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * Scope extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

inline fun <reified T : ViewModel> Scope.viewModel(
    noinline store: ViewModelStoreDefinition,
    noinline stateRegistry: SavedStateRegistryOwnerDefinition? = null,
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        getViewModel(T::class, store, stateRegistry, qualifier, state, parameters)
    }
}

inline fun <reified T : ViewModel> Scope.getViewModel(
    noinline store: ViewModelStoreDefinition,
    noinline stateRegistry: SavedStateRegistryOwnerDefinition? = null,
    qualifier: Qualifier? = null,
    noinline state: BundleDefinition? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getViewModel(T::class, store, stateRegistry, qualifier, state, parameters)
}

typealias SavedStateRegistryOwnerDefinition = () -> SavedStateRegistryOwner
typealias ViewModelStoreDefinition = () -> ViewModelStore

fun emptyState(): BundleDefinition = { Bundle() }
typealias BundleDefinition = () -> Bundle

fun <T : ViewModel> Scope.getViewModel(
    clazz: KClass<T>,
    store: ViewModelStoreDefinition,
    stateRegistry: SavedStateRegistryOwnerDefinition? = null,
    qualifier: Qualifier? = null,
    state: BundleDefinition? = null,
    parameters: ParametersDefinition? = null
): T {
    return getViewModel(
        ViewModelParameter(
            clazz,
            qualifier,
            parameters,
            state?.invoke(),
            store(),
            stateRegistry?.invoke()
        )
    )
}

fun <T : ViewModel> Scope.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
    val viewModelProvider = createViewModelProvider(viewModelParameters)
    return viewModelProvider.resolveInstance(viewModelParameters)
}