package com.example.vybes.dependencyInjection

import com.example.vybes.BuildConfig
import com.example.vybes.auth.service.AuthService
import com.example.vybes.auth.service.VybesAuthService
import com.example.vybes.auth.setup.UserService
import com.example.vybes.auth.setup.VybesUserService
import com.example.vybes.feedback.service.FeedbackService
import com.example.vybes.feedback.service.VybesFeedbackService
import com.example.vybes.network.AuthInterceptor
import com.example.vybes.network.TokenAuthenticator
import com.example.vybes.network.VybesApiClient
import com.example.vybes.network.adapters.LocalDateTypeAdapter
import com.example.vybes.network.adapters.PostDeserializer
import com.example.vybes.network.adapters.ZonedDateTimeTypeAdapter
import com.example.vybes.post.model.Post
import com.example.vybes.post.service.PostService
import com.example.vybes.post.service.VybesPostService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
            .registerTypeAdapter(Post::class.java, PostDeserializer())
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .authenticator(TokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
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
    fun provideUserService(vybesApiClient: VybesApiClient): UserService {
        return VybesUserService(vybesApiClient)
    }

    @Provides
    @Singleton
    fun provideVybeService(vybesApiClient: VybesApiClient): PostService {
        return VybesPostService(vybesApiClient)
    }

    @Provides
    @Singleton
    fun provideFeedbackService(vybesApiClient: VybesApiClient): FeedbackService {
        return VybesFeedbackService(vybesApiClient)
    }
}