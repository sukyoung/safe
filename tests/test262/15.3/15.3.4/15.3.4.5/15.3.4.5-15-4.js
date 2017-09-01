  function testcase() 
  {
    var canEnumerable = false;
    var hasProperty = false;
    function foo() 
    {
      
    }
    var obj = foo.bind({
      
    });
    hasProperty = obj.hasOwnProperty("length");
    for(var prop in obj)
    {
      if (prop === "length")
      {
        canEnumerable = true;
      }
    }
    return hasProperty && ! canEnumerable;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  