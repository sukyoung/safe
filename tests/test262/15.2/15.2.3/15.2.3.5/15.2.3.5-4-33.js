  function testcase() 
  {
    try
{      Math.prop = {
        value : 12,
        enumerable : true
      };
      var newObj = Object.create({
        
      }, Math);
      return newObj.hasOwnProperty("prop");}
    finally
{      delete Math.prop;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  