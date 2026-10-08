package com.example.module6.task2.domain.usecase

import com.example.module6.task2.domain.model.LaureateDetail
import com.example.module6.task2.domain.model.LaureateItem
import com.example.module6.task2.domain.repository.InfoRepository


class GetLaureatesUseCase(private val repo: InfoRepository) {
    suspend operator fun invoke(year: Int?, category: String?): List<LaureateItem> =
        repo.getLaureates(year, category)
}

class GetLaureateDetailUseCase(private val repo: InfoRepository) {
    suspend operator fun invoke(id: String): LaureateDetail = repo.getLaureateDetail(id)
}