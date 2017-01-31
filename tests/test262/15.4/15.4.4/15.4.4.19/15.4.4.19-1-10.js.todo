  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return ('[object Math]' === Object.prototype.toString.call(obj));
    }
    try
{      Math.length = 1;
      Math[0] = 1;
      var testResult = Array.prototype.map.call(Math, callbackfn);
      return testResult[0] === true;}
    finally
{      delete Math[0];
      delete Math.length;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  