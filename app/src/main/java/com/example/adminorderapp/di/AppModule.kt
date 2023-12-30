package com.example.adminorderapp.di

import com.example.adminorderapp.api.login.AuthService
import com.example.adminorderapp.api.TokenInterceptor
import com.example.adminorderapp.api.TokenInterceptorOkHttp
import com.example.adminorderapp.api.category.CategoryService
import com.example.adminorderapp.api.dispatchers.IODispatcher
import com.example.adminorderapp.api.manager.ManagerService
import com.example.adminorderapp.api.menuItem.MenuItemService
import com.example.adminorderapp.api.revenue.Revenue
import com.example.adminorderapp.api.revenue.RevenueService
import com.example.adminorderapp.api.shipper.ShipperService
import com.example.adminorderapp.api.store.StoreService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://terrier-modern-violently.ngrok-free.app")
            .addConverterFactory(MoshiConverterFactory.create())
    }
    @Provides
    @Singleton
    @TokenInterceptorOkHttp
    fun provideTokenInterceptorOkHttp(
        tokenInterceptor: TokenInterceptor
    ) : OkHttpClient = OkHttpClient.Builder().addInterceptor(tokenInterceptor).build()

    @Provides
    @Singleton
    fun provideLoginService(retrofitBuilder : Builder): AuthService
            = retrofitBuilder.build().create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideAuthenticateRetrofit(
        retrofitBuilder : Builder,
        @TokenInterceptorOkHttp okHttpClient: OkHttpClient
    ) : Retrofit = retrofitBuilder.client(okHttpClient).build()

    @Provides
    @Singleton
    fun provideManagerService(
        retrofit: Retrofit
    ) : ManagerService = retrofit.create(ManagerService::class.java)
    @Provides
    @Singleton
    fun provideShipperService(
        retrofit: Retrofit
    ) : ShipperService = retrofit.create(ShipperService::class.java)
    @Provides
    @Singleton
    fun provideStoreService(
        retrofit: Retrofit
    ) : StoreService = retrofit.create(StoreService::class.java)
    @Provides
    @Singleton
    fun provideMenuItemService(
        retrofit: Retrofit
    ) : MenuItemService = retrofit.create(MenuItemService::class.java)
    @Provides
    @Singleton
    @IODispatcher
    fun provideDispatcher() : CoroutineDispatcher = Dispatchers.IO
    @Provides
    @Singleton
    fun provideCategoryService(
        retrofit: Retrofit
    ) : CategoryService = retrofit.create(CategoryService::class.java)
    @Provides
    @Singleton
    fun provideRevenueService(
        retrofit: Retrofit
    ) : RevenueService = retrofit.create(RevenueService::class.java)
}