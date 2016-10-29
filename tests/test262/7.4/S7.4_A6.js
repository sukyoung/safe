  var errorCount = 0;
  var count = 0;
  for(var indexI = 0;indexI <= 65535;indexI++)
  {
    try
{      var xx = 0;
      eval("/*var " + String.fromCharCode(indexI) + "xx = 1*/");
      var hex = decimalToHexString(indexI);
      if (xx !== 0)
      {
        $ERROR('#' + hex + ' ');
        errorCount++;
      }}
    catch (e)
{      $ERROR('#' + hex + ' ');
      errorCount++;}

    count++;
  }
  {
    var __result1 = errorCount > 0;
    var __expect1 = false;
  }
  function decimalToHexString(n) 
  {
    n = Number(n);
    var h = "";
    for(var i = 3;i >= 0;i--)
    {
      if (n >= Math.pow(16, i))
      {
        var t = Math.floor(n / Math.pow(16, i));
        n -= t * Math.pow(16, i);
        if (t >= 10)
        {
          if (t == 10)
          {
            h += "A";
          }
          if (t == 11)
          {
            h += "B";
          }
          if (t == 12)
          {
            h += "C";
          }
          if (t == 13)
          {
            h += "D";
          }
          if (t == 14)
          {
            h += "E";
          }
          if (t == 15)
          {
            h += "F";
          }
        }
        else
        {
          h += String(t);
        }
      }
      else
      {
        h += "0";
      }
    }
    return h;
  }
  