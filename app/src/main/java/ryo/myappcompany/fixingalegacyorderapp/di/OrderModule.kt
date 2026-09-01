package ryo.myappcompany.fixingalegacyorderapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ryo.myappcompany.fixingalegacyorderapp.repository.FlashSaleRepository
import ryo.myappcompany.fixingalegacyorderapp.repository.FlashSaleRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderModule {

    @Singleton
    @Binds
    abstract fun bindFlashSaleRepository(
        flashSaleRepositoryImpl: FlashSaleRepositoryImpl
    ): FlashSaleRepository
}
