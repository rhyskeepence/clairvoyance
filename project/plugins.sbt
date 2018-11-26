resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage"    % "sbt-scoverage"        % "1.5.1")
addSbtPlugin("org.scoverage"    % "sbt-coveralls"        % "1.2.7")
addSbtPlugin("org.xerial.sbt"   % "sbt-sonatype"         % "2.3")
addSbtPlugin("com.jsuereth"     % "sbt-pgp"              % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-site"             % "1.3.2")
addSbtPlugin("io.get-coursier"  % "sbt-coursier"         % "1.0.3")
