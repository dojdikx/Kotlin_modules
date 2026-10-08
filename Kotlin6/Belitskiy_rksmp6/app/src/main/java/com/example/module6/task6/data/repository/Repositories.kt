package com.example.module6.task6.data.repository

import com.example.module6.task6.data.LaureateDto
import com.example.module6.task6.data.PrizeDto
import com.example.module6.task6.data.RemoteApi
import com.example.module6.task6.data.TokenStore
import com.example.module6.task6.domain.AuthRepository
import com.example.module6.task6.domain.FavoriteRepository
import com.example.module6.task6.domain.Laureate
import com.example.module6.task6.domain.Prize
import com.example.module6.task6.domain.PrizeRepository
import kotlinx.coroutines.flow.Flow

internal fun LaureateDto.toDomain() = Laureate(id, fullName, portion, motivation, portraitUrl)

internal fun PrizeDto.toDomain() = Prize(
    id, awardYear, category, fullName, motivation, detailLink,
    laureates.map { it.toDomain() }
)

class AuthRepositoryImpl(
    private val api: RemoteApi,
    private val store: TokenStore
) : AuthRepository {
    override val tokenFlow: Flow<String?> = store.tokenFlow

    override suspend fun login(username: String, password: String) {
        val resp = api.login(username, password)
        store.saveToken(resp.token)
    }

    override suspend fun logout() = store.clear()
}

class PrizeRepositoryImpl(private val api: RemoteApi) : PrizeRepository {
    override suspend fun getPrizes(): List<Prize> = api.getPrizes().map { it.toDomain() }
}

class FavoriteRepositoryImpl(
    private val api: RemoteApi,
    private val store: TokenStore
) : FavoriteRepository {
    private suspend fun token(): String = store.getToken() ?: error("No token")

    override suspend fun getFavorites(): List<Prize> =
        api.getFavorites(token()).map { it.toDomain() }

    override suspend fun add(prizeId: Int) { api.addFavorite(token(), prizeId) }
    override suspend fun remove(prizeId: Int) { api.removeFavorite(token(), prizeId) }
}
