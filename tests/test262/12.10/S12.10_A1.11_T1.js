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
  var f = (function () 
  {
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
  });
  with (myObj)
  {
    f();
  }
  {
    var __result1 = ! (p1 === "x1");
    var __expect1 = false;
  }
  {
    var __result2 = ! (p2 === "x2");
    var __expect2 = false;
  }
  try
{    p3;
    $ERROR('#3: p3 is nod defined');}
  catch (e)
{    }

  try
{    p4;
    $ERROR('#4: p4 is not defined');}
  catch (e)
{    }

  {
    var __result3 = ! (p5 === "x5");
    var __expect3 = false;
  }
  {
    var __result4 = ! (myObj.p1 === "a");
    var __expect4 = false;
  }
  {
    var __result5 = ! (myObj.p2 === "b");
    var __expect5 = false;
  }
  {
    var __result6 = ! (myObj.p3 === "c");
    var __expect6 = false;
  }
  {
    var __result7 = ! (myObj.p4 === undefined);
    var __expect7 = false;
  }
  {
    var __result8 = ! (myObj.p5 === undefined);
    var __expect8 = false;
  }
  {
    var __result9 = ! (st_parseInt === parseInt);
    var __expect9 = false;
  }
  {
    var __result10 = ! isNaN(st_NaN);
    var __expect10 = false;
  }
  {
    var __result11 = ! (st_Infinity === Infinity);
    var __expect11 = false;
  }
  {
    var __result12 = ! (st_eval === eval);
    var __expect12 = false;
  }
  {
    var __result13 = ! (st_parseFloat === parseFloat);
    var __expect13 = false;
  }
  {
    var __result14 = ! (st_isNaN === isNaN);
    var __expect14 = false;
  }
  {
    var __result15 = ! (st_isFinite === isFinite);
    var __expect15 = false;
  }
  try
{    value;
    $ERROR('#18: value is not defined');}
  catch (e)
{    }

  {
    var __result16 = ! (myObj.value === "myObj_value");
    var __expect16 = false;
  }
  