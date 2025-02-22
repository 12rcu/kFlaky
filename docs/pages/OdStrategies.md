# Test Order Strategies

## SKIP

Skips the reorder step entirely, and only runs the pre runs.

## TUSCAN_SQUARES

Most common configuration, after pre runs reorder tests in a way that all tests are pairwise tested. Uses the order 
annotations within a testsuite to reorder the tests.

## PAIR_WISE

A not optimized approach where a given test is tested pairwise with all tests in the entire project. This uses the
command arguments to only test the specified test and not the entire test suite.

An example project config with maven:
```json
{
  "strategy": "PAIR_WISE",
  "testExecutionCommand": "/usr/share/maven/bin/mvn clean test -Dtest=SpecTestSuite#specTest,{def:suite}#{def:testName}"
}
```