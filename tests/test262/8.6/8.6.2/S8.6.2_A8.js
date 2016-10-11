  var x = Object.preventExtensions({
    
  });
  var y = {
    
  };
  try
{    x.__proto__ = y;}
  catch (err)
{    }

  {
    var __result1 = Object.getPrototypeOf(x) !== Object.prototype;
    var __expect1 = false;
  }
  