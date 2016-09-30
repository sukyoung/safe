  function testcase() 
  {
    function Array() 
    {
      
    }
    var o = {
      x : 1,
      y : 2
    };
    var a = Object.keys(o);
    var s = Object.prototype.toString.call(a);
    if (s === '[object Array]')
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  