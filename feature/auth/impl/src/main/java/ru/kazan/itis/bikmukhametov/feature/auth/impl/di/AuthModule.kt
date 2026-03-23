package ru.kazan.itis.bikmukhametov.feature.auth.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.GetCurrentUserUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.SignInWithEmailPasswordUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.ValidateLoginUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.validation.InputValidator
import ru.kazan.itis.bikmukhametov.feature.auth.impl.data.repository.AuthRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.usecase.GetCurrentUserUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.usecase.SignInWithEmailPasswordUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.usecase.ValidateLoginUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.validation.InputValidatorImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSignInWithEmailPasswordUseCase(
        impl: SignInWithEmailPasswordUseCaseImpl,
    ): SignInWithEmailPasswordUseCase

    @Binds
    @Singleton
    abstract fun bindInputValidator(impl: InputValidatorImpl): InputValidator

    @Binds
    @Singleton
    abstract fun bindGetCurrentUserUseCase(impl: GetCurrentUserUseCaseImpl): GetCurrentUserUseCase

    @Binds
    @Singleton
    abstract fun bindValidateLoginUseCase(impl: ValidateLoginUseCaseImpl): ValidateLoginUseCase
}
