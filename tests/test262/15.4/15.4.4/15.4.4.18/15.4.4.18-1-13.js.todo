  function testcase() 
  {
    var result = false;
    function callbackfn(val, idx, obj) 
    {
      result = ('[object JSON]' === Object.prototype.toString.call(obj));
    }
    try
{      JSON.length = 1;
      JSON[0] = 1;
      Array.prototype.forEach.call(JSON, callbackfn);
      return result;}
    finally
{      delete JSON.length;
      delete JSON[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  