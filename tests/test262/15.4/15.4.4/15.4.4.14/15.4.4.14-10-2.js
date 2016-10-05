  function testcase() 
  {
    var accessed = false;
    var f = {
      length : 0
    };
    Object.defineProperty(f, "0", {
      get : (function () 
      {
        accessed = true;
        return 1;
      })
    });
    var i = Array.prototype.indexOf.call(f, 1);
    if (i === - 1 && accessed == false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  