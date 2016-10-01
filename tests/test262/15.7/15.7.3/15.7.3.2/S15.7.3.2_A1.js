// math_precision.js
function getPrecision(num)
{
        log2num = Math.log(Math.abs(num))/Math.LN2;
        pernum = Math.ceil(log2num);
        return(2 * Math.pow(2, -52 + pernum));
        //return(0);                                                                            
}

// math_isequal.js
var prec;
function isEqual(num1, num2)
{
        if ((num1 === Infinity)&&(num2 === Infinity))
        {
                return(true);
        }
        if ((num1 === -Infinity)&&(num2 === -Infinity))
        {
                return(true);
        }
        prec = getPrecision(Math.min(Math.abs(num1), Math.abs(num2)));
        return(Math.abs(num1 - num2) <= prec);
        //return(num1 === num2);                                                                
}

  {
    var __result1 = ! isEqual(Number.MAX_VALUE, 1.7976931348623157e308);
    var __expect1 = false;
  }
  
