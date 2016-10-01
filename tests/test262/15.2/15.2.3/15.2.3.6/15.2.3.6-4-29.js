//   TODO getter/setter
//   function testcase() 
//   {
//     var proto = {
//       
//     };
//     Object.defineProperty(proto, "foo", {
//       get : (function () 
//       {
//         
//       }),
//       configurable : true
//     });
//     var ConstructFun = (function () 
//     {
//       
//     });
//     ConstructFun.prototype = proto;
//     var obj = new ConstructFun();
//     Object.defineProperty(obj, "foo", {
//       get : (function () 
//       {
//         
//       }),
//       configurable : false
//     });
//     try
// {      Object.defineProperty(obj, "foo", {
//         configurable : true
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
