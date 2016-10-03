  try
{    x = x;}
  catch (e)
{    $ERROR('#1: Declaration variable inside "do-while" statement is admitted');}

  do
    var x;while (false);
  