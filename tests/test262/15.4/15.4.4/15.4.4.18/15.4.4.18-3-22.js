  function testcase() 
  {
    var accessed = false;
    var firstStepOccured = false;
    var secondStepOccured = false;
    function callbackfn(val, idx, obj) 
    {
      accessed = true;
    }
    var obj = {
      1 : 11,
      2 : 12,
      length : {
        valueOf : (function () 
        {
          firstStepOccured = true;
          return {
            
          };
        }),
        toString : (function () 
        {
          secondStepOccured = true;
          return {
            
          };
        })
      }
    };
    try
{      Array.prototype.forEach.call(obj, callbackfn);
      return false;}
    catch (ex)
{      return ex instanceof TypeError && ! accessed;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  