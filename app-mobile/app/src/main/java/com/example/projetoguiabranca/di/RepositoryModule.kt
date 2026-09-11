package com.example.projetoguiabranca.di

import com.example.projetoguiabranca.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindEstrategiaRepository(
        impl: EstrategiaRepositoryImpl
    ): EstrategiaRepository

    @Binds
    @Singleton
    abstract fun bindIdeiaRepository(
        impl: IdeiaRepositoryImpl
    ): IdeiaRepository

    @Binds
    @Singleton
    abstract fun bindProjetoRepository(
        impl: ProjetoRepositoryImpl
    ): ProjetoRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        impl: DashboardRepositoryImpl
    ): DashboardRepository
}
