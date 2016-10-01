  prec = 0.00000000003;
  period = Math.PI;
  pernum = 100;
  a = - pernum * period + period / 2;
  b = pernum * period - period / 2;
  snum = 9;
  step = period / (snum + 2);
  x = new Array();
  for (i = 0;i <= snum;i++)
  {
    x[i] = a + (i + 1) * step;
  }
  var curval;
  var curx;
  var j;
  for (i = 0;i < snum;i++)
  {
    curval = Math.tan(x[i]);
    curx = x[i] + period;
    j = 0;
    while (curx <= b)
    {
      curx += period;
      j++;
      {
        var __result1 = Math.abs(Math.tan(curx) - curval) >= prec;
        var __expect1 = false;
      }
    }
  }
  