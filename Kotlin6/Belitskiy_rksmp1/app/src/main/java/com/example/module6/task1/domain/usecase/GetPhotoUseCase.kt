package com.example.module6.task1.domain.usecase

import com.example.module6.task1.domain.model.Photo
import com.example.module6.task1.domain.repository.PhotoRepository

class GetPhotoUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(): List<Photo> = repository.getPhotos()
}