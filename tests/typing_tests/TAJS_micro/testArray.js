// Constructor tests
var arr = []
//dumpObject(arr)
var __result1 = arr.length;  // for SAFE
var __expect1 = 0;  // for SAFE

var newArr0 = new Array()
//dumpObject(newArr0)
var __result2 = newArr0.length;  // for SAFE
var __expect2 = 0;  // for SAFE

var newArr01 = [2]
//dumpObject(newArr01)
var __result3 = newArr01[0];  // for SAFE
var __expect3 = 2;  // for SAFE

var newArr02 = ["a"]
//dumpObject(newArr02)
var __result4 = newArr02[0];  // for SAFE
var __expect4 = "a";  // for SAFE

var newArr1 = new Array("test");
//dumpObject(newArr1)
var __result5 = newArr1[0];  // for SAFE
var __expect5 = "test";  // for SAFE

var newArr2 = new Array(42, 45);
//dumpObject(newArr2)
var __result6 = newArr2[0];  // for SAFE
var __expect6 = 42;  // for SAFE

var __result7 = newArr2[1];  // for SAFE
var __expect7 = 45;  // for SAFE

var newArr42 = new Array(42)
//dumpObject(newArr42)
var __result8 = newArr42.length;  // for SAFE
var __expect8 = 42;  // for SAFE

if (Math.random()) 
    var gt = {gt:23, bg:43}
else
    var gt = 42
//dumpValue(gt);

var newArrMNum = new Array(gt);
//dumpObject(newArrMNum);
var __result9 = newArrMNum[0].gt;  // for SAFE
var __expect9 = 23;  // for SAFE

var __result10 = newArrMNum[0].bg;  // for SAFE
var __expect10 = 43;  // for SAFE

var __result11 = newArrMNum.length;  // for SAFE
var __expect11 = 42;  // for SAFE

if (Math.random()) 
    var yt = {gt:23, bg:43}
else
    if (Math.random()) var yt = 2;
    else var yt = 564;
//dumpValue(yt);

var newArrNNum = new Array(yt);
//dumpObject(newArrNNum);
var __result12 = newArrNNum[0].gt;  // for SAFE
var __expect12 = 23;  // for SAFE

var __result13 = newArrNNum[0].bg;  // for SAFE
var __expect13 = 43;  // for SAFE

var __result14 = newArrNNum.length;  // for SAFE
var __expect14 = 2;  // for SAFE

var __result15 = newArrNNum.length;  // for SAFE
var __expect15 = 564;  // for SAFE

// NYI
// Length test
//var arr = [1,2]
//dumpObject(arr);
//arr[42] = "string"
//dumpObject(arr);
//
//var arr = [1,2]
////arr[1] = 1000;
//dumpObject(arr);
//arr["ni"] = 23;
//dumpObject(arr);
//dumpValue(arr.toString())
//
//var arr = [];
//dumpValue(arr.length);
//arr.length = 67;
//dumpValue(arr.length);
//assert(arr.length == 67);
//
//var arr = [1,2,3,4,5,6,7,8,9,0]
//arr.length = 3;
//dumpObject(arr);
//
if (Math.random())
    var maybeNum = 4;
else
    var maybeNum = 6;
//dumpValue(maybeNum);
//
//var arr = [1,23,4]
//arr[maybeNum] = 34;
//dumpObject(arr)
//
//var arr = [1,2]
//arr.length = maybeNum
//dumpObject(arr);
//arr[7] = 32;
//dumpObject(arr);

// toString and join test
//dumpValue([].toString())
var __result16 = [].toString();  // for SAFE
var __expect16 = "";  // for SAFE

var arr = [1,2,3,undefined,4,5,6]
//dumpValue(arr.toString())
var __result17 = arr.toString();  // for SAFE
var __expect17 = "1,2,3,,4,5,6";  // for SAFE

//dumpValue(arr.join("   "));
var __result18 = arr.join("   ");  // for SAFE
var __expect18 = "1   2   3      4   5   6";  // for SAFE

//dumpValue(arr.join(maybeNum))
var __result19 = arr.join(maybeNum);  // for SAFE
var __expect19 = "142434444546";  // for SAFE

var __result20 = arr.join(maybeNum);  // for SAFE
var __expect20 = "162636646566";  // for SAFE

var arr = [1,2,3,4,5,maybeNum,4]
//dumpValue(arr.toString())
var __result21 = arr.toString();  // for SAFE
var __expect21 = "1,2,3,4,5,4,4";  // for SAFE

var __result22 = arr.toString();  // for SAFE
var __expect22 = "1,2,3,4,5,6,4";  // for SAFE

if (Math.random())
    var lor = [9,2,8]
else
    var lor = [1,2]
//dumpValue(lor.toString())
var __result23 = lor.toString();  // for SAFE
var __expect23 = "9,2,8";  // for SAFE

var __result24 = lor.toString();  // for SAFE
var __expect24 = "1,2";  // for SAFE

//Concat
var arr1 = [1,3,4,5,"dfg"]
var arr2 = [1,43,"sdf",34]
//dumpObject(arr1.concat(arr2))
var __result25 = arr1.concat(arr2).toString();  // for SAFE
var __expect25 = "1,3,4,5,dfg,1,43,sdf,34";  // for SAFE

//dumpObject(arr1.concat({gt:6, bt:34}));
var __result26 = arr1.concat({gt:6, bt:34}).toString();  // for SAFE
var __expect26 = "1,3,4,5,dfg,[object Object]";  // for SAFE

if (Math.random()) 
    var fr = {gt: 4}
else
    var fr = [1,2]
//dumpObject(arr1.concat(fr))
var __result27 = arr1.concat(fr)[5].gt;  // for SAFE
var __expect27 = 4;  // for SAFE

var __result29 = arr1.concat(fr)[5];  // for SAFE
var __expect29 = 1;  // for SAFE

var __result30 = arr1.concat(fr)[6];  // for SAFE
var __expect30 = 2;  // for SAFE

//dumpObject(arr1.concat("string"))
var __result31 = arr1.concat("string")[5];  // for SAFE
var __expect31 = "string";  // for SAFE

// Push and pop test
var arr = [1]
//arr.push("string", 34);
var __result32 = arr.push("string", 34);  // for SAFE
var __expect32 = 3;  // for SAFE

//dumpObject(arr);

//dumpValue(arr.pop())
var __result33 = arr.pop();  // for SAFE
var __expect33 = 34;  // for SAFE

//dumpObject(arr);

//arr.push(100);
var __result34 = arr.push(100);  // for SAFE
var __expect34 = 3;  // for SAFE

//arr.push(243);
var __result35 = arr.push(243);  // for SAFE
var __expect35 = 4;  // for SAFE

//dumpObject(arr)

//dumpValue(arr.pop())
var __result36 = arr.pop();  // for SAFE
var __expect36 = 243;  // for SAFE

//dumpObject(arr);

//dumpValue([].pop())
var __result37 = [].pop();  // for SAFE
var __expect37 = undefined;  // for SAFE

// Reverse
//dumpObject([].reverse());
var __result38 = [].reverse().toString();  // for SAFE
var __expect38 = "";  // for SAFE

var arr = [0,1,2,3,4,5,6,7]
arr.reverse();
var __result39 = arr.toString();  // for SAFE
var __expect39 = "7,6,5,4,3,2,1,0";  // for SAFE

// NYI
//dumpObject(arr);
//arr.length = maybeNum;
//arr.reverse()
//dumpObject(arr);

// Shift
arr = [];
//dumpValue(arr.shift());
//dumpObject(arr); 
var __result40 = arr.shift();  // for SAFE
var __expect40 = undefined;  // for SAFE

var arr = [0,1,2,3,4,5,6,7,8,9]
//dumpValue(arr.shift());
//dumpObject(arr);
var __result41 = arr.shift();  // for SAFE
var __expect41 = 0;  // for SAFE

var __result42 = arr.toString();  // for SAFE
var __expect42 = "1,2,3,4,5,6,7,8,9";  // for SAFE

// NYI
//var arr = [0,1,2,3,4,5,6,7,8,9]
//arr.length = 11;
//dumpValue(arr.shift());
//dumpObject(arr);

var arr = [0,1,2,3,4,5,6,7,8,9]
delete arr[2]
//dumpObject(arr)
//dumpValue(arr.shift());
//dumpObject(arr);
var __result43 = arr.shift();  // for SAFE
var __expect43 = 0;  // for SAFE

var __result44 = arr.toString();  // for SAFE
var __expect44 = "1,,3,4,5,6,7,8,9";  // for SAFE

// NYI
//arr.length = maybeNum
//dumpValue(arr.shift());
//dumpObject(arr);
