var x = [1,2,3,4,5,6]
try {  // for SAFE
	x.length = "string"
} catch(e) {  // for SAFE
	__result1 = e.name;  // for SAFE
}
__expect1 = "RangeError";  // for SAFE
