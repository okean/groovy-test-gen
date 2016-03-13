## Groovy-test-gen
[![Build Status](https://img.shields.io/travis/Respect/Template.svg?style=flat-square)](https://travis-ci.org/okean/groovy-test-gen)

This project provides a best-practices template Groovy project which simplify designing tests in Java and Groovy languages. It lets you write core functionalities, utils and helpers in Java, hide this by a Groovy wrapper and turn it into a domain-specific language.

As it is an all-in-one solution, the projects includes:
- Test execution profile for tests written in groovy
- Test generation feature given a mapping and data input file.

## Installing

    mvn clean install

Test Generator script is invoked during resources generation phase, reads the [`config.xml`](https://github.com/okean/groovy-test-gen/blob/master/src/generator-data/config.xml) and put all the scripts in `{project.build.directory}/generated-resources/groovy/tests` directory.

## Running tests

    mvn -P run-test -Dtest.filter=test1.groovy

You need to pass the path of the target script or directory as argument. `test.filter` accepts wildcard arguments. 

`mvn -Prun-test -Dtest.filter=HomePageTests*` executes all home page related tests.
`mvn -Prun-test -Dtest.filter=regression/` executes all tests from regression directory.

## Licensing

See [LICENSE](https://github.com/okean/groovy-test-gen/blob/master/LICENSE)
