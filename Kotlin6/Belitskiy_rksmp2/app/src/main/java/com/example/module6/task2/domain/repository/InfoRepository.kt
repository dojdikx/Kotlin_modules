package com.example.module6.task2.domain.repository

import com.example.module6.task2.domain.model.LaureateDetail
import com.example.module6.task2.domain.model.LaureateItem


interface InfoRepository {
    suspend fun getLaureates(year: Int?, category: String?): List<LaureateItem>
    suspend fun getLaureateDetail(id: String): LaureateDetail
}