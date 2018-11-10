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
ext.LogoGenerator = { String name, Closure configureClosure ->
	String srcFile = file('src/main/svg/logo.svg')
	String outputDir = file("$buildDir/$name")
	Task prevTask
	Task task
	def debug = false
	def commands = configureClosure(srcFile, includePath, outputDir, debug)
	commands.eachWithIndex { command, step ->
		String taskName = name
		if (step < commands.size() - 1)
			taskName += "Step${step}"
		task = tasks.create(taskName, command['type']) {
			group 'Build'
			inputs.source srcFile
			switch (command['type']) {
				case Exec:
				case CrossPlatformExec:
					commandLine command['commandLine']
					command['outputFiles'].eachWithIndex { f, i ->
						outputs.file f
						dependsOn tasks.create("${taskName}OutputDirectory$i", Directory) {
							dir = f.getParentFile()
						}
					}
					break
				case Copy:
					from srcFile
					// Copy task creates output directory automatically
					into command['into']
					rename { command['rename'] }
					break
				case Task:
					doLast command['doLast']
					command['outputFiles'].eachWithIndex { f, i ->
						outputs.file f
						dependsOn tasks.create("${taskName}OutputDirectory$i", Directory) {
							dir = f.getParentFile()
						}
					}
			}
		}
		if (prevTask)
			task.dependsOn prevTask
		prevTask = task
	}
	distributions.create(name) { contents {
		from outputDir
	} }
	task = tasks[name + 'DistZip']
	task.dependsOn prevTask
	tasks['build'].dependsOn task
	publishing {
		publications {
			artifactPublication(MavenPublication) {
				artifact(outputDir) {
					classifier name
				}
			}
		}
	}
	artifactoryPublish.dependsOn task
	return prevTask
}
