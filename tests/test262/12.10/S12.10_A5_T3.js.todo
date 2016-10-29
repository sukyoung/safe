// TODO eval: statement
this.p1 = 'a';
var myObj = {
  p1: true,
  del:false 
}

//eval("with(myObj){del = delete p1}");
with(myObj){del = delete p1}

var __result1 = true;
if(myObj.p1 === true){
    var __result1 = false;
}
var __expect1 = true;

var __result2 = true;
if(myObj.p1 !== undefined){
    var __result2 = false;
}
var __expect2 = true;

var __result3 = true;
if(myObj.del !== true){
    var __result3 = false;
}
var __expect3 = true;

var __result4 = true;
if(myObj.p1 === 'a'){
    var __result4 = false;
}
var __expect4 = true;
