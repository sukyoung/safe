  try
{    index = index;}
  catch (e)
{    $ERROR('#1: VariableDeclaration in "var VariableDeclarationListNoIn" of for IterationStatement is allowed');}

  for(var index = 0;index < 6;index++)
  {
    ;
  }
  