  var c1 = 1;
  try
{    if (c1 === 1)
    {
      throw "ex1";
      $ERROR('#1.1: throw "ex1" lead to throwing exception');
    }
    $ERROR('#1.2: throw "ex1" inside the "if" statement lead to throwing exception');}
  catch (er1)
{    if (er1 !== "ex1")
      $ERROR('#1.3: Exception ==="ex1". Actual:  Exception ===' + er1);}

  var c2 = 1;
  if (c2 === 1)
  {
    try
{      throw "ex1";
      $ERROR('#2.1: throw "ex1" lead to throwing exception');}
    catch (er1)
{      if (er1 != "ex1")
        $ERROR('#2.2: Exception ==="ex1". Actual:  Exception ===' + er1);}

  }
  