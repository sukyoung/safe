//   TODO getter/setter
//   function testcase() 
//   {
//     var o = {
//       
//     };
//     var getter = (function () 
//     {
//       return 1;
//     });
//     var d1 = {
//       get : getter,
//       enumerable : true,
//       configurable : false
//     };
//     Object.defineProperty(o, "foo", d1);
//     var desc = {
//       get : getter,
//       enumerable : false
//     };
//     try
// {      Object.defineProperty(o, "foo", desc);}
//     catch (e)
// {      if (e instanceof TypeError)
//       {
//         var d2 = Object.getOwnPropertyDescriptor(o, "foo");
//         if (d2.get === getter && d2.enumerable === true && d2.configurable === false)
//         {
//           return true;
//         }
//       }}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
