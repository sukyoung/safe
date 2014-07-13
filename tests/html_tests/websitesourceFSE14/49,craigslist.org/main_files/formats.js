(function(){var toStringProto=Object.prototype.toString;var toString=function(obj){return toStringProto.call(obj)};var boxedString=Object("a"),splitString=boxedString[0]!="a"||!(0 in boxedString);var toObject=function(o){if(o==null){throw new TypeError("can't convert "+o+" to object");}
return Object(o);};var ws="\x09\x0A\x0B\x0C\x0D\x20\xA0\u1680\u180E\u2000\u2001\u2002\u2003"+
"\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000\u2028"+
"\u2029\uFEFF";Object.create=Object.create||(function(){function F(){}
return function(o){if(arguments.length!=1){throw new Error('Object.create implementation only accepts one parameter.');}
F.prototype=o;return new F();};})()
Object.keys=Object.keys||(function(){var hasOwnProperty=Object.prototype.hasOwnProperty,hasDontEnumBug=!{toString:null}.propertyIsEnumerable("toString"),DontEnums=['toString','toLocaleString','valueOf','hasOwnProperty','isPrototypeOf','propertyIsEnumerable','constructor'],DontEnumsLength=DontEnums.length;return function(o){if(typeof o!="object"&&typeof o!="function"||o===null){throw new TypeError("Object.keys called on a non-object");}
var result=[];for(var name in o){if(hasOwnProperty.call(o,name)){result.push(name);}}
if(hasDontEnumBug){for(var i=0;i<DontEnumsLength;i++){if(hasOwnProperty.call(o,DontEnums[i])){result.push(DontEnums[i]);}}}
return result;};})();Array.isArray=Array.isArray||function(obj){return toString(obj)=="[object Array]";};Array.prototype.forEach=Array.prototype.forEach||function forEach(fun){var object=toObject(this),self=splitString&&toString(this)=="[object String]"?this.split(""):object,thisp=arguments[1],i=-1,length=self.length>>>0;if(toString(fun)!="[object Function]"){throw new TypeError();}
while(++i<length){if(i in self){fun.call(thisp,self[i],i,object);}}};Array.prototype.indexOf=Array.prototype.indexOf||function(searchElement){"use strict";if(this==null){throw new TypeError();}
var t=Object(this);var len=t.length>>>0;if(len===0){return-1;}
var n=0;if(arguments.length>1){n=Number(arguments[1]);if(n!=n){n=0;}else if(n!=0&&n!=Infinity&&n!=-Infinity){n=(n>0||-1)*Math.floor(Math.abs(n));}}
if(n>=len){return-1;}
var k=n>=0?n:Math.max(len-Math.abs(n),0);for(;k<len;k++){if(k in t&&t[k]===searchElement){return k;}}
return-1;};Array.prototype.map=Array.prototype.map||function(callback,thisArg){var T,A,k;if(this==null){throw new TypeError(" this is null or not defined");}
var O=Object(this);var len=O.length>>>0;if(typeof callback!=="function"){throw new TypeError(callback+" is not a function");}
if(thisArg){T=thisArg;}
A=new Array(len);k=0;while(k<len){var kValue,mappedValue;if(k in O){kValue=O[k];mappedValue=callback.call(T,kValue,k,O);A[k]=mappedValue;}
k++;}
return A;};Array.prototype.reduce=Array.prototype.reduce||function(callback,opt_initialValue){'use strict';if(null===this||'undefined'===typeof this){throw new TypeError('Array.prototype.reduce called on null or undefined');}
if('function'!==typeof callback){throw new TypeError(callback+' is not a function');}
var index,value,length=this.length>>>0,isValueSet=false;if(1<arguments.length){value=opt_initialValue;isValueSet=true;}
for(index=0;length>index;++index){if(this.hasOwnProperty(index)){if(isValueSet){value=callback(value,this[index],index,this);}
else{value=this[index];isValueSet=true;}}}
if(!isValueSet){throw new TypeError('Reduce of empty array with no initial value');}
return value;};Array.prototype.filter=Array.prototype.filter||function(fun){'use strict';if(!this){throw new TypeError();}
var objects=Object(this);var len=objects.length>>>0;if(typeof fun!=='function'){throw new TypeError();}
var res=[];var thisp=arguments[1];for(var i in objects){if(objects.hasOwnProperty(i)){if(fun.call(thisp,objects[i],i,objects)){res.push(objects[i]);}}}
return res;};Array.prototype.every=Array.prototype.every||function(fun){'use strict';var t,len,i,thisp;if(this==null){throw new TypeError();}
t=Object(this);len=t.length>>>0;if(typeof fun!=='function'){throw new TypeError();}
thisp=arguments[1];for(i=0;i<len;i++){if(i in t&&!fun.call(thisp,t[i],i,t)){return false;}}
return true;};if(!String.prototype.trim||ws.trim()){ws="["+ws+"]";var trimBeginRegexp=new RegExp("^"+ws+ws+"*"),trimEndRegexp=new RegExp(ws+ws+"*$");String.prototype.trim=function trim(){if(this===void 0||this===null){throw new TypeError("can't convert "+this+" to object");}
return String(this)
.replace(trimBeginRegexp,"")
.replace(trimEndRegexp,"");};}}());Base64=(function(){var _PADCHAR="=",_ALPHA="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",_VERSION="1.0";var _getbyte64=function(s,i){var idx=_ALPHA.indexOf(s.charAt(i));if(idx===-1){throw"Cannot decode base64";}
return idx;}
var _decode=typeof window.atob==='function'?function(s){return window.atob(s);}:function(s){var pads=0,i,b10,imax=s.length,x=[];s=String(s);if(imax===0){return s;}
if(imax%4!==0){throw"Cannot decode base64";}
if(s.charAt(imax-1)===_PADCHAR){pads=1;if(s.charAt(imax-2)===_PADCHAR){pads=2;}
imax-=4;}
for(i=0;i<imax;i+=4){b10=(_getbyte64(s,i)<<18)|(_getbyte64(s,i+1)<<12)|(_getbyte64(s,i+2)<<6)|_getbyte64(s,i+3);x.push(String.fromCharCode(b10>>16,(b10>>8)&0xff,b10&0xff));}
switch(pads){case 1:b10=(_getbyte64(s,i)<<18)|(_getbyte64(s,i+1)<<12)|(_getbyte64(s,i+2)<<6);x.push(String.fromCharCode(b10>>16,(b10>>8)&0xff));break;case 2:b10=(_getbyte64(s,i)<<18)|(_getbyte64(s,i+1)<<12);x.push(String.fromCharCode(b10>>16));break;}
return x.join("");};var _getbyte=function(s,i){var x=s.charCodeAt(i);if(x>255){throw"INVALID_CHARACTER_ERR: DOM Exception 5";}
return x;}
var _encode=typeof window.btoa==='function'?function(s){return window.btoa(s);}:function(s){if(arguments.length!==1){throw"SyntaxError: exactly one argument required";}
s=String(s);var i,b10,x=[],imax=s.length-s.length%3;if(s.length===0){return s;}
for(i=0;i<imax;i+=3){b10=(_getbyte(s,i)<<16)|(_getbyte(s,i+1)<<8)|_getbyte(s,i+2);x.push(_ALPHA.charAt(b10>>18));x.push(_ALPHA.charAt((b10>>12)&0x3F));x.push(_ALPHA.charAt((b10>>6)&0x3f));x.push(_ALPHA.charAt(b10&0x3f));}
switch(s.length-imax){case 1:b10=_getbyte(s,i)<<16;x.push(_ALPHA.charAt(b10>>18)+_ALPHA.charAt((b10>>12)&0x3F)+_PADCHAR+_PADCHAR);break;case 2:b10=(_getbyte(s,i)<<16)|(_getbyte(s,i+1)<<8);x.push(_ALPHA.charAt(b10>>18)+_ALPHA.charAt((b10>>12)&0x3F)+_ALPHA.charAt((b10>>6)&0x3f)+_PADCHAR);break;}
return x.join("");};return{decode:_decode,encode:_encode,VERSION:_VERSION};}());var CL=CL||{};CL.extend=function(namespace,obj){if(typeof namespace==='object'&&typeof obj==='undefined'){obj=namespace;namespace=[];}else if(typeof obj!=='object'){return;}else{namespace=namespace.split('.');}
var self=this;var objToExtend=self;var newNS;while(newNS=namespace.shift()){objToExtend[newNS]=objToExtend[newNS]||{};objToExtend=objToExtend[newNS];}
$.extend(true,objToExtend,obj);};CL.extend({browser:{androidVersion:(function(){return navigator.userAgent.match(/Android (\d)\.(\d)(?:\.(\d))?/);}()),ieVersion:(function(){var v=3,div=document.createElement('div'),all=div.getElementsByTagName('i');while(div.innerHTML='<!--[if gt IE '+(++v)+']><i></i><![endif]-->',all[0]);return v>4?v:undefined;}()),touchCapable:'ontouchstart'in window},cookies:{getItem:function(sKey){return decodeURIComponent(document.cookie.replace(new RegExp('(?:(?:^|.*;)\\s*'+encodeURIComponent(sKey).replace(/[\-\.\+\*]/g,"\\$&")+'\\s*\\=\\s*([^;]*).*$)|^.*$'),'$1'))||null;},setItem:function(sKey,sValue,options){options=options||{};if(!sKey||/^(?:expires|max\-age|path|domain|secure)$/i.test(sKey)){return false;}
var sExpires='';if(options.expires){switch(options.expires.constructor){case Number:sExpires=options.expires===Infinity?'; expires=Fri, 31 Dec 9999 23:59:59 GMT':'; max-age='+options.expires;break;case String:sExpires='; expires='+options.expires;break;case Date:sExpires='; expires='+options.expires.toUTCString();break;}}
var sDomain=options.domain?'; domain='+options.domain:'';var sPath=options.path?'; path='+options.path:'';var sSecure=options.secure?'; secure':'';document.cookie=encodeURIComponent(sKey)+"="+encodeURIComponent(sValue)+sExpires+sDomain+sPath+sSecure;return true;},removeItem:function(sKey,options){options=options||{};var sPath=options.path?'; path='+options.path:'';var sDomain=options.domain?'; domain='+options.domain:'';if(!sKey||!this.hasItem(sKey)){return false;}
document.cookie=encodeURIComponent(sKey)+'=; expires=Thu, 01 Jan 1970 00:00:00 GMT'+sDomain+sPath;return true;},hasItem:function(sKey){return(new RegExp('(?:^|;\\s*)'+encodeURIComponent(sKey).replace(/[\-\.\+\*]/g,"\\$&")+'\\s*\\=')).test(document.cookie);},keys:function(){var aKeys=document.cookie.replace(/((?:^|\s*;)[^\=]+)(?=;|$)|^\s*|\s*(?:\=[^;]*)?(?:\1|$)/g,'').split(/\s*(?:\=[^;]*)?;\s*/);for(var nIdx=0;nIdx<aKeys.length;nIdx++){aKeys[nIdx]=decodeURIComponent(aKeys[nIdx]);}
return aKeys;}},lightbox:(function(){var $window=$(window);var $document=$(document);var $body=$('body');var $lightbox;var $lbframe;var $lbcontent;var escHandler=function(e){if(e.keyCode===27){close();}};var noop=function(){};var defaults={beforeOpen:noop,afterOpen:noop,beforeClose:noop,afterClose:noop,beforeCenter:noop,afterCenter:noop,closeOnEsc:true,closeButton:true};var config={};var close=function(){config.beforeClose();$document.off('keydown',escHandler);$body.removeClass('lightbox');config.afterClose();};var center=function(){if(!$lightbox){return;}
config.beforeCenter();var ww=$window.width();var wh=$window.height();var fx,fy;fx=((ww-$lbframe.width())/2)+'px';fy=((wh-$lbframe.height())/2)+'px';$lbframe.css({top:fy,left:fx});config.afterCenter();};var create=function(content,opts){opts=(typeof opts==='object')?opts:{};config=$.extend({},defaults,opts);if(!$lightbox){$body.prepend('<div id="lightbox"></div>');$lightbox=$('#lightbox');$lightbox.prepend('<div class="lbbg"></div><div class="lbframe"><span class="lbclose">x</span><div class="lbcontent"></div></div>');$lbframe=$lightbox.find('.lbframe');$lbcontent=$lightbox.find('.lbcontent');if(config.closeButton){$lightbox.find('.lbclose').on('click touchstart',function(e){e.preventDefault();e.stopPropagation();close();});}else{$lightbox.find('.lbclose').hide();}}
$lbcontent.html(content);if(typeof opts.css==='object'){$lbcontent.css(opts.css);}
center();if(config.closeOnEsc){$document.on('keydown',escHandler);}
config.beforeOpen();$body.addClass('lightbox');config.afterOpen();};create.close=close;create.center=center;return create;}()),swipe:{makeGallery:function($container){$container=$container||$('body');var total,current,sw,$sliderNav=$container.find('.slidernav'),$sliderInfo=$sliderNav.find('.sliderinfo'),$back=$sliderNav.find('.back'),$forward=$sliderNav.find('.forward'),$swipe=$container.find('.swipe'),updateInfo=function(current,total){$sliderInfo.text(current+' of '+total);if(current==1){$back.prop('disabled',true);}
if(current>1){$back.prop('disabled',false);}
if(current==total){$forward.prop('disabled',true);}
if(current<total){$forward.prop('disabled',false);}};if($swipe.length===0){return;}
sw=new Swipe($swipe[0],{continuous:false,callback:function(i,el){updateInfo(i+1,total);}});total=sw.getNumSlides();updateInfo(1,total);$back.on('touchstart mousedown',function(e){e.preventDefault();e.stopPropagation();sw.prev();});$forward.on('touchstart mousedown',function(e){e.preventDefault();e.stopPropagation();sw.next();});}},url:{baseDomain:window.location.hostname.match(/craigslist\..+?$/)[0],param:(function(){var memo={};return function(key){var result;if(typeof memo[key]==='undefined'){result=new RegExp('[\?&]'+key+'=([^&]*)','i').exec(window.location.search);memo[key]=result&&result[1]||'';}
return memo[key];};})()},util:{isoDateString:function(d){function pad(n){return n<10?'0'+n:n}
return d.getUTCFullYear()+'-'
+pad(d.getUTCMonth()+1)+'-'
+pad(d.getUTCDate())+'T'
+pad(d.getUTCHours())+':'
+pad(d.getUTCMinutes())+':'
+pad(d.getUTCSeconds())+'Z';},safe:function(fn,context){if(typeof fn==='function'){return function(){var args=Array.prototype.slice.call(arguments,0);try{return fn.apply(context||this,args);}
catch(e){}};}}},when:{localStorageAvailable:$.Deferred()}});(function(safe,w){if(!w.JSON){w.JSON={};}
if(!w.localStorage){w.localStorage={};}
CL.extend({'JSON':{parse:safe(w.JSON.parse,w.JSON),stringify:safe(w.JSON.stringify,w.JSON)},'localStorage':{getItem:safe(w.localStorage.getItem,w.localStorage),setItem:safe(w.localStorage.setItem,w.localStorage),removeItem:safe(w.localStorage.removeItem,w.localStorage)},'jls':{getItem:safe(function(key){return JSON.parse(localStorage.getItem(key));}),setItem:safe(function(key,value){return localStorage.setItem(key,JSON.stringify(value));})}});})(CL.util.safe,window);if(CL.url.baseDomain&&window.localStorage){document.domain=CL.url.baseDomain;$('body').append('<iframe style="display: none;" src="//'+(window.lsDomain||'www')+'.'+CL.url.baseDomain+'/about/localstorage"></iframe>');}else{CL.when.localStorageAvailable.reject();}
var getCLFmt=function(){return(document.cookie.match('(^|; )cl_fmt=([^;]*)')||0)[2];};$(document).ready(function(){formats_autosize(pagetype);});function formats_autosize(pagetype){if(pagemode==='mobile'){if(typeof getCLFmt()==='undefined'){firstTimeMobileFormat();}
$('meta[name=viewport]').attr('content','width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0');var mobile_handler={homepage:homepage_size_mobile,toc:toc_size_mobile,posting:posting_size_mobile,post:post_size_mobile,simple:simple_size_mobile,sites:sites_size_mobile,account:account_size_mobile}[pagetype];if(typeof mobile_handler==="function"){mobile_handler();}
mobile_header();}
update_format_selector();}
function issueFormatCookie(format){var date=+(new Date())+(365*24*60*60*1000);CL.cookies.setItem('cl_fmt',format,{domain:CL.url.baseDomain,expires:date,path:'/'});}
function update_format_selector(){$('.clfooter .fsel').on('click',function(e){e.preventDefault();changeFormat($(this).attr('data-mode'));});}
var changeFormat=function(format){issueFormatCookie(format);window.location.href=window.location.href;};var firstTimeMobileFormat=function(){var $mobileFormatNotice=$('#mobileformatnotice');if($mobileFormatNotice.length){issueFormatCookie('mobile');$mobileFormatNotice.on('click','.desktopmode',function(){changeFormat('regular');}).show();}};function mobile_header(){var $header=$('.mobile').not('.post').find('header');var $contents=$header.find('.contents');var $breadbox=$header.find('.breadbox');var $breadcrumbLinks=$breadbox.find('a');var $backButton=$header.find('.back');var closedHeight,openHeight;var isOpen=false;var headerActions=function(e){if(e.target===$backButton[0]){}else if(isOpen&&$breadcrumbLinks.filter(e.target).length){}else{e.preventDefault();if(typeof closedHeight==='undefined'){closedHeight=$contents.height();openHeight=$breadbox.outerHeight();}
if(isOpen){$contents.height(closedHeight).removeClass('open').addClass('closed');}else{$contents.height(openHeight).addClass('open').removeClass('closed');}
isOpen=!isOpen;}};$contents.on('touchstart mousedown',headerActions);$backButton.on('click',function(e){window.history.go(-1);});$header.siblings().on('touchstart mousedown',function(e){if(isOpen){headerActions(e);}});var androidVersion=CL.browser.androidVersion;if(androidVersion&&androidVersion[1]==2){var timer;var $body=$('body');var bodyHeight=$body.height();$(window).on('scroll',function(){if(!timer){timer=setTimeout(function(){$body.height(bodyHeight++);timer=undefined;},300);}});}}
function build_sorted_cat_list(abbr){var catList={};var $oldCat=$('#'+abbr);var $oldCatLink=$oldCat.find('.ban a');var $allLink=$oldCatLink.clone().html($oldCatLink.data('alltitle')).wrapAll('<li></li>').parent();var $cat=$('<div id="'+abbr+'"></div>').append($oldCat.contents());var $listItems=$cat.find('li');$listItems.each(function(){catList[$(this).find('a').html()]=this;});var linkList=Object.keys(catList).sort();if(linkList.length){while(linkList[0].charAt(0)==='['){linkList.push(linkList.shift());}
linkList=linkList.map(function(item){return catList[item];});linkList.unshift($allLink[0]);$cat.find('.cats')
.html($('<ul id="'+abbr+'0"></ul>').html(linkList));}
return $cat;}
function homepage_size_mobile(){var $topban=$('#topban');var $body=$('.body');var $sublinks=$('.sublinks');var $search=$('#search');var $main=$('#main');var $cal=$('.cal');var $calban=$('#calban');$topban.prependTo('#pagecontainer').removeClass('ban');['center','rightbar'].forEach(function(v){$body.prepend($('<div id="'+v+'"></div>').html($('#'+v).detach().contents()));});$sublinks.prepend('<a href="//'+window.location.hostname+'" title="'+allText+'">'+allText+'</a>').prependTo($body).hide();var $center=$('#center');var $rightbar=$('#rightbar');$topban
.find('h2')
.wrapInner('<a href="#">')
.find('a')
.on('click',function(e){e.preventDefault();$rightbar.add($sublinks).slideToggle();})
.end()
.prepend(' &gt; ')
.prepend($('#logo a').first().html('CL'));$search
.find('>div:first').remove()
.end()
.insertAfter($rightbar)
.after($('#postlks'));$rightbar.find('h5').on('click',function(){var $this=$(this);if($this.hasClass('active')){return;}
var $prevActive=$rightbar.find('h5.active').not($this);if($prevActive.length){var thisTop=$this.offset().top;var prevTop=$prevActive.offset().top
var $document=$(document);if(prevTop<thisTop&&$document.scrollTop()>prevTop){$document.scrollTop(prevTop);}}});['sss','jjj','hhh','ppp','ccc','bbb','ggg'].forEach(function(v){var $newList=build_sorted_cat_list(v);$('#'+v).remove();$main.before($newList);});var finalItems=[document.getElementById('forums'),document.getElementById('res'),$calban[0],$cal[0]]
.filter(function(val){return val;});$(finalItems).removeClass('col').appendTo($center);$('.ban').filter(function(el){return $(this).siblings('.cats').children().length;})
.on('click',function(e){e.preventDefault();$(this).siblings('.cats').slideToggle();});$calban.addClass('ban').click(function(e){e.preventDefault();$cal.slideToggle();});$center.append($('.leftlinks')).show();$main.add('#container, #leftbar').remove();}
function toc_size_mobile(){$(window).bind('orientationchange',function(){$('#pagecontainer').css('width','100%');});build_toc_searchform();build_toc_results();}
function toc_orientation_flip(){if(window.innerWidth>window.innerHeight){$('body').removeClass('portrait');$('#tocright').appendTo('#pagecontainer');}else{$('body').addClass('portrait');$('#tocleft').appendTo('#pagecontainer');}}
function build_toc_searchform(){var $searchfieldset=$('#searchfieldset');var $query=$('#query').attr('size','');var $satabs=$('#satabs');var $expsearch;var $searchdrop;var searchOpen=false;$searchfieldset
.find(':submit')
.remove()
.end()
.append('<button id="topsubmit" type="submit">&gt;</button>')
.append($('<div class="leftside"></div>')
.append('<div class="expando"><button id="expsrch" type="button">+</button></div>')
.append($('<div class="searchbox"></div>').append($query)));var saSelect=[];if($satabs.length){$satabs
.removeClass('tabcontainer')
.children()
.each(function(i,el){var newOpt=document.createElement('option');newOpt.innerHTML=el.innerHTML;if(el.href){newOpt.value=el.href;saSelect.push(newOpt);}else{saSelect.unshift(newOpt);}});saSelect=$('<select class="subareas"></select>').append(saSelect);}
$('#searchtable')
.wrap('<div id="searchdrop" />')
.find('.searchgroup').first().empty().append('area: ').append(saSelect);$('.subareas').on('change',function(e){if(this.value){document.location.href=this.value;}});$searchdrop=$('#searchdrop');$expsearch=$('#expsrch');$expsearch.on('click',function(e){if(searchOpen){$searchdrop.slideUp();$expsearch.html('+');}else{$searchdrop.slideDown();$expsearch.html('&ndash;');}
searchOpen=!searchOpen;});$searchfieldset.show();}
function build_toc_results(){$('#toc_rows').find('.row')
.find('.gc').text(function(i,text){return text;}).end()
.on('click',function(e){var href=$(this).find('.pl').find('a').attr('href');if(href){window.location.href=href;}});}
function posting_size_mobile(){$('.cltags').before($('#attributes'));var $thumbs=$('#thumbs a');var $figure=$('figure.iw');var sliderHtml='';if($thumbs.length>1){sliderHtml+='<div class="slidernav"><button class="back" disabled="disabled">&lt;</button>'+
'<span class="sliderinfo"></span><button class="forward">&gt;</button></div>'+
'<div class="swipe"><div class="swipe-wrap">';for(var i=0,len=imgList.length;i<len;i++){sliderHtml+='<div><img src="'+imgList[i]+'" /></div>';}
sliderHtml+='</div></div>';$figure.html(sliderHtml);CL.swipe.makeGallery($figure);}}
function post_size_mobile(){var $header=$('header');var $managestatus=$('.managestatus');var $form=$('form');var $table=$form.find('table');$header.find('aside.highlight').appendTo('.post > header section.contents');$header.find('> br:last, #accountBlurb br').remove();$managestatus.find('a').prepend('<br>');$managestatus.find('form').prepend('<br>');$managestatus.find('table td').wrap('<tr />');$('blockquote > i').each(function(){var $this=$(this);$this.find('sup').each(function(){$(this).unwrap();});$this.prev('label').append('<br>');$this.appendTo($this.prev('label'));});if($table.attr('summary')==='neighborhood picker'){$table.find('td:last').prependTo($table.find('td blockquote'));}
if($table.attr('summary')==='flava picka'){$table.find('td fieldset').last().appendTo($table.find('td:first'));$table.find('td:first').append('<br>');$table.find('td:last').children().appendTo($table.find('td:first'));}
if($('.posting').addClass('mobile').length){posting_size_mobile();}}
function simple_size_mobile(){$('body').addClass('mobile');if($('table:first').css('width')==='706px'){$('td').each(function(){$(this).children().appendTo($('form'));});}
if($('table:first').css('width')==='500px'){$('td').each(function(){$(this).append('<br/>').children().prependTo('#content>div:first');});}
return false;}
function sites_size_mobile(){$('.box').children().unwrap();$('h1,h4').click(function(e){var menu=$(this).next('ul,.colmask');menu.slideToggle();$(this).parent().children('ul:visible,colmask:visible').not(menu).slideUp();});}
function account_size_mobile(){var $bchead=$('.bchead');var $ef=$('.bchead').find('#ef');var $satabs=$bchead.find('>#satabs');var $paginator=$('#paginator');$('body').removeClass('toc');$ef.find('>a:first').appendTo($satabs);$satabs.append(' ');$ef.find('>a:first').appendTo($satabs);$ef.remove();$('form').each(function(){var $this=$(this);$this.find('table td').children().appendTo($this.find('table td:first'));});$paginator.find('>table>tbody>tr').first().remove();$paginator.find('>table>tbody>tr').each(function(){var newDiv=$('<div class="postingrow"></div>');var $this=$(this);var posttitle=$this.find('.title');newDiv.append(posttitle.html())
.append($this.find('.areacat').html())
.append('&bull;')
.append($this.find('.dates').html())
.append('<br>')
.append($this.find('.status').html());newDiv.css({'background':posttitle.css('background'),'border':posttitle.css('border')});$paginator.append(newDiv);newDiv.click(function(e){e.preventDefault;window.location.href=posttitle.find('a').attr('href');});});$paginator.find('>table').remove();return false;}
