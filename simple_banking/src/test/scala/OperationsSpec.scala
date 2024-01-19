import account.Account.Account
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import account.operations.Operations._
import account.utils.AmountUtils._

class OperationsSpec extends AnyFlatSpec with Matchers with MockFactory {

  "Replenishment" should "call the replenishment method on the account" in {
    val mockAccount = mock[Account[_]]
    val amount: Amount = Amount(100L)
    val op = Replenishment(amount, mockAccount)

    (mockAccount.replenishment _).expects(amount).once()

    op.execute()
  }

  "Withdrawal" should "call the withdrawal method on the account" in {
    val mockAccount = mock[Account[_]]
    val amount = 100L
    val op = Withdrawal(amount, mockAccount)

    (mockAccount.withdrawal _).expects(amount).once()

    op.execute()
  }

  "Transfer" should "withdraw from one account and replenish another" in {
    val mockAccountFrom = mock[Account[_]]
    val mockAccountTo = mock[Account[_]]
    val amount = 100L
    val op = Transfer(amount, mockAccountFrom, mockAccountTo)

    (mockAccountFrom.withdrawal _).expects(amount).once()
    (mockAccountTo.replenishment _).expects(amount).once()

    op.execute()
  }

  "Balance" should "output the balance of the account" in {
    val mockAccount = mock[Account[_]]
    val op = Balance(mockAccount)
    val balance = 100L

    (() => mockAccount.balance).expects().returning(balance).once()

    op.execute()
  }
}
