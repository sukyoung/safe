  function testcase() 
  {
    var f = new Function('return 42;');
    var desc = Object.getOwnPropertyDescriptor(f, "functionNameHopefullyDoesNotExist");
    return desc === undefined;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  