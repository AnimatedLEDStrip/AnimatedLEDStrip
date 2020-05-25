package animatedledstrip.animationutils

class MissingParamException(param: String) :
    BadParamException("Missing required parameter $param")
