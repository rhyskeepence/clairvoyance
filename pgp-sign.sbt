import scala.util.Try

pgpPassphrase := Some(Try(sys.env("SECRET")).getOrElse("goaway").toCharArray)

pgpSecretRing := file("./scripts/sonatype.asc")
