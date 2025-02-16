# kFlkay

KFlkay is a project that in its current form detects and classifies order dependent flaky tests in jUnit based test 
environments in Java and Kotlin. This project makes use of, but is not limited to, the annotations provided by junit. 
Annotations are used to change the test order of set tests, and thus recognize order dependencies.

## Usage

Usage is currently a bit tricky as no working dockerfiles are provided (hopefully this will change soon).
Modify the config.json in the root directory of this project and edit the paths to the project you want to test. 
See the usage page for more information.

## Contributing

All contributions are welcome. This project uses a hexagonal structure, see the contribution page for more information.

![hexagonal structure](./assets/hexagonal.png)