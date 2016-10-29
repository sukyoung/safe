  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return '[object JSON]' === Object.prototype.toString.call(JSON);
    }
    try
{      JSON.length = 1;
      JSON[0] = 1;
      var newArr = Array.prototype.filter.call(JSON, callbackfn);
      return newArr[0] === 1;}
    finally
{      delete JSON.length;
      delete JSON[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  