  var i = 1;
  function Integer(value, exception) 
  {
    try
{      this.value = checkValue(value);
      if (exception)
        $ERROR('#' + i + '.1: Must be exception');}
    catch (e)
{      this.value = e.toString();
      if (! exception)
        $ERROR('#' + i + '.2: Don`t must be exception');}

    i++;
  }
  function checkValue(value) 
  {
    if (Math.floor(value) != value || isNaN(value))
    {
      throw (INVALID_INTEGER_VALUE + ": " + value);
    }
    else
    {
      return value;
    }
  }
  new Integer(13, false);
  new Integer(NaN, true);
  new Integer(0, false);
  new Integer(Infinity, false);
  new Integer(- 1.23, true);
  new Integer(Math.LN2, true);
  