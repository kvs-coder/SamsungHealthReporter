package com.kvs.samsunghealthreporter

class SamsungHealthInitializationException: Exception("Shealth is not supported. Package not found")
class SamsungHealthConnectionException(cause: String): Exception(cause)
class SamsungHealthWriteException(cause: String): Exception(cause)
class SamsungHealthTypeException(cause: String): Exception(cause)
