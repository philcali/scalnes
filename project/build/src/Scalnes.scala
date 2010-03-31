import sbt._

class Scalnes(info: ProjectInfo) extends DefaultProject(info) {
  val sanselanRepo = "Sanselan image library" at "http://mvnrepository.com/artifact"
  val sanselan = "org.apache.sanselan" % "sanselan" % "0.97-incubator"

  override def mainClass = Some("calico.scalnes.Scalnes")
}
