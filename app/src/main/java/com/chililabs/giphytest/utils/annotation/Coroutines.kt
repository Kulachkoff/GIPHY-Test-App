package com.chililabs.giphytest.utils.annotation

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppCoroutineScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServiceCoroutineScope