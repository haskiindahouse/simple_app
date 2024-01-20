package account.operations

case class Operation(
  operationType: String,
  amount: BigDecimal,
  account1: String,
  account2: Option[String] = None
)
