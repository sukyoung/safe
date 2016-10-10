  try
{    for(var i = 0;i < 10;i++)
    {
      if (i === 5)
        throw i;
    }}
  catch (e)
{    if (e !== 5)
      $ERROR('#1: Exception === 5. Actual:  Exception ===' + e);}

  