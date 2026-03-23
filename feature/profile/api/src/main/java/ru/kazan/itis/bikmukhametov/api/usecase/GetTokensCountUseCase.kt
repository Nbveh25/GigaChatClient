package ru.kazan.itis.bikmukhametov.api.usecase

import ru.kazan.itis.bikmukhametov.api.model.TokensCountModel

interface GetTokensCountUseCase {
    suspend operator fun invoke(): Result<TokensCountModel>
}
