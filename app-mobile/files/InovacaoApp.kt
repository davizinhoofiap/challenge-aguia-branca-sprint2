// ═══════════════════════════════════════════════════════════
// InovacaoApp.kt  —  Application com @HiltAndroidApp
// ═══════════════════════════════════════════════════════════

package com.aguiabranca.inovacao

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * @HiltAndroidApp dispara a geração de código do Hilt e
 * torna o Application o container raiz de injeção de dependência.
 */
@HiltAndroidApp
class InovacaoApp : Application()


// ═══════════════════════════════════════════════════════════
// MainActivity.kt  —  atualizada com @AndroidEntryPoint
// ═══════════════════════════════════════════════════════════

// package com.aguiabranca.inovacao
//
// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.activity.enableEdgeToEdge
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.material3.Surface
// import androidx.compose.ui.Modifier
// import com.aguiabranca.inovacao.ui.navigation.AppNavGraph
// import com.aguiabranca.inovacao.ui.theme.AguiaBrancaTheme
// import dagger.hilt.android.AndroidEntryPoint
//
// @AndroidEntryPoint   // <-- necessário para usar hiltViewModel() na Activity
// class MainActivity : ComponentActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         enableEdgeToEdge()
//         setContent {
//             AguiaBrancaTheme {
//                 Surface(Modifier.fillMaxSize()) {
//                     AppNavGraph()  // usa NavGraphFirebase.kt
//                 }
//             }
//         }
//     }
// }
