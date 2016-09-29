  x = + 0;
  y = new Array();
  y[0] = 0.000000000000001;
  y[2] = + Infinity;
  y[1] = 1;
  ynum = 3;
  for (i = 0;i < ynum;i++)
  {
    this["__result" + i] = ! (Math.atan2(y[i], x) === (Math.PI) / 2);
	this["__expect" + i] = false;
  }
  
