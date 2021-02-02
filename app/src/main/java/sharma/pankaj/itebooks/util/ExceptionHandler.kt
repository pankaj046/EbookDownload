package sharma.pankaj.itebooks.util

import java.io.IOException

class ApiException(message: String) : IOException(message)
class InternetException(message: String) : IOException(message)