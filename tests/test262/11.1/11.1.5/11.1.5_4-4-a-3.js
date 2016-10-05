  function testcase() 
  {
    var o = ({
      foo : 0,
      foo : 1
    });
    return o.foo === 1;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  