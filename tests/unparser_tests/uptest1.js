function a() {
				var i = 0;
				var j;
				var l,m=1,n=0;
				return i;
}
function b(p, q, r) {
				p = p + 1;
				{
				p = p + q - r;
				p = p - (q + r);
				}
}
var a = function (p,q){
				return p+q;
}(1, 2);
a(2,1);
var b = function (p,q){
		var i = 0;
		var arr = [1,2,3,4,5,6];
		while(arr[i] < 5){
			if(arr[i] == 1){
							break;
			}

			if(arr[i] == 2){
							break;
			}else{
							continue;
			}

			if(arr[i] == 3){
							break;
			}else if(arr[i] == 4){
							continue;
			}else{
				break;
			}
			i++;
		}
		var bo = true;
		var bo2 = false;
		this.p = 2;
		try{
			throw new Exception();
		}catch(e){
			e = "abc";
			e = 'abcd';
			e = this.p;
			e = new Array();
			e = new String("abc");
//			e = 123.456;
			e = 1<2 ? 1 : 2;
			var gg = 10;
			switch(e){
				case 1:
					gg = 1;
					break;
				case 0:
					break;
				default:
					gg = 0;
					do{
						gg--;
					}while(false);
					do {} while(false);
					break;
				case 2:
					gg = 2;
					break;
				case 3:
					break;
			}
			return e;
		}finally{gg=100;}
		switch(p){}
		return p+q;
};
var d = function(){
	this.a = null;
	this.b = 123;
}
d.prototype = {
	x: function(p,q,r){
		return p+q+r;
	},
	y: function(z) {return "abc";}
}
var s,s2;
with(d){
	var j = 0;
	for(var i=0;i<10;i++,j++){

	}
	i++,j++,++j;
	for(;;){

	}
	var arr = [1,2,3];
	for(j in arr)
		for(var k in arr) continue;
};;;;
with(d) {a=1;}
{}
