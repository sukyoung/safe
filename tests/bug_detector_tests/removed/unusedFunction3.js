function funcA() {}
function funcB(argFunc) {}
function libTest1(testFunc1, testFunc2) {
  testFunc1(testFunc2)
}
function libTest2(testFunc1, testFunc2) {}
//libTest1(funcB, funcA);
libTest2(funcB, funcA);
