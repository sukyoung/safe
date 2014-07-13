// 15.4.5.1 ToUint32(length) != ToNumber(length)
var arr = [0,1,2,3,4];
arr.length = 4294967296;	// Throw RangeError

