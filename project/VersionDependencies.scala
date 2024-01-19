import sbt.Def
import sbt.librarymanagement.ModuleID
import sbt.librarymanagement.syntax.Test


trait VersionedDependenciesSource {
  protected def distributionDependencies: Seq[ModuleID]

  protected def testDependencies: Seq[ModuleID]
}