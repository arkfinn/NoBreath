NoBreath
======================
ブレス追加用プラグイン
sakuseigo
jar作成後、utauフォルダ以下をコピーします


使い方
----

project/Build.scala
	import sbt._
	import Keys._

	object NoBreath extends Build {
	  lazy val root = Project(id = "NoBreath", base = file(".")) dependsOn(foo)
	  lazy val foo = uri("file://●ここにパスを記入●/UtauPlug")
	}
相対パス指定がよくわからないのでこのようなファイルを作成してください