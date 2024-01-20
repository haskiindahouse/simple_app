package account

import account.utils.AmountUtils.AccumulativeAmount
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.refineV

import scala.concurrent.Future

case class AccumulativeAccount(id: String, balance: AccumulativeAmount, interestRate: Double) extends Account[AccumulativeAmount] {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def replenishment(amount: AccumulativeAmount): AccumulativeAccount =
    copy(balance = refineV[Positive](balance.value + amount.value).getOrElse(balance))

  override def withdrawal(amount: AccumulativeAmount): Future[AccumulativeAccount] =
    Future {
      if (balance.value >= amount.value) copy(balance = refineV[Positive](balance.value - amount.value).getOrElse(balance))
      else throw new IllegalArgumentException("Insufficient funds")
    }

  def applyInterest(): AccumulativeAccount = {
    val interest = Math.round(balance.value * interestRate / 365)
    val newBalance = balance.value + interest
    val refinedBalance = refineV[Positive](newBalance).getOrElse(balance)
    copy(balance = refinedBalance)
  }
}