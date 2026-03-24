package ru.kazan.itis.bikmukhametov.common.util.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.common.util.error.ErrorMessageMapper
import ru.kazan.itis.bikmukhametov.common.util.error.ErrorMessageMapperImpl
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResourceModule {

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): StringResourceProvider {
        return StringResourceProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideErrorMessageMapper(
        stringResourceProvider: StringResourceProvider,
    ): ErrorMessageMapper {
        return ErrorMessageMapperImpl(stringResourceProvider)
    }

}
