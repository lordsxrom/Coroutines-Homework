package otus.homework.coroutines

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val catsService: CatsService,
    private val meowService: MeowService,
) : ViewModel() {

    private val _viewObject = MutableLiveData<ResultOf<CatsVO>>()
    val viewObject: LiveData<ResultOf<CatsVO>> = _viewObject

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Что-то пошло не так", throwable)
        CrashMonitor.trackWarning()
    }

    fun onInitComplete() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                _viewObject.value = ResultOf.Success(
                    value = withContext(Dispatchers.IO) {
                        val factDeferred = async { catsService.getCatFact() }
                        val meowDeferred = async { meowService.getCatImage() }
                        CatsVO(
                            fact = factDeferred.await().fact,
                            imageUrl = meowDeferred.await().imageUrl
                        )
                    }
                )
            } catch (throwable: Throwable) {
                when (throwable) {
                    is SocketTimeoutException -> {
                        Log.e(TAG, "Не удалось получить ответ от сервера", throwable)
                        _viewObject.value = ResultOf.Failure(
                            message = "Не удалось получить ответ от сервера",
                            throwable = throwable
                        )
                    }
                    else -> throw throwable
                }
            }
        }
    }

    companion object {
        private const val TAG = "CatsViewModel"
    }
}