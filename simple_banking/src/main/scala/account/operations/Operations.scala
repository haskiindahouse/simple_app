package account.operations

import account.Account.Account
import account.utils.AmountUtils.Amount

// 1. Пополнение счета
// 2. Снятие средств
// 3. Перевод на другой счет
// 5. Проверка баланса
object Operations {
  trait Operation {
    def execute(): Unit
  }

  case class Replenishment[A <: Account[_]](amount: Amount, account: A) extends Operation {
    override def execute(): Unit = {
      account.replenishment(amount)
    }
  }

  case class Withdrawal[A <: Account[_]](amount: Amount, account: A) extends Operation {
    override def execute(): Unit = {
      account.withdrawal(amount)
    }
  }

  case class Transfer[A <: Account[_], B <: Account[_]](amount: Amount,
                                                           accountFrom: A,
                                                           accountTo: B) extends Operation {
    override def execute(): Unit = {
      accountFrom.withdrawal(amount)
      accountTo.replenishment(amount)
    }
  }

  case class Balance[A <: Account[_]](account: A) extends Operation {
    override def execute(): Unit = {
      println(account.balance)
    }
  }
}
