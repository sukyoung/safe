/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var obj1 = {x:"A", y:"B"};
var obj2 = {a:"C", b:"D", c:3, d:obj1};
delete obj2.b;
delete obj2.d.x;
