  function testcase() 
  {
    var arrObj = [];
    arrObj.foo = 10;
    Object.freeze(arrObj);
    var desc = Object.getOwnPropertyDescriptor(arrObj, "foo");
    delete arrObj.foo;
    return arrObj.foo === 10 && desc.configurable === false && desc.writable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  