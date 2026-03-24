package ru.kazan.itis.bikmukhametov.api.usecase

import ru.kazan.itis.bikmukhametov.api.model.ImageModel

interface SelectImageUseCase {
    suspend operator fun invoke(imageUriString: String): Result<ImageModel>
}
