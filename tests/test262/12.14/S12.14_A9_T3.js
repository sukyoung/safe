  var c1 = 0, fin = 0;
  do
  {
    try
{      c1 += 1;
      break;}
    catch (er1)
{      }

    finally
{      fin = 1;}

    fin = - 1;
    c1 += 2;
  }while (c1 < 2);
  {
    var __result1 = fin !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = c1 !== 1;
    var __expect2 = false;
  }
  var c2 = 0, fin2 = 0;
  do
  {
    try
{      throw "ex1";}
    catch (er1)
{      c2 += 1;
      break;}

    finally
{      fin2 = 1;}

    c2 += 2;
    fin2 = - 1;
  }while (c2 < 2);
  {
    var __result3 = fin2 !== 1;
    var __expect3 = false;
  }
  {
    var __result4 = c2 !== 1;
    var __expect4 = false;
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
      break;}

    c3 += 2;
    fin3 = 0;
  }while (c3 < 2);
  {
    var __result5 = fin3 !== 1;
    var __expect5 = false;
  }
  {
    var __result6 = c3 !== 1;
    var __expect6 = false;
  }
  var c4 = 0, fin4 = 0;
  do
  {
    try
{      c4 += 1;
      break;}
    finally
{      fin4 = 1;}

    fin4 = - 1;
    c4 += 2;
  }while (c4 < 2);
  {
    var __result7 = fin4 !== 1;
    var __expect7 = false;
  }
  {
    var __result8 = c4 !== 1;
    var __expect8 = false;
  }
  var c5 = 0;
  do
  {
    try
{      throw "ex1";}
    catch (er1)
{      break;}

  }while (c5 < 2);
  {
    var __result9 = c5 !== 0;
    var __expect9 = false;
  }
  var c6 = 0;
  do
  {
    try
{      c6 += 1;
      break;}
    catch (er1)
{      }

    c6 += 2;
  }while (c6 < 2);
  {
    var __result10 = c6 !== 1;
    var __expect10 = false;
  }
  var c7 = 0, fin7 = 0;
  try
{    do
    {
      try
{        c7 += 1;
        throw "ex1";}
      finally
{        fin7 = 1;
        break;}

      fin7 = - 1;
      c7 += 2;
    }while (c7 < 2);}
  catch (ex1)
{    c7 = 10;}

  {
    var __result11 = fin7 !== 1;
    var __expect11 = false;
  }
  {
    var __result12 = c7 !== 1;
    var __expect12 = false;
  }
  