import account.operations.OperationsQueue
import account.operations.OperationsQueue.Operation
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

class OperationsQueueSpec extends AnyFlatSpec with Matchers with MockFactory {

  implicit val ec: ExecutionContext = ExecutionContext.global

  "OperationsQueue" should "enqueue and execute operations" in {
    val mockOperation = mock[Operation]

    (mockOperation.execute _).expects().once()

    OperationsQueue.enqueue(mockOperation)
    OperationsQueue.startProcessing()

    Thread.sleep(1000)
  }

  it should "handle multiple operations correctly" in {
    val mockOperation1 = mock[Operation]
    val mockOperation2 = mock[Operation]

    (mockOperation1.execute _).expects().once()
    (mockOperation2.execute _).expects().once()

    OperationsQueue.enqueue(mockOperation1)
    OperationsQueue.enqueue(mockOperation2)
    OperationsQueue.startProcessing()

    Thread.sleep(2000)

  }

  it should "not execute operations when the queue is empty" in {
    OperationsQueue.startProcessing()

    Thread.sleep(500)
  }
}
