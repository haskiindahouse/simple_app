package account

import account.utils.AmountUtils._

// 1. Дебетовый
// 2. Кредитный
// 3. Накопительный
object Account {
  trait Account[B] {
    def balance: B
    def replenishment(amount: Amount): Unit
    def withdrawal(amount: Amount): Unit
  }

  case class Debit(override var balance: DebitAmount) extends Account[DebitAmount] {
    override def replenishment(amount: Amount): Unit = {
      balance += amount
    }

    override def withdrawal(amount: Amount): Unit = {
    }
  }

  case class Credit(override val balance: CreditAmount, limit: CreditLimit) extends Account[CreditAmount] {
    override def replenishment(amount: Amount): Unit = {
    }

    override def withdrawal(amount: Amount): Unit = {
    }
  }

  case class Accumulative(override val balance: AccumulativeAmount) extends Account[AccumulativeAmount] {
    override def replenishment(amount: Amount): Unit = {
    }

    override def withdrawal(amount: Amount): Unit = {
    }
  }
}
