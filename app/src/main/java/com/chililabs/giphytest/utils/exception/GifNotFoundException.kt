package com.chililabs.giphytest.utils.exception

class GifNotFoundException(gifId: String) : NoSuchElementException("Gif $gifId not found")