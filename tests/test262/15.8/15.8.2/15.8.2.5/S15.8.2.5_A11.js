  y = - 0;
  x = new Array();
  x[0] = - 0.000000000000001;
  x[2] = - Infinity;
  x[1] = - 1;
  xnum = 3;
  for (i = 0;i < xnum;i++)
  {
    this["__result" + i] = ! (Math.atan2(y, x[i]) === - Math.PI);
	this["__expect" + i] = false;
  }
  
