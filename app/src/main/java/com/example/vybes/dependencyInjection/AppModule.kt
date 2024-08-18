package com.example.vybes.dependencyInjection

import android.content.Context
import com.example.vybes.auth.service.AuthService
import com.example.vybes.auth.service.VybesAuthService
import com.example.vybes.feedback.service.DummyFeedbackService
import com.example.vybes.feedback.service.FeedbackService
import com.example.vybes.network.AuthInterceptor
import com.example.vybes.network.VybesApiClient
import com.example.vybes.post.service.PostService
import com.example.vybes.post.service.VybesPostService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): VybesApiClient {
        return retrofit.create(VybesApiClient::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(vybesApiClient: VybesApiClient): AuthService {
        return VybesAuthService(vybesApiClient)
    }

    @Provides
    @Singleton
    fun provideFeedbackService(): FeedbackService {
        return DummyFeedbackService()
    }

    @Provides
    @Singleton
    fun provideVybeService(vybesApiClient: VybesApiClient): PostService {
        return VybesPostService(vybesApiClient)
    }
}