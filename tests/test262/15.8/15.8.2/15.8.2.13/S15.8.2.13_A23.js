  y = NaN;
  x = new Array();
  x[0] = - 1.7976931348623157e308;
  x[1] = - Math.PI;
  x[2] = - 1;
  x[3] = - 0.000000000000001;
  xnum = 4;
  y[0] = - Math.PI;
  y[1] = - Math.E;
  y[2] = - 1.000000000000001;
  y[3] = - 0.000000000000001;
  y[4] = 0.000000000000001;
  y[5] = 1.000000000000001;
  y[6] = Math.E;
  y[7] = Math.PI;
  ynum = 8;
  for (i = 0;i < xnum;i++)
    for (j = 0;j < ynum;j++)
      if (! isNaN(Math.pow(x[i], y[j])))
        $ERROR("#1: isNaN(Math.pow(" + x[i] + ", " + y[j] + ")) === false");
  