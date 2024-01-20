import account.{Account, AccumulativeAccount, CreditAccount, DebitAccount}
import account.exception.IllegalAmountException
import account.operations.Operation
import account.utils.AmountUtils.{AccumulativeAmount, CreditAmount, CreditLimit, DebitAmount}

import scala.collection.mutable.ListBuffer
import scala.io.StdIn.readLine

object SimpleBanking extends App {
  // in-memory collections - потому что в требованиях не было ограничений
  var accounts: Map[String, Account[_]] = Map()
  var transactions: ListBuffer[Operation] = ListBuffer()

  // Да простит меня проверяющий за такой ужасный код ниже:
  private def replenishment(accountId: String, amount: Long): Unit = {
    // Добро пожаловать в стирание типов:
    /*
      Если в Account засунуть:
       ... def test(a: Long): A
      а ниже:
        val test = account.test(amount)
        account.replenishment(test)
      поэтому ща мы закостылим))
     */
    transactions += Operation("replenishment", amount, accountId)
    val updatedAccount = accounts.get(accountId).map { account =>
      accountId match {
        case "debit" =>
          account.asInstanceOf[DebitAccount].replenishment(DebitAmount(amount).getOrElse { throw IllegalAmountException() })
        case "credit" =>
          account.asInstanceOf[CreditAccount].replenishment(CreditAmount(amount).getOrElse { throw IllegalAmountException() })
        case "accumulative" =>
          account.asInstanceOf[AccumulativeAccount].replenishment(AccumulativeAmount(amount).getOrElse { throw IllegalAmountException() })
        case _ => account
      }
    }

    updatedAccount.foreach(acc => accounts += (accountId -> acc))
  }

  private def withdrawal(accountId: String, amount: Long): Unit = {
      transactions += Operation("withdrawal", amount, accountId)
      val updatedAccount = accounts.get(accountId).map { account =>
        accountId match {
          case "debit" => account.asInstanceOf[DebitAccount].withdrawal(DebitAmount(amount).getOrElse { throw IllegalAmountException() })
          case "credit" => account.asInstanceOf[CreditAccount].withdrawal(CreditAmount(amount).getOrElse { throw IllegalAmountException() })
          case "accumulative" => account.asInstanceOf[AccumulativeAccount].withdrawal(AccumulativeAmount(amount).getOrElse { throw IllegalAmountException() })
          case _ => account
        }
      }

      updatedAccount.foreach(acc => accounts += (accountId -> acc))
    }

  private def showTransactions(): Unit = transactions.foreach(println)

  private def showBalance(accountId: String): Unit = {
    accounts.get(accountId).foreach { account =>
      println(s"Balance of account $accountId: ${account.balance}")
    }
  }

  def mainLoop(): Unit = {
    var continue = true
    while (continue) {
      println("1: Deposit, 2: Withdraw, 3: Show Transactions, 4: Show Balance, 5: Exit")
      val choice = readLine()
      choice match {
        case "1" =>
          println("Enter account ID:")
          val accountId = readLine()
          println("Enter amount to deposit:")
          val amount = BigDecimal(readLine())
          replenishment(accountId, amount.toLong)
        case "2" =>
          println("Enter account ID:")
          val accountId = readLine()
          println("Enter amount to withdraw:")
          val amount = BigDecimal(readLine())
          withdrawal(accountId, amount.toLong)
        case "3" => showTransactions()
        case "4" =>
          println("Enter account ID:")
          val accountId = readLine()
          showBalance(accountId)
        case "5" => continue = false
        case _ => println("Invalid choice")
      }
    }
  }

  // Инициализация счетов
  accounts += ("debit" -> DebitAccount("acc123",
    DebitAmount(100).getOrElse { throw IllegalAmountException() }
  ))
  accounts += ("credit" -> CreditAccount("acc456",
    CreditAmount(100).getOrElse { throw IllegalAmountException() },
    CreditLimit(100).getOrElse { throw IllegalAmountException() }
  ))

  accounts += ("accumulative" -> AccumulativeAccount("acc789",
    AccumulativeAmount(100).getOrElse { throw  IllegalAmountException() }, 13))

  mainLoop()

  // предоставляет возможность сформировать отчет с балансом по месяцам. Что имеется в виду?
  // инструкция позапуску и использованию
  // Настроены проверки:
  // форматирование
  // unit-тесты
}