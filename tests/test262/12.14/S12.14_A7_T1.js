  try
{    try
{      throw "ex2";}
    catch (er2)
{      if (er2 !== "ex2")
        $ERROR('#1.1: Exception === "ex2". Actual:  Exception ===' + e);
      throw "ex1";}
}
  catch (er1)
{    if (er1 !== "ex1")
      $ERROR('#1.2: Exception === "ex1". Actual: ' + er1);
    if (er1 === "ex2")
      $ERROR('#1.3: Exception !== "ex2". Actual: catch previous embedded exception');}

  try
{    throw "ex1";}
  catch (er1)
{    try
{      throw "ex2";}
    catch (er1)
{      if (er1 === "ex1")
        $ERROR('#2.1: Exception !== "ex1". Actual: catch previous catching exception');
      if (er1 !== "ex2")
        $ERROR('#2.2: Exception === "ex2". Actual:  Exception ===' + er1);}

    if (er1 !== "ex1")
      $ERROR('#2.3: Exception === "ex1". Actual:  Exception ===' + er1);
    if (er1 === "ex2")
      $ERROR('#2.4: Exception !== "ex2". Actual: catch previous catching exception');}

  try
{    throw "ex1";}
  catch (er1)
{    if (er1 !== "ex1")
      $ERROR('#3.1: Exception ==="ex1". Actual:  Exception ===' + er1);}

  finally
{    try
{      throw "ex2";}
    catch (er1)
{      if (er1 === "ex1")
        $ERROR('#3.2: Exception !=="ex1". Actual: catch previous embedded exception');
      if (er1 !== "ex2")
        $ERROR('#3.3: Exception ==="ex2". Actual:  Exception ===' + er1);}
}

  var c4 = 0;
  try
{    throw "ex1";}
  catch (er1)
{    try
{      throw "ex2";}
    catch (er1)
{      if (er1 === "ex1")
        $ERROR('#4.1: Exception !=="ex1". Actual: catch previous catching exception');
      if (er1 !== "ex2")
        $ERROR('#4.2: Exception ==="ex2". Actual:  Exception ===' + er1);}

    if (er1 !== "ex1")
      $ERROR('#4.3: Exception ==="ex1". Actual:  Exception ===' + er1);
    if (er1 === "ex2")
      $ERROR('#4.4: Exception !=="ex2". Actual: Catch previous embedded exception');}

  finally
{    c4 = 1;}

  if (c4 !== 1)
    $ERROR('#4.5: "finally" block must be evaluated');
  var c5 = 0;
  try
{    try
{      throw "ex2";}
    catch (er1)
{      if (er1 !== "ex2")
        $ERROR('#5.1: Exception ==="ex2". Actual:  Exception ===' + er1);}

    throw "ex1";}
  catch (er1)
{    if (er1 !== "ex1")
      $ERROR('#5.2: Exception ==="ex1". Actual:  Exception ===' + er1);
    if (er1 === "ex2")
      $ERROR('#5.3: Exception !=="ex2". Actual: catch previous embedded exception');}

  finally
{    c5 = 1;}

  if (c5 !== 1)
    $ERROR('#5.4: "finally" block must be evaluated');
  var c6 = 0;
  try
{    try
{      throw "ex1";}
    catch (er1)
{      if (er1 !== "ex1")
        $ERROR('#6.1: Exception ==="ex1". Actual:  Exception ===' + er1);}
}
  finally
{    c6 = 1;}

  if (c6 !== 1)
    $ERROR('#6.2: "finally" block must be evaluated');
  var c7 = 0;
  try
{    try
{      throw "ex1";}
    finally
{      try
{        c7 = 1;
        throw "ex2";}
      catch (er1)
{        if (er1 !== "ex2")
          $ERROR('#7.1: Exception ==="ex2". Actual:  Exception ===' + er1);
        if (er1 === "ex1")
          $ERROR('#7.2: Exception !=="ex1". Actual: catch previous embedded exception');
        c7++;}
}
}
  catch (er1)
{    if (er1 !== "ex1")
      $ERROR('#7.3: Exception ==="ex1". Actual:  Exception ===' + er1);}

  if (c7 !== 2)
    $ERROR('#7.4: "finally" block must be evaluated');
  