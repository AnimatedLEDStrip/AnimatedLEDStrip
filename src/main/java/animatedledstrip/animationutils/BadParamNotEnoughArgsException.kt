package animatedledstrip.animationutils

class BadParamNotEnoughArgsException(param: String) :
    BadParamException("Not enough arguments for $param")
