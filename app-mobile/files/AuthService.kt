package com.aguiabranca.inovacao.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serviço de autenticação Firebase.
 * Expõe o estado do usuário como Flow para que a UI reaja
 * automaticamente a login/logout sem polling manual.
 */
@Singleton
class AuthService @Inject constructor(
    private val auth: FirebaseAuth
) {

    // Flow que emite null (deslogado) ou FirebaseUser (logado)
    // callbackFlow converte o listener de callback em coroutine-friendly Flow
    val usuarioAtualFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val usuarioAtual: FirebaseUser? get() = auth.currentUser

    val estaLogado: Boolean get() = auth.currentUser != null

    // ── Login com e-mail e senha ──────────────────────────────────────

    suspend fun loginComEmail(email: String, senha: String): Result<FirebaseUser> {
        return try {
            val resultado = auth.signInWithEmailAndPassword(email, senha).await()
            Result.success(resultado.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Login com Google (recebe o idToken do GoogleSignIn) ───────────

    suspend fun loginComGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val resultado = auth.signInWithCredential(credential).await()
            Result.success(resultado.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Cadastro de novo usuário ──────────────────────────────────────

    suspend fun cadastrar(email: String, senha: String): Result<FirebaseUser> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(email, senha).await()
            Result.success(resultado.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()
}
