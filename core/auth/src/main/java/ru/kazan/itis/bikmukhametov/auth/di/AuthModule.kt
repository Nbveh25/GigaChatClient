package ru.kazan.itis.bikmukhametov.auth.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.auth.data.datasource.AuthDataSource
import ru.kazan.itis.bikmukhametov.auth.data.datasource.AuthRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAuthProvideModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAuthDataSource(impl: AuthRemoteDataSource): AuthDataSource
}
