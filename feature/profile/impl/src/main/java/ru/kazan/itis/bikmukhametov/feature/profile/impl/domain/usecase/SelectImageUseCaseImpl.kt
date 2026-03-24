package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.api.model.ImageModel
import ru.kazan.itis.bikmukhametov.api.resource.ImageResourceProvider
import ru.kazan.itis.bikmukhametov.api.usecase.SelectImageUseCase
import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider
import ru.kazan.itis.bikmukhametov.feature.profile.impl.R
import javax.inject.Inject

internal class SelectImageUseCaseImpl @Inject constructor(
    private val imageResourceProvider: ImageResourceProvider,
    private val stringResources: StringResourceProvider,
) : SelectImageUseCase {

    override suspend fun invoke(imageUriString: String): Result<ImageModel> = withContext(Dispatchers.IO) {
        runCatchingCancelable {
            val inputStream = imageResourceProvider.openInputStream(imageUriString)
                ?: error(stringResources.getString(R.string.profile_select_image_error_open_failed))

            val imageBytes = inputStream.use { it.readBytes() }

            require(imageBytes.size <= MAX_FILE_SIZE_BYTES) {
                stringResources.getString(
                    R.string.profile_select_image_error_file_too_large,
                    MAX_FILE_SIZE_MB,
                )
            }

            val fileName = imageResourceProvider.getFileName(imageUriString)
                ?: stringResources.getString(
                    R.string.profile_select_image_default_file_name,
                    System.currentTimeMillis(),
                )

            ImageModel(imageBytes, fileName)
        }
    }

    private companion object {
        private const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024
        private val MAX_FILE_SIZE_MB = MAX_FILE_SIZE_BYTES / (1024 * 1024)
    }
}
