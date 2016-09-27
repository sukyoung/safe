/* from http://inimino.org/~inimino/blog/javascript_semicolons */
a = b + c
(d + e).print()
a = b + c
;(d + e).print()

[['January','Jan']
,['February','Feb']
,['March','Mar']
,['April','Apr']
,['May','May']
,['June','Jun']
,['July','Jul']
,['August','Aug']
,['September','Sep']
,['October','Oct']
,['November','Nov']
,['December','Dec']
].forEach(function(a){ print("The abbreviation of "+a[0]+" is "+a[1]+".") })

['/script.js'
,'/style1.css'
,'/style2.css'
,'/page1.html'
].forEach(function(uri){
   log('Looking up and caching '+uri)
   fetch_and_cache(uri)})

var i,s
s="here is a string"
i=0
/[a-z]/g.exec(s)

var x,y,z
x = +y;    // useful
y = -y;    // useful
print(-y); // useful
+z;        // useless
