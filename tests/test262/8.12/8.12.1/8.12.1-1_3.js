  function testcase() 
  {
    var base = {
      foo : 42
    };
    var o = Object.create(base);
    return o.hasOwnProperty("foo") === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  