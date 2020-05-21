package animatedledstrip.animationutils

class BadParamException(param: String, reason: BadParamReason) :
    Exception(when (reason) {
        BadParamReason.NOT_ENOUGH_ARGS -> "Not enough arguments for $param"
        BadParamReason.INVALID_PARAMETER -> "Invalid parameter $param"
        BadParamReason.INVALID_TYPE -> "Invalid type for parameter $param"
        BadParamReason.MISSING -> "Missing required parameter $param"
    })