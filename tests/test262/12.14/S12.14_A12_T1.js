  var x;
  var mycars = new Array();
  mycars[0] = "Saab";
  mycars[1] = "Volvo";
  mycars[2] = "BMW";
  try
{    for (x in mycars)
    {
      if (mycars[x] === "BMW")
        throw "ex";
    }}
  catch (e)
{    if (e !== "ex")
      $ERROR('#1: Exception ==="ex". Actual:  Exception ===' + e);}

  