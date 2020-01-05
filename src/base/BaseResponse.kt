package ru.radiationx.base

import ru.radiationx.base.BaseErrorContainer

open class BaseResponse(
    open val error: BaseErrorContainer? = null
)