//  TODO [[DefaultValue]]
//   var __obj = {
//     toString : (function () 
//     {
//       return {
        
//       };
//     }),
//     valueOf : (function () 
//     {
//       throw "intostr";
//     })
//   };
//   var __obj2 = {
//     valueOf : (function () 
//     {
//       throw "intointeger";
//     })
//   };
//   __FACTORY.prototype.indexOf = String.prototype.indexOf;
//   var __instance = new __FACTORY(void 0);
//   try
// {    var x = __instance.indexOf(__obj, __obj2);
//     $FAIL('#1: "var x = __instance.indexOf(__obj, __obj2)" lead to throwing exception');}
//   catch (e)
// {    {
//       var __result1 = e !== "intostr";
//       var __expect1 = false;
//     }}

//   function __FACTORY(value) 
//   {
//     this.value = value;
//     this.toString = (function () 
//     {
//       return new Number;
//     });
//     this.valueOf = (function () 
//     {
//       return this.value + "";
//     });
//   }
  
