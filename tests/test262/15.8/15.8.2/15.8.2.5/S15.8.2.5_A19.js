  y = - Infinity;
  x = new Array();
  x[0] = 0.000000000000001;
  x[1] = 1;
  x[2] = 1.7976931348623157e308;
  x[3] = - 0.000000000000001;
  x[4] = - 1;
  x[5] = - 1.7976931348623157e308;
  xnum = 6;
  for (i = 0;i < xnum;i++)
  {
    this["__result" + i] = ! (Math.atan2(y, x[i]) === - (Math.PI) / 2);
	this["__expect" + i] = false;
  }
