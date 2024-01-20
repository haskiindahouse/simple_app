package account

import account.exception.{CreditLimitException, IllegalAmountException}
import account.utils.AmountUtils.{CreditAmount, CreditLimit}
import eu.timepit.refined.refineV

import scala.concurrent.Future

case class CreditAccount(id: String, balance: CreditAmount, limit: CreditLimit) extends Account[CreditAmount] {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def replenishment(amount: CreditAmount): CreditAccount = {
    copy(balance = refineV[CreditAmount.Valid](balance.value + amount.value).getOrElse(balance))
  }

  override def withdrawal(amount: CreditAmount): Future[CreditAccount] =
    Future {
      if (balance.value - amount.value >= -limit.value)
        copy(balance = refineV[CreditAmount.Valid](balance.value - amount.value).getOrElse(balance))
      else throw CreditLimitException(limit.value.toString)
    }
}