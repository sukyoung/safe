  var c = 0;
  if (0)
    $ERROR('#1.1: 0 in expression is evaluated to false ');
  else
    c++;
  if (c != 1)
    $ERROR('#1.2: else branch don`t execute');
  if (false)
    $ERROR('#2.1: false in expression is evaluated to false ');
  else
    c++;
  if (c != 2)
    $ERROR('#2.2: else branch don`t execute');
  if (null)
    $ERROR('#3.1: null in expression is evaluated to false ');
  else
    c++;
  if (c != 3)
    $ERROR('#3.2: else branch don`t execute');
  if (undefined)
    $ERROR('#4.1: undefined in expression is evaluated to false ');
  else
    c++;
  if (c != 4)
    $ERROR('#4.2: else branch don`t execute');
  if ("")
    $ERROR('#5.1: empty string in expression is evaluated to false ');
  else
    c++;
  if (c != 5)
    $ERROR('#5.2: else branch don`t execute');
  if (NaN)
    $ERROR('#6.1: NaN in expression is evaluated to false ');
  else
    c++;
  if (c != 6)
    $ERROR('#6.2: else branch don`t execute');
  