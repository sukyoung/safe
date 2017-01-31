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
  var alphabetR = [undefined, 2, 1, "X", - 1, "a", true, obj, NaN, Infinity, ];
  var alphabet = [true, "a", "X", NaN, Infinity, 2, 1, obj, - 1, undefined, ];
  var myComparefn = (function (x, y) 
  {
    var xS = String(x);
    var yS = String(y);
    if (xS < yS)
      return 1;
    if (xS > yS)
      return - 1;
    return 0;
  });
  alphabetR.sort(myComparefn);
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
    var __result1 = result !== true;
    var __expect1 = false;
  }
  