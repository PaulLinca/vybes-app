package com.example.vybes.dependencyInjection

import android.content.Context
import com.example.vybes.auth.service.AuthService
import com.example.vybes.auth.service.DummyAuthService
import com.example.vybes.feedback.service.DummyFeedbackService
import com.example.vybes.feedback.service.FeedbackService
import com.example.vybes.post.service.DummyVybeService
import com.example.vybes.post.service.VybeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFeedbackService(): FeedbackService {
        return DummyFeedbackService()
    }

    @Provides
    fun provideVybeService(): VybeService {
        return DummyVybeService()
    }

    @Provides
    fun provideAuthService(): AuthService {
        return DummyAuthService()
    }

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
}