  this.p1 = 1;
  this.p2 = 2;
  this.p3 = 3;
  var result = "result";
  var myObj = {
    p1 : 'a',
    p2 : 'b',
    p3 : 'c',
    value : 'myObj_value',
    valueOf : (function () 
    {
      return 'obj_valueOf';
    }),
    parseInt : (function () 
    {
      return 'obj_parseInt';
    }),
    NaN : 'obj_NaN',
    Infinity : 'obj_Infinity',
    eval : (function () 
    {
      return 'obj_eval';
    }),
    parseFloat : (function () 
    {
      return 'obj_parseFloat';
    }),
    isNaN : (function () 
    {
      return 'obj_isNaN';
    }),
    isFinite : (function () 
    {
      return 'obj_isFinite';
    })
  };
  var del;
  var st_p1 = "p1";
  var st_p2 = "p2";
  var st_p3 = "p3";
  var st_parseInt = "parseInt";
  var st_NaN = "NaN";
  var st_Infinity = "Infinity";
  var st_eval = "eval";
  var st_parseFloat = "parseFloat";
  var st_isNaN = "isNaN";
  var st_isFinite = "isFinite";
  with (myObj)
  {
    do
    {
      break;
      st_p1 = p1;
      st_p2 = p2;
      st_p3 = p3;
      st_parseInt = parseInt;
      st_NaN = NaN;
      st_Infinity = Infinity;
      st_eval = eval;
      st_parseFloat = parseFloat;
      st_isNaN = isNaN;
      st_isFinite = isFinite;
      p1 = 'x1';
      this.p2 = 'x2';
      del = delete p3;
      var p4 = 'x4';
      p5 = 'x5';
      var value = 'value';
    }while (false);
  }
  {
    var __result1 = ! (p1 === 1);
    var __expect1 = false;
  }
  {
    var __result2 = ! (p2 === 2);
    var __expect2 = false;
  }
  {
    var __result3 = ! (p3 === 3);
    var __expect3 = false;
  }
  {
    var __result4 = ! (p4 === undefined);
    var __expect4 = false;
  }
  try
{    p5;
    $ERROR('#5: p5 is not defined');}
  catch (e)
{    }

  {
    var __result5 = ! (myObj.p1 === "a");
    var __expect5 = false;
  }
  {
    var __result6 = ! (myObj.p2 === "b");
    var __expect6 = false;
  }
  {
    var __result7 = ! (myObj.p3 === "c");
    var __expect7 = false;
  }
  {
    var __result8 = ! (myObj.p4 === undefined);
    var __expect8 = false;
  }
  {
    var __result9 = ! (myObj.p5 === undefined);
    var __expect9 = false;
  }
  {
    var __result10 = ! (st_parseInt === "parseInt");
    var __expect10 = false;
  }
  {
    var __result11 = ! (st_NaN === "NaN");
    var __expect11 = false;
  }
  {
    var __result12 = ! (st_Infinity === "Infinity");
    var __expect12 = false;
  }
  {
    var __result13 = ! (st_eval === "eval");
    var __expect13 = false;
  }
  {
    var __result14 = ! (st_parseFloat === "parseFloat");
    var __expect14 = false;
  }
  {
    var __result15 = ! (st_isNaN === "isNaN");
    var __expect15 = false;
  }
  {
    var __result16 = ! (st_isFinite === "isFinite");
    var __expect16 = false;
  }
  {
    var __result17 = ! (value === undefined);
    var __expect17 = false;
  }
  {
    var __result18 = ! (myObj.value === "myObj_value");
    var __expect18 = false;
  }
  