import sbt.*
import sbt.librarymanagement.ModuleID

object Dependencies  {

  private val derevo = "0.13.0"
  private val newtype = "0.4.4"
  private val refined = "0.11.0"
  private val scalamock = "5.2.0"
  private val scalatest = "3.2.15"

  object BankingDependencies extends VersionedDependenciesSource {

    override protected def distributionDependencies: Seq[ModuleID] = Seq(
      "io.estatico" %% "newtype" % newtype,
      "eu.timepit" %% "refined"  % refined,

      // tofu
      "tf.tofu" %% "derevo-pureconfig" %  derevo,
      "tf.tofu" %% "derevo-tethys" % derevo,
      "tf.tofu" %% "derevo-cats-tagless" % derevo,
      "tf.tofu" %% "derevo-scalacheck" % derevo
    )

    override protected def testDependencies: Seq[ModuleID] = Seq(
      "org.scalatest" %% "scalatest" % scalatest,
      "org.scalamock" %% "scalamock" % scalamock,
    )

    def dependencies: Seq[ModuleID] = distributionDependencies ++ testDependencies.map(_ % Test)
  }
}