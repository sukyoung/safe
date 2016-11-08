  function testcase() 
  {
    foo.prototype = new Array(1, 2, 3);
    function foo() 
    {
      
    }
    var f = new foo();
    f.length = [];
    var callCnt = 0;
    function cb() 
    {
      callCnt++;
    }
    var i = f.forEach(cb);
    if (callCnt === 0)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  