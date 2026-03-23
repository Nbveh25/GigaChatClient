package ru.kazan.itis.bikmukhametov.feature.register.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.RegisterUseCase
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.ValidateRegistrationUseCase
import ru.kazan.itis.bikmukhametov.feature.register.impl.domain.usecase.RegisterUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.register.impl.domain.usecase.ValidateRegistrationUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RegisterModule {

    @Binds
    @Singleton
    abstract fun bindRegisterUseCase(impl: RegisterUseCaseImpl): RegisterUseCase

    @Binds
    @Singleton
    abstract fun bindValidateRegistrationUseCase(impl: ValidateRegistrationUseCaseImpl): ValidateRegistrationUseCase
}
