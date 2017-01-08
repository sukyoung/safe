  var obj = {
    valueOf : (function () 
    {
      return 1;
    }),
    toString : (function () 
    {
      return - 2;
    })
  };
  var alphabetR = {
    0 : undefined,
    1 : 2,
    2 : 1,
    3 : "X",
    4 : - 1,
    5 : "a",
    6 : true,
    7 : obj,
    8 : NaN,
    9 : Infinity
  };
  alphabetR.sort = Array.prototype.sort;
  alphabetR.length = 10;
  var alphabet = [- 1, obj, 1, 2, Infinity, NaN, "X", "a", true, undefined, ];
  alphabetR.sort();
  alphabetR.getClass = Object.prototype.toString;
  {
    var __result1 = alphabetR.getClass() !== "[object " + "Object" + "]";
    var __expect1 = false;
  }
  var result = true;
  for(var i = 0;i < 10;i++)
  {
    if (! (isNaN(alphabetR[i]) && isNaN(alphabet[i])))
    {
      if (alphabetR[i] !== alphabet[i])
        result = false;
    }
  }
  {
    var __result2 = result !== true;
    var __expect2 = false;
  }
  