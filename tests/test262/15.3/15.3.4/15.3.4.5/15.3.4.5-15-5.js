  function testcase() 
  {
    var canConfigurable = false;
    var hasProperty = false;
    function foo() 
    {
      
    }
    var obj = foo.bind({
      
    });
    hasProperty = obj.hasOwnProperty("length");
    delete obj.caller;
    canConfigurable = ! obj.hasOwnProperty("length");
    return hasProperty && ! canConfigurable;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  