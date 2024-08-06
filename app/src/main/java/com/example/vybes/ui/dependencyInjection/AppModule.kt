package com.example.vybes.ui.dependencyInjection

import com.example.vybes.ui.feedback.data.DummyFeedbackService
import com.example.vybes.ui.feedback.data.FeedbackService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFeedbackService(): FeedbackService {
        return DummyFeedbackService()
    }
}