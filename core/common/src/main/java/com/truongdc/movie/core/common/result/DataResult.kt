/*
 * Designed and developed by 2024 truongdc21 (Dang Chi Truong)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.truongdc.movie.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class DataResult<out R> {

    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val throwable: Throwable) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()

    inline fun <M> map(block: (R) -> M): DataResult<M> {
        return when (this) {
            is Success -> Success(block(data))
            is Error -> Error(throwable)
            is Loading -> Loading
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[throwable=$throwable]"
            Loading -> "Loading"
        }
    }
}

fun <T> Flow<T>.asResult(): Flow<DataResult<T>> = map<T, DataResult<T>> { DataResult.Success(it) }
    .onStart { emit(DataResult.Loading) }
    .catch { emit(DataResult.Error(it)) }
