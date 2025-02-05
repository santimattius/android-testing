package com.santimattius.template.ui.rules

import org.koin.core.module.Module
import org.koin.test.KoinTestRule

/**
 * Loads a single Koin [Module] into the Koin container within the scope of this [KoinTestRule].
 *
 * This function simplifies the process of loading a module specifically for testing purposes.
 * It delegates the actual loading process to Koin's `loadModules` function.
 *
 * @param module The Koin [Module] to be loaded.
 * @param allowOverride If `true`, allows definitions in the loaded module to override
 * existing definitions. Defaults to `true`.
 * @param createEagerInstances If `true`, forces the creation of all eager instances
 * defined in the loaded module. Defaults to `false`.
 *
 * @see org.koin.core.Koin.loadModules
 * @see org.koin.core.module.Module
 * @see org.koin.test.KoinTestRule
 *
 * Example Usage:
 * ```
 * val myModule = module {
 *     single { MyDependency() }
 * }
 *
 * @get:Rule
 * val koinTestRule = KoinTestRule.create {
 *     // ... other configurations
 * }
 *
 * @Test
 * fun testMyComponent() {
 *   koinTestRule.loadModule(myModule)
 *   // ... now the module is loaded and ready for usage
 * }
 * ```
 */
fun KoinTestRule.loadModule(
    module: Module,
    allowOverride: Boolean = true,
    createEagerInstances: Boolean = false
) {
    koin.loadModules(
        modules = listOf(module),
        allowOverride = allowOverride,
        createEagerInstances = createEagerInstances
    )
}