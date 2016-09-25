var __result1 = String.fromCharCode(65) 
var __expect1 = "A"

var __result2 = String.fromCharCode(65, 66, 67, 68) 
var __expect2 = "ABCD"

var __result3 = String.fromCharCode(65536+65, 65536+66) 
var __expect3 = "AB"

var __result4 = String.fromCharCode() 
var __expect4 = ""

var __result5 = String.fromCharCode(65-65536, 66-65536) 
var __expect5 = "AB"

var __result6 = String.fromCharCode(66,67);
var __expect6 = "BC";

var __result7 = String.fromCharCode(49,50,51,52,53)
var __expect7 = "12345"
