  function testcase() 
  {
    var origReduce = Array.prototype.reduce;
    var origDesc = Object.getOwnPropertyDescriptor(Array.prototype, "reduce");
    var newDesc;
    try
{      Array.prototype.reduce = (function () 
      {
        ;
      });
      newDesc = Object.getOwnPropertyDescriptor(Array.prototype, "reduce");
      var descArray = [origDesc, newDesc, ];
      for(var j in descArray)
      {
        for(var i in descArray[j])
        {
          if (i === "value")
          {
            if (origDesc[i] === newDesc[i])
            {
              return false;
            }
          }
          else
            if (origDesc[i] !== newDesc[i])
            {
              return false;
            }
        }
      }
      return true;}
    finally
{      Array.prototype.reduce = origReduce;}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  