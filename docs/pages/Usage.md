# kFlkay Usage

## Docker

wip

## Local Setup

### Prerequisites

#### Files:

- kFlkay.jar    (Download from [Release Page](https://github.com/12rcu/kFlaky/releases))
- config.json
- default.db    (SQLite file)
- project to test

#### Programs

- java (version is dependent on the version that was downloaded of kFlkay or the project you want to test)

### Config.json

The config defines everything kFlaky needs to know, so there are no command line arguments to pass to kFlaky as
is taken care of by the config.

#### kFlkay specific

- baseDir: the directory where temporary and log files are saved
- logDir: the relative dir from the base dir to save the logs
- tmpDir: the relative dir from the base dir to save execution tmp files (this dir can be deleted after execution)
- worker: the number of processes that are started in parallel. Note: the main limiting factor of java based projects is memory

#### project specific

- identifier: an internal identifier for a project, must be unique!
- framework: the framework the project uses, currently only junit
- language: the language of the project, currently only kotlin and java
- projectPath: the absolute path to the project that should be tested
- testExecutionDir: relative path to the projectPath in which the test exec command should be run, for gradle/maven this is empty as it's just the project dir
- testExecutionCommand: the command that is run within the testExecution directory that executes the tests
- testResultDir: optional, a directory relative to the projectPath to search for test result files
- testDir: optional, a directory relative to the projectPath to search for test files
- strategy: the order strategy that should be used to modify test files, currently only TUSCAN_SQUARES or SKIP (only runs the pre runs)
- preRuns: the number of test runs kFlaky should do to determine if tests have other sources of flakiness


#### Sample Linux:

```json
{
    "baseDir": "/home/matthias/Documents/opensource/kFlaky",    //used as base dir for log dir and temp dir
    "logDir": "logs",   //the relative path to the log dir
    "tmpDir": "temp",   //the relative path to the tmp dir (worker dir)
    "worker": 2,        //number of workers, note: keep this number low as the main resource that is used for java projects is memory and not CPU
    "projects": [
        {
            "identifier": "flakyTestPrj",                                                           //identifier for the databse
            "framework": "junit",                                                                   //framework currently only jUnit is supported
            "language": "kotlin",                                                                   //language, java and kotlin is supported
            "projectPath": "/home/matthias/Documents/opensource/flakyProjects/FlakyTestProject",    //the absolute path to the project
            "testExecutionCommand": "./gradlew test",                                               //command to execute tests
            "testResultDir": "build/test-results",                                                  //the test reulsts dir (can be empty, seaching for these files happens then in the entiere project)
            "testExecutionDir": "",                                                                 //for gradle this is just the project root dir
            "testDir": "src/test/kotlin",                                                           //if empty will search in entiere project
            "strategy": "TUSCAN_SQUARES",                                                           //test order strategy (currently only TUSCAN_SQUARES)
            "preRuns": 10                                                                           //runs to determin if a test is flaky but not OD flaky
        }
    ]
}
```

#### Sample Windows:

```json
{
    "baseDir": "C:\\Users\\matth\\Documents\\opensource\\kFlaky",
    "logDir": "logs",
    "tmpDir": "temp",
    "worker": 2,
    "projects": [
        {
            "identifier": "flakyTestPrj",
            "framework": "junit",
            "language": "kotlin",
            "projectPath": "C:\\Users\\matth\\Documents\\opensource\\flakyTest",
            "testExecutionCommand": "cmd.exe /c .\\gradlew.bat test",
            "testResultDir": "build/test-results",
            "testExecutionDir": "",
            "testDir": "src/test/kotlin",
            "strategy": "TUSCAN_SQUARES",
            "preRuns": 10
        }
    ]
}
```