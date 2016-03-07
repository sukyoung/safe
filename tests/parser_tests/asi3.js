/* from http://inimino.org/~inimino/blog/javascript_semicolons */
var c,i,l,quitchars
quitchars=['q','Q']
charloop:while(c=getc()){
    for (i=0; i<quitchars.length; i++){
        if (c==quitchars[i])
            break
                charloop
    }
}
