package xsbtConcat

import sbt._

import xsbtUtil.{ util => xu }
import xsbtAsset.AssetPlugin
import xsbtAsset.AssetProcessor
import xsbtAsset.AssetProcessors

object Import {
	case class ConcatType(
		suffix:String,
		header:Option[String] = None,
		footer:Option[String] = None
	)
	val concatProcessor		= taskKey[AssetProcessor]("concatenation processor")
	val concatTypes			= settingKey[Seq[ConcatType]]("suffixes to combine")
	val concatName			= settingKey[String]("name of concatenated files, without suffix")
	val concatBuildDir		= settingKey[File]("where to put concatenated sources")
}

object ConcatProcessorPlugin extends AutoPlugin {
	override val requires:Plugins		= AssetPlugin

	override val trigger:PluginTrigger	= allRequirements

	lazy val autoImport	= Import
	import autoImport._

	override lazy val projectSettings:Seq[Def.Setting[_]]	=
		Vector(
			concatTypes			:= Seq(ConcatType("js"), ConcatType("css")),
			concatName			:= "concat",
			concatBuildDir		:= Keys.target.value / "concat",

			concatProcessor		:= {
				Function chain (
					concatTypes.value map singleProcessor(concatBuildDir.value, concatName.value)
				)
			}
		)

	def singleProcessor(buildDir:File, baseName:String)(typ:ConcatType):AssetProcessor = {
		val filter	= GlobFilter(s"*.${typ.suffix}") && -DirectoryFilter

		(AssetProcessors selective filter) { input =>
			val output	= buildDir / s"${baseName}.${typ.suffix}"
			val files	= input map xu.pathMapping.getFile

			concatTexts(files, typ.header, typ.footer, output)

			Vector(output -> output.getName)
		}
	}

	def concatTexts(files:Seq[File], header:Option[String], footer:Option[String], to:File) {
		val charset		= IO.utf8
		val contents	= files map { IO read (_, charset) }
		val string		= header.toVector ++ contents ++ footer.toVector mkString "\n"
		IO write (to, string, charset)
	}
}
