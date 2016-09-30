//  TODO 15.9.3.1 new Date (year, month [, date [, hours [, minutes [, seconds [, ms]]]]])
//  var myObj = (function (val) 
//  {
//    this.value = val;
//    this.valueOf = (function () 
//    {
//      throw "valueOf-" + this.value;
//    });
//    this.toString = (function () 
//    {
//      throw "toString-" + this.value;
//    });
//  });
//  try
//{    var x1 = new Date(new myObj(1), new myObj(2), new myObj(3));
////    $ERROR("#1: The 1st step is calling ToNumber(year)");
//}
//  catch (e)
//{    {
//      var __result1 = e;
//      var __expect1 = "valueOf-1";
//    }}
//
//  
