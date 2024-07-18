package com.bestapp.zipbab.di

import android.content.Context
import com.bestapp.zipbab.ui.signup.SignUpInputValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class HelperModule {

    @Provides
    @ViewModelScoped
    fun provideLoginValidator(@ApplicationContext context: Context): SignUpInputValidator {
        return SignUpInputValidator(context)
    }
}