package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.api.model.ImageModel
import ru.kazan.itis.bikmukhametov.api.resource.ImageResourceProvider
import ru.kazan.itis.bikmukhametov.api.usecase.SelectImageUseCase
import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import java.io.IOException
import javax.inject.Inject

internal class SelectImageUseCaseImpl @Inject constructor(
    private val imageResourceProvider: ImageResourceProvider
): SelectImageUseCase {
    override suspend fun invoke(imageUriString: String): Result<ImageModel> = withContext(Dispatchers.IO) {
        runCatchingCancelable {

            // Открываем поток для чтения
            val inputStream = imageResourceProvider.openInputStream(imageUriString)
                ?: error("Не удалось открыть файл")

            // Читаем данные из потока
            val imageBytes = inputStream.use { it.readBytes() }

            require(imageBytes.size <= MAX_FILE_SIZE_BYTES) {
                "Файл слишком большой. Максимальный размер: $MAX_FILE_SIZE_MB МБ"
            }

            // Формируем имя файла
            val fileName = imageResourceProvider.getFileName(imageUriString)
                ?: "profile_photo_${System.currentTimeMillis()}.jpg"

            ImageModel(imageBytes, fileName)
        }
    }

    private companion object {
        private const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024 // 10 МБ
        private const val MAX_FILE_SIZE_MB = MAX_FILE_SIZE_BYTES / (1024 * 1024)
    }
}
