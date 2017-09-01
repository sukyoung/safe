  function testcase() 
  {
    var foo = (function () 
    {
      
    });
    var obj = foo.bind({
      
    });
    return Object.prototype.toString.call(obj) === "[object Function]";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  