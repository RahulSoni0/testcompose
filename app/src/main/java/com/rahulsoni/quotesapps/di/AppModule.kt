package com.rahulsoni.quotesapps.di

import com.rahulsoni.quotesapps.data.remote.NewsApi
import com.rahulsoni.quotesapps.data.remote.NewsRepositoryImpl
import com.rahulsoni.quotesapps.data.remote.RemoteNewsDataSource
import com.rahulsoni.quotesapps.domain.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRemoteNewsDataSource(newsApi: NewsApi): RemoteNewsDataSource {
        return RemoteNewsDataSource(newsApi)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        remoteDataSource: RemoteNewsDataSource
    ): NewsRepository {
        return NewsRepositoryImpl(remoteDataSource)
    }

}