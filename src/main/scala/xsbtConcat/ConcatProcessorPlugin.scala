package xsbtConcat

import sbt._

import xsbtUtil.{ util => xu }
import xsbtWebApp.WebAppPlugin
import xsbtWebApp.WebAppProcessor
import xsbtWebApp.WebAppProcessors

object ConcatProcessorPlugin extends AutoPlugin {
	object autoImport {
		val concatProcessor		= taskKey[WebAppProcessor]("concatenation processor")
		val concatSuffixes		= settingKey[Seq[String]]("suffixes to combine")
		val concatName			= settingKey[String]("name of concatenated files, without suffix")
		val concatBuildDir		= settingKey[File]("where to put concatenated sources")
	}
	import autoImport._
	
	override val requires:Plugins		= WebAppPlugin
	
	override val trigger:PluginTrigger	= allRequirements
	
	override lazy val projectSettings:Seq[Def.Setting[_]]	=
			Vector(
				concatSuffixes		:= Seq("js", "css"),
				concatName			:= "concat",
				concatBuildDir		:= Keys.target.value / "concat",
				
				concatProcessor		:= {
					val mkProcessor:String=>WebAppProcessor	=
							suffix => singleProcessor(concatBuildDir.value, concatName.value, suffix)
					Function chain (
						concatSuffixes.value map mkProcessor
					)
				}
			)
			
	def singleProcessor(buildDir:File, baseName:String, suffix:String):WebAppProcessor = {
		val filter	= GlobFilter(s"*.${suffix}") && -DirectoryFilter
		
		(WebAppProcessors selective filter) { input =>
			val output	= buildDir / s"${baseName}.${suffix}"
			val files	= input map xu.pathMapping.getFile
			
			concatTexts(files, output)
			
			Vector(output -> output.getName)
		}
	}
	
	def concatTexts(files:Seq[File], to:File) {
		val charset	= IO.utf8
		val string	= files map { IO read (_, charset) } mkString "\n"
		IO write (to, string, charset)
	}
}
