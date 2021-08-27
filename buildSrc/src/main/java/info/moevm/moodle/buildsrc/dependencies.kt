package info.moevm.moodle.buildsrc

object Versions {
    const val ktlint = "0.40.0"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.1.0+"
    const val jdkDesugar = "com.android.tools:desugar_jdk_libs:1.1.1"

    const val timber = "com.jakewharton.timber:timber:4.7.1"

    const val material = "com.google.android.material:material:1.1.0"

    object Kotlin {
        private const val version = "1.5.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-alpha02"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha01"

        object Compose {
            const val snapshot = ""
            private const val version = "1.0.0"

            const val core = "androidx.compose.ui:ui:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val layout = "androidx.compose.foundation:foundation-layout:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$version"
            const val runtime = "androidx.compose.runtime:runtime:$version"
            const val runtimeLivedata = "androidx.compose.runtime:runtime-livedata:$version"
            const val animation = "androidx.compose.animation:animation:$version"
            const val tooling = "androidx.compose.ui:ui-tooling:$version"
            const val junitCompose = "androidx.compose.ui:ui-test-junit4:$version"
            const val uiTest = "androidx.compose.ui:ui-test:$version"
        }

        object Navigation {
            private const val version = "2.3.5"
            private const val nav_compose_version = "2.4.0-alpha05"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
            const val jetpackNavigation = "androidx.navigation:navigation-compose:$nav_compose_version"
        }

        object Material {
            private const val version = "1.2.0"
            const val material = "com.google.android.material:material:$version"
        }

        object Test {
            private const val version = "1.3.0"
            const val core = "androidx.test:core:$version"
            const val rules = "androidx.test:rules:$version"

            object Ext {
                private const val version = "1.1.2-rc01"
                const val junit = "androidx.test.ext:junit-ktx:$version"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
        }

        object Lifecycle {
            private const val version = "2.2.0"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val annotations = "androidx.lifecycle:lifecycle-compiler:$version"
            const val viewModelSaveState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$version"

        }
    }
}
