  prec = 0.000000000003;
  period = 2 * Math.PI;
  pernum = 100;
  a = - pernum * period;
  b = pernum * period;
  snum = 9;
  step = period / snum + 0.0;
  x = new Array();
  for (i = 0;i < snum;i++)
  {
    x[i] = a + i * step;
  }
  x[9] = a + period;
  var curval;
  var curx;
  var j;
  for (i = 0;i < snum;i++)
  {
    curval = Math.sin(x[i]);
    curx = x[i] + period;
    j = 0;
    while (curx <= b)
    {
      curx += period;
      j++;
      {
        var __result1 = Math.abs(Math.sin(curx) - curval) >= prec;
        var __expect1 = false;
      }
    }
  }
  