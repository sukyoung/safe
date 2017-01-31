  function testcase() 
  {
    function callbackfn(val, idx, obj) 
    {
      return true;
    }
    var obj = {
      0 : 11,
      1 : 9,
      length : 2
    };
    var newArr = Array.prototype.map.call(obj, callbackfn);
    try
{      var tempVal = newArr[1];
      delete newArr[1];
      return tempVal !== undefined && newArr[1] === undefined;}
    catch (ex)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  