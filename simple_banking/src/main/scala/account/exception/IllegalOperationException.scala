package account.exception

case class IllegalOperationException(val operationName: String) extends Exception(s"Illegal ${operationName}") {
}
