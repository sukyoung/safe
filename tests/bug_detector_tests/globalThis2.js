/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

function foo() {return this.Math;}
foo();

var x = {p:1};
x.bar = function boo() {return this.p;}

x.bar();
