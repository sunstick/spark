resolvers ++= Seq(
  Classpaths.typesafeResolver,
  Resolver.url("sbt-plugin-releases", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("bintray-sbt-plugins", url("http://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
)

//addSbtPlugin("com.jsuereth" % "xsbt-gpg-plugin" % "0.6")

addSbtPlugin("com.eed3si9n" %% "sbt-assembly" % "0.9.2")
