  var c1 = 0, fin = 0;
  do
  {
    try
{      c1 += 1;
      continue;}
    catch (er1)
{      }

    finally
{      fin = 1;}

    fin = - 1;
  }while (c1 < 2);
  {
    var __result1 = fin !== 1;
    var __expect1 = false;
  }
  var c2 = 0, fin2 = 0;
  do
  {
    try
{      throw "ex1";}
    catch (er1)
{      c2 += 1;
      continue;}

    finally
{      fin2 = 1;}

    fin2 = - 1;
  }while (c2 < 2);
  {
    var __result2 = fin2 !== 1;
    var __expect2 = false;
  }
  var c3 = 0, fin3 = 0;
  do
  {
    try
{      throw "ex1";}
    catch (er1)
{      c3 += 1;}

    finally
{      fin3 = 1;
      continue;}

    fin3 = 0;
  }while (c3 < 2);
  {
    var __result3 = fin3 !== 1;
    var __expect3 = false;
  }
  var c4 = 0, fin4 = 0;
  do
  {
    try
{      c4 += 1;
      continue;}
    finally
{      fin4 = 1;}

    fin4 = - 1;
  }while (c4 < 2);
  {
    var __result4 = fin4 !== 1;
    var __expect4 = false;
  }
  var c5 = 0;
  do
  {
    try
{      throw "ex1";}
    catch (er1)
{      c5 += 1;
      continue;}

  }while (c5 < 2);
  {
    var __result5 = c5 !== 2;
    var __expect5 = false;
  }
  var c6 = 0, fin6 = 0;
  do
  {
    try
{      c6 += 1;
      throw "ex1";}
    finally
{      fin6 = 1;
      continue;}

    fin6 = - 1;
  }while (c6 < 2);
  {
    var __result6 = fin6 !== 1;
    var __expect6 = false;
  }
  {
    var __result7 = c6 !== 2;
    var __expect7 = false;
  }
  