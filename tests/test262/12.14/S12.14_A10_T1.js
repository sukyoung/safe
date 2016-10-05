  var i = 0;
  try
{    while (i < 10)
    {
      if (i === 5)
        throw i;
      i++;
    }}
  catch (e)
{    if (e !== 5)
      $ERROR('#1: Exception === 5. Actual:  Exception ===' + e);}

  