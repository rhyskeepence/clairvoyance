resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")
addSbtPlugin("org.scoverage"    % "sbt-scoverage"        % "1.0.4")
//addSbtPlugin("org.scoverage"    % "sbt-coveralls"        % "1.0.0")
addSbtPlugin("org.xerial.sbt"   % "sbt-sonatype"         % "1.0")
addSbtPlugin("com.jsuereth"     % "sbt-pgp"              % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-site"             % "0.8.1")
