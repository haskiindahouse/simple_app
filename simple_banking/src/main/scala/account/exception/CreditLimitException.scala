package account.exception

case class CreditLimitException(val limitAmount: String) extends Exception(s"Overlimits ${limitAmount}") {
}