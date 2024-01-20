package account

import account.exception.IllegalOperationException
import account.utils.AmountUtils.DebitAmount
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.refineV

import scala.concurrent.Future

case class DebitAccount(id: String, balance: DebitAmount) extends Account[DebitAmount] {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def replenishment(amount: DebitAmount): DebitAccount =
    copy(balance = refineV[Positive](balance.value + amount.value).getOrElse(balance))

  override def withdrawal(amount: DebitAmount): Future[DebitAccount] =
    Future {
      if (balance.value >= amount.value) copy(balance = refineV[Positive](balance.value - amount.value).getOrElse(balance))
      else throw IllegalOperationException("withdrawal")
    }
}