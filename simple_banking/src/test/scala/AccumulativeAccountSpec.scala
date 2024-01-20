import account.AccumulativeAccount
import account.utils.AmountUtils.AccumulativeAmount
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal
import org.scalatest.time.{Millis, Seconds, Span}

class AccumulativeAccountSpec extends AnyFunSpec with Matchers with ScalaFutures {

  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  describe("AccumulativeAccount") {

    def testReplenishment(initialBalance: Long, amount: Long, interestRate: Double, expectedBalance: Long): Unit = {
      it(s"should replenish $amount to an account with initial balance $initialBalance and interest rate $interestRate resulting in $expectedBalance") {
        val account = AccumulativeAmount(initialBalance).right.flatMap { initialAmount =>
          Right(AccumulativeAccount(initialAmount, interestRate))
        }.getOrElse(fail("Invalid initial balance"))
        val replenishedAccount = account.replenishment(AccumulativeAmount(amount).getOrElse(fail("Invalid replenish amount")))
        replenishedAccount.balance.value shouldBe expectedBalance
      }
    }

    def testWithdrawal(initialBalance: Long, amount: Long, interestRate: Double, isAllowed: Boolean): Unit = {
      it(s"should ${if (!isAllowed) "not " else ""}allow withdrawal of $amount from an account with initial balance $initialBalance and interest rate $interestRate") {
        val account = AccumulativeAmount(initialBalance).right.flatMap { initialAmount =>
          Right(AccumulativeAccount(initialAmount, interestRate))
        }.getOrElse(fail("Invalid initial balance"))
        val withdrawalAmount = AccumulativeAmount(amount).getOrElse(fail("Invalid withdrawal amount"))

        val futureResult = account.withdrawal(withdrawalAmount).map(Some(_)).recover {
          case NonFatal(_) => None
        }

        whenReady(futureResult) { result =>
          if (isAllowed) {
            result shouldBe defined
            result.get.balance.value should be(initialBalance - amount)
          } else {
            result should not be defined
          }
        }
      }
    }

    def testApplyInterest(initialBalance: Long, interestRate: Double, expectedBalance: Long): Unit = {
      it(s"should apply an interest rate of $interestRate to an account with initial balance $initialBalance resulting in $expectedBalance") {
        val account = AccumulativeAmount(initialBalance).right.flatMap { initialAmount =>
          Right(AccumulativeAccount(initialAmount, interestRate))
        }.getOrElse(fail("Invalid initial balance"))

        val accountWithInterest = account.applyInterest()
        accountWithInterest.balance.value shouldBe expectedBalance
      }
    }

    testReplenishment(1000, 500, 0.05, 1500)

    testWithdrawal(1000, 500, 0.05, isAllowed = true)
    testWithdrawal(1000, 1500, 0.05, isAllowed = false)

    testApplyInterest(1000, 15, 1000 + Math.round(1000 * 15 / 365))
  }
}
