  var c = 0;
  if (! (1))
    $ERROR('#1.1: 1 in expression is evaluated to true');
  else
    c++;
  if (c != 1)
    $ERROR('#1.2: else branch don`t execute');
  if (! (true))
    $ERROR('#2.1: true in expression is evaluated to true');
  else
    c++;
  if (c != 2)
    $ERROR('#2.2: else branch don`t execute');
  if (! ("1"))
    $ERROR('#3.1: "1" in expression is evaluated to true');
  else
    c++;
  if (c != 3)
    $ERROR('#3.2: else branch don`t execute');
  if (! ("A"))
    $ERROR('#4.1: "A" in expression is evaluated to true');
  else
    c++;
  if (c != 4)
    $ERROR('#4.2: else branch don`t execute');
  