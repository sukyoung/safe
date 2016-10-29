  var mycars = new Array();
  mycars[0] = "Saab";
  mycars[1] = "Volvo";
  mycars[2] = "BMW";
  var mycars2 = new Array();
  mycars2[0] = "Mercedes";
  mycars2[1] = "Jeep";
  mycars2[2] = "Suzuki";
  try
{    throw mycars;}
  catch (e)
{    for(var i = 0;i < 3;i++)
    {
      if (e[i] !== mycars[i])
        $ERROR('#1.' + i + ': Exception[' + i + '] === mycars[' + i + ']. Actual:  Exception[' + i + '] ===' + e[i]);
    }}

  