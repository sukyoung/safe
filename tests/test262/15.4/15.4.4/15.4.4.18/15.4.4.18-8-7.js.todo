  function testcase() 
  {
    foo.prototype = new Array(1, 2, 3);
    function foo() 
    {
      
    }
    var f = new foo();
    var o = {
      toString : (function () 
      {
        return '0';
      })
    };
    f.length = o;
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
  