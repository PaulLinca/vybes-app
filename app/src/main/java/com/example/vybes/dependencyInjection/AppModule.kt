package com.example.vybes.dependencyInjection

import com.example.vybes.auth.service.AuthService
import com.example.vybes.auth.service.VybesAuthService
import com.example.vybes.feedback.service.DummyFeedbackService
import com.example.vybes.feedback.service.FeedbackService
import com.example.vybes.network.AuthInterceptor
import com.example.vybes.network.TokenAuthenticator
import com.example.vybes.network.VybesApiClient
import com.example.vybes.post.service.PostService
import com.example.vybes.post.service.VybesPostService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.ZonedDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .authenticator(TokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiClient(retrofit: Retrofit): VybesApiClient {
        return retrofit.create(VybesApiClient::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(vybesApiClient: VybesApiClient): AuthService {
        return VybesAuthService(vybesApiClient)
    }

    @Provides
    @Singleton
    fun provideVybeService(vybesApiClient: VybesApiClient): PostService {
        return VybesPostService(vybesApiClient)
    }

    @Provides
    @Singleton
    fun provideFeedbackService(): FeedbackService {
        return DummyFeedbackService()
    }
}