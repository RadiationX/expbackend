package ru.radiationx.app.base

class BaseDataResponse<T>(
    val data: T? = null,
    error: BaseErrorContainer? = null
) : BaseResponse(error)