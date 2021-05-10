package com.wechat.moments.repos

import android.util.Log
import com.timespace.base.ext.toastOnUI
import com.timespace.base.network.HttpCode
import com.timespace.base.network.Result
import com.timespace.base.repos.IRemoteDataSource
import com.wechat.moments.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import retrofit2.Response
import java.io.IOException

open class BaseRemoteDataSource: IRemoteDataSource {
    companion object {
        private const val TAG = "BaseRemoteDataSource"
    }

    suspend fun <T: Any> safeApiCall(
        call: suspend () -> Result<T>,
        message: String
    ): Result<T> {
        return try {
            call()
        } catch (e: Exception) {
            Log.w(TAG, "${e.message}")
            toastOnUI(id = R.string.network_failed)
            Result.Error(IOException(message, e))
        }
    }

    suspend fun <T: Any> executeResponse(
        response: Response<T>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): Result<T> {
        return coroutineScope {
            Log.d(TAG, "executeResponse code = ${response.code()} message = ${response.message()}")
            val code = HttpCode.code(response.code())
            if (code == HttpCode.SUCCESS) {
                try {
                    successBlock?.let { it() }
                    Result.Success(response.body())
                } catch (e: Exception) {
                    Log.w(TAG, "executeResponse ${e.message}")
                    Result.Error(e)
                }
            } else {
                errorBlock?.let { it() }
                when(code) {
                    HttpCode.TIME_OUT_ERROR -> {
                        toastOnUI(id = R.string.bad_network)
                    }
                    HttpCode.TIMESTAMP_EXPIRED -> {
                        toastOnUI(id = R.string.network_timestamp_timeout)
                    }
                    else -> {
                        toastOnUI(id = R.string.network_server_exception)
                    }
                }
                Result.Error(IOException(response.message()))
            }
        }
    }
}