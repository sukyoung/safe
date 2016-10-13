  function testcase() 
  {
    var foo = (function (x, y) 
    {
      return new Boolean((x + y) === "ab" && arguments[0] === "a" && arguments[1] === "b" && arguments.length === 2);
    });
    var obj = foo.bind({
      
    }, "a", "b");
    return obj() == true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  