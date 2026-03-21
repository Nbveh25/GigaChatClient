package ru.kazan.itis.bikmukhametov.feature.register.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.RegisterUseCase
import ru.kazan.itis.bikmukhametov.feature.register.impl.domain.usecase.RegisterUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RegisterModule {

    @Binds
    @Singleton
    abstract fun bindRegisterUseCase(impl: RegisterUseCaseImpl): RegisterUseCase
}
