/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var data = '{"foo": 123}';
var obj = JSON.parse(data);

var __result1 = obj.foo;
var __expect1 = 123;
