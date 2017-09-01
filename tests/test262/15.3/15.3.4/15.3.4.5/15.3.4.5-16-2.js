  function testcase() 
  {
    function foo() 
    {
      
    }
    var obj = foo.bind({
      
    });
    obj.property = 12;
    return obj.hasOwnProperty("property");
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  