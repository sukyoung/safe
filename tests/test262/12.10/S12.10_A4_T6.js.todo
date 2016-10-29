// TODO eval: statement
this.p1 = 'a';
var myObj = {
  p1: function(){return 0;}, 
}
//eval("with(myObj){p1=function(){return 1;}}");
with(myObj){p1=function(){return 1;}}

var __result1 = true;
if(myObj.p1() !== 1){
    var __result1 = false;
}
var __expect1 = true;

var __result2 = true;
if(myObj.p1 === 'a'){
    var __result2 = false;
}
var __expect2 = true;
