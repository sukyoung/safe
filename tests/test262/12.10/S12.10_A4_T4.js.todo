// TODO eval: statement
this.p1 = 'a';
var myObj = {
  p1: {a:"hello"}, 
}
//eval("with(myObj){p1={b:'hi'}}");
with(myObj){p1={b:'hi'}}

var __result1 = true;
if(myObj.p1.a === "hello"){
    var __result1 = false;
}
var __expect1 = true;

var __result2 = true;
if(myObj.p1.b !== "hi"){
    var __result2 = false;
}
var __expect2 = true;

var __result3 = true;
if(myObj.p1 === 'a'){
    var __result3 = false;
}
var __expect3 = true;
