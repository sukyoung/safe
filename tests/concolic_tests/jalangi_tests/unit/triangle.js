var Triangle = {EQUILATERAL:"EQUILATERAL", ISOSCELES:"ISOSCELES", SCALENE: "SCALENE", ERROR: "ERROR"};



function getType(a, b, c)
{
    if(a<=0||b<=0||c<=0)
        throw new Error("Length of sides cannot be equal to or less than zero");

    if(a==b && b== c&& c==a)
        return Triangle.EQUILATERAL;
    else if((a==b)||(b==c)||(c==a))
        return Triangle.ISOSCELES;
    else if(a!=b && b!=c && c!=a)
        return Triangle.SCALENE;
    else
        return Triangle.ERROR;
}

var ret = getType(1, 1, 1);

_<>_print(ret);
