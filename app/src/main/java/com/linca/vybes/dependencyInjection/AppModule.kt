package com.linca.vybes.dependencyInjection

import com.google.firebase.auth.FirebaseAuth
import com.linca.vybes.BuildConfig
import com.linca.vybes.auth.service.AuthService
import com.linca.vybes.auth.service.VybesAuthService
import com.linca.vybes.auth.setup.UserService
import com.linca.vybes.auth.setup.VybesUserService
import com.linca.vybes.feedback.service.FeedbackService
import com.linca.vybes.feedback.service.VybesFeedbackService
import com.linca.vybes.model.Post
import com.linca.vybes.network.VybesApiClient
import com.linca.vybes.network.adapters.LocalDateTypeAdapter
import com.linca.vybes.network.adapters.PostDeserializer
import com.linca.vybes.network.adapters.ZonedDateTimeTypeAdapter
import com.linca.vybes.post.PostsRepository
import com.linca.vybes.post.service.PostService
import com.linca.vybes.post.service.VybesPostService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.linca.vybes.network.interceptor.FirebaseAuthInterceptor
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
import java.util.concurrent.TimeUnit
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
        val firebaseAuth = FirebaseAuth.getInstance()
        val authInterceptor = FirebaseAuthInterceptor(firebaseAuth)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
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

    @Provides
    @Singleton
    fun providePostsRepository(): PostsRepository {
        return PostsRepository()
    }
}