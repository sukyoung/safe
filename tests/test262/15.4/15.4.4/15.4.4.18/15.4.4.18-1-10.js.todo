  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = ('[object Math]' === Object.prototype.toString.call(obj));
    }
    try
{      Math.length = 1;
      Math[0] = 1;
      Array.prototype.forEach.call(Math, callbackfn);
      return result;}
    finally
{      delete Math[0];
      delete Math.length;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  