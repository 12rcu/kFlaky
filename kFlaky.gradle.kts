class GreetingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        tasks.register("kFlaky") {
            dependsOn("test")
            println("Run kFlaky")

            layout
                .buildDirectory
                .dir("test-results")
                .get()
                .asFile
                .walk()
                .filter { it.isFile && it.name.endsWith(".xml") }
                .forEach {

                }
        }
        project.task("hello") {
            doLast {
                println("Hello from the GreetingPlugin")
            }
        }
    }
}

// Apply the plugin
apply<GreetingPlugin>()
