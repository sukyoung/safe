  var x;
  var mycars = new Array();
  mycars[0] = "Saab";
  mycars[1] = "Volvo";
  mycars[2] = "BMW";
  var fin = 0;
  var i = 0;
  for (x in mycars)
  {
    try
{      i += 1;
      continue;}
    catch (er1)
{      }

    finally
{      fin = 1;}

    fin = - 1;
  }
  {
    var __result1 = fin !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = i !== 3;
    var __expect2 = false;
  }
  var c2 = 0, fin2 = 0;
  for (x in mycars)
  {
    try
{      throw "ex1";}
    catch (er1)
{      c2 += 1;
      continue;}

    finally
{      fin2 = 1;}

    fin2 = - 1;
  }
  {
    var __result3 = fin2 !== 1;
    var __expect3 = false;
  }
  {
    var __result4 = c2 !== 3;
    var __expect4 = false;
  }
  var c3 = 0, fin3 = 0;
  for (x in mycars)
  {
    try
{      throw "ex1";}
    catch (er1)
{      c3 += 1;}

    finally
{      fin3 = 1;
      continue;}

    fin3 = 0;
  }
  {
    var __result5 = c3 !== 3;
    var __expect5 = false;
  }
  {
    var __result6 = fin3 !== 1;
    var __expect6 = false;
  }
  var fin = 0;
  for (x in mycars)
  {
    try
{      continue;}
    finally
{      fin = 1;}

    fin = - 1;
  }
  {
    var __result7 = fin !== 1;
    var __expect7 = false;
  }
  var c5 = 0;
  for (x in mycars)
  {
    try
{      throw "ex1";}
    catch (er1)
{      c5 += 1;
      continue;}

    c5 += 12;
  }
  {
    var __result8 = c5 !== 3;
    var __expect8 = false;
  }
  var c6 = 0, fin6 = 0;
  for (x in mycars)
  {
    try
{      c6 += 1;
      throw "ex1";}
    finally
{      fin6 = 1;
      continue;}

    fin6 = - 1;
  }
  {
    var __result9 = fin6 !== 1;
    var __expect9 = false;
  }
  {
    var __result10 = c6 !== 3;
    var __expect10 = false;
  }
  