package com.bestapp.zipbab.userlocation

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object LocationModule {

    @ViewModelScoped
    @Provides
    fun provideLocationClient(
        @ApplicationContext context: Context
    ): LocationService = LocationService(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )
}