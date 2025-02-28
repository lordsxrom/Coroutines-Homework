package otus.homework.coroutines

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiContainer {

    @FactRetrofit
    @Provides
    fun provideFactRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://catfact.ninja/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @MeowRetrofit
    @Provides
    fun provideMeowRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://aws.random.cat/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideCatsService(@FactRetrofit retrofit: Retrofit): CatsService {
        return retrofit.create(CatsService::class.java)
    }

    @Provides
    fun provideMeowService(@MeowRetrofit retrofit: Retrofit): MeowService {
        return retrofit.create(MeowService::class.java)
    }
}

@Qualifier
annotation class FactRetrofit

@Qualifier
annotation class MeowRetrofit