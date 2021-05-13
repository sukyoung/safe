  var alphabetR = ["z", "y", "x", "w", "v", "u", "t", "s", "r", "q", "p", "o", "n", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D", "C", "B", "A", ];
  var alphabet = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", ];
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
  alphabet.sort(myComparefn);
  var result = true;
  for(var i = 0;i < 26;i++)
  {
    if (alphabetR[i] !== alphabet[i])
      result = false;
  }
  {
    var __result1 = result !== true;
    var __expect1 = false;
  }
  