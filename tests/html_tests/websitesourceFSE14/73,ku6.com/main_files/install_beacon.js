(function(g,h,e){

var j={
	getCookie:function(n){
		var m=h.cookie.match(new RegExp("(^| )"+n+"=([^;]*)(;|$)"));
		return(m!=null)?unescape(m[2]):"-"
  }
,setCookie:function(r,o,s,q,n,p){
	var m=new Date();
	m.setTime(m.getTime()+(s*24*60*60*1000));
	h.cookie=r+"="+o+";expires="+m.toGMTString()+((n==null)?"":(";domain="+n))+((q==null)?"":(";path="+q))+((p==true)?";secure":"")
}
,getRandom:function(min,max){
    return Math.floor(min+Math.random()*(max-min));
}
,getIP:function(){
    return j.getRandom(1, 999)+"."+j.getRandom(1, 999)+"."+j.getRandom(1, 999)+"."+j.getRandom(1, 999);
}
}
;
var d=j.getCookie("o_b_t_s");
var l=0;
var b=3*24*60*60*1000;
var a=new Date().getTime()-1200000000000;

var domain = h.URL.replace(/.+[\.\/]([A-Za-z0-9]+\.[A-Za-z]+)\/[^\/].+/,"$1");
var i;
if(d!="-"){
  var f=d.split('.')[4];
  var c=a-f;
  if(c>b){
	l=a;
  	j.setCookie("o_b_t_s",j.getIP()+"."+a+"."+j.getRandom(0,9),10000,"/",domain);
  }
  else{
  	l=f;
  	}
}else{
	if(d=="-"){
		j.setCookie("o_b_t_s",j.getIP()+"."+a+"."+j.getRandom(0,9),10000,"/",domain);
	}
	l=a;
}
if(typeof(g._beacon) == 'undefined'||g._beacon==''){
	g._beacon =j.getCookie("o_b_t_s");
}else{
	if(g._beacon.split('^')[0]!=j.getCookie("o_b_t_s")){
		g._beacon =j.getCookie("o_b_t_s")+"^"+g._beacon;
	}
}
if(location.search == '?from=bdbytj' || /from=bdbytj/.test(location.href)) return;
h.write('<script type="text/javascript" src="http://ipic.staticsdo.com/external/install_beacon.js"><\/script>');
}
)(window,document);