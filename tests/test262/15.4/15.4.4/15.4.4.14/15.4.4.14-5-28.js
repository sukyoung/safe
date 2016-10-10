  function testcase() 
  {
    var stepFiveOccurs = false;
    var fromIndex = {
      valueOf : (function () 
      {
        stepFiveOccurs = true;
        return 0;
      })
    };
    try
{      Array.prototype.indexOf.call(undefined, undefined, fromIndex);
      return false;}
    catch (e)
{      return (e instanceof TypeError) && ! stepFiveOccurs;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  