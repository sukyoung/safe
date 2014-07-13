/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var obj = {};
var obj2 = {x:1, y:2, z:3};
obj.x = 1;
obj.y = obj2.z;
obj2.y = obj.y;
