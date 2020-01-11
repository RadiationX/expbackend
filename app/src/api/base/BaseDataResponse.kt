package ru.radiationx.base

class BaseDataResponse<T>(
    val data: T? = null,
    error: BaseErrorContainer? = null
) : BaseResponse(error)