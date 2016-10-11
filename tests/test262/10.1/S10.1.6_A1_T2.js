  var ARG_STRING = "value of the argument property";
  function f1() 
  {
    this.constructor.prototype.arguments = ARG_STRING;
    return arguments;
  }
  if ((new f1(1, 2, 3, 4, 5)).length !== 5)
    $ERROR('#1: (new f1(1,2,3,4,5)).length===5, where f1 returns "arguments" that is set to "' + ARG_STRING + '"');
  if ((new f1(1, 2, 3, 4, 5))[3] !== 4)
    $ERROR('#2: (new f1(1,2,3,4,5))[3]===4, where f1 returns "arguments" that is set to "' + ARG_STRING + '"');
  var x = new f1(1, 2, 3, 4, 5);
  if (delete x[3] !== true)
    $ERROR('#3.1: Function parameters have attribute {DontDelete}');
  if (x[3] === 4)
    $ERROR('#3.2: Function parameters have attribute {DontDelete}');
  