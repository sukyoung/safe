  try
{    infor_var = infor_var;}
  catch (e)
{    $ERROR('#1: Variable declaration inside "for" loop is admitted');}

  ;
  for (;;)
  {
    break;
    var infor_var;
  }
  