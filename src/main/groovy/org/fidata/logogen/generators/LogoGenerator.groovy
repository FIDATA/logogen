package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory

/*
   name
     Name of generator. Used in names of tasks and output directory
   configureClosure
     Configuration closure
     Parameters:
       srcFile    Source file
       outputDir  Output directory
       debug      Boolean indicator of debug/verbose mode
     Return: array of maps with the following fields:
       type        (required)                  Class of task.
                                               Currently only generic
                                               Task, Exec and Copy
                                               classes are supported
       doLast      (required for type == Task) task action
       commandLine (required for type == Exec) command line for
                                               execution
       outputFiles (required for type == Exec) output files
       into        (required for type == Copy) output directory
       rename      (required for type == Copy) output file

   For filenames generators may use Gradle's project.group
     as name of application/project/organization
     logo is being created for
*/
@CompileStatic
abstract class LogoGenerator extends DefaultTask {
  @InputFile
  final RegularFileProperty srcFile = newInputFile()

  @OutputDirectory
  final DirectoryProperty outputDir = newOutputDirectory()

  abstract String getGeneratorName()

  protected final void exec(List<String> commandLine) {
    ext.imconv = "$System.env.IMCONV"


  }
  protected final void copy(Object into, Closure rename) {
    project.copy { CopySpec copySpec ->
      copySpec.from srcFile
      copySpec.into into
      copySpec.rename rename
    }
  }
}
