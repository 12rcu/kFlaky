# kFlaky

An adaptive flaky test detector that uses annotations in test frameworks to change the order in which tests are executed.

## Current state

This project is still in development, it can currently:
- run Gradle projects with the jUnit testing framework
- detect and differentiate order dependent flaky tests
- run detection in parallel

## Running Projects

Requirements:
- Use a Linux based system (Windows is not tested)
- jdk 21

In order to run kFlaky, you need to have
- add a config entry similar to the one that already exists
- remove existing configs
- run the jar file

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

Build the jar file:
- run from the project root directory: ./gradelw build
- move the jar file (from build/libs/kFlaky.jar) to the same directory as your config.json.

```
./gradlew build
cp ./build/libs/kFlaky*.jar ./kFlaky.jar
java -jar ./kFlaky.jar
```