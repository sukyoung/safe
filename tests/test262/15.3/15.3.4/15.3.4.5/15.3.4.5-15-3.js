  function testcase() 
  {
    var canWritable = false;
    var hasProperty = false;
    function foo() 
    {
      
    }
    var obj = foo.bind({
      
    });
    hasProperty = obj.hasOwnProperty("length");
    obj.length = 100;
    canWritable = (obj.length === 100);
    return hasProperty && ! canWritable;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  