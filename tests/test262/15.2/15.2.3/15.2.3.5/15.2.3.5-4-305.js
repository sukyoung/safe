  function testcase() 
  {
    try
{      var newObj = Object.create({
        
      }, {
        prop : {
          enumerable : true
        }
      });
      return newObj.hasOwnProperty("prop");}
    catch (e)
{      return false;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  