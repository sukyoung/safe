(function(){
jQuery.merge = function (first, second) {
        var l = second.length,
            i = first.length,
            j = 0;

        if ( typeof l === "number" ) {
            for ( ; j < l; j++ ) {
                first[ i++ ] = second[ j ];
            }

        } else {
            while ( second[j] !== undefined ) {
                first[ i++ ] = second[ j++ ];
            }
        }

        first.length = i;

        return first;
    }

jQuery.prototype.pushStack = function (elems, name, selector) {

        // Build a new jQuery matched element set
        var ret = jQuery.merge( this.constructor(), elems );

        // Add the old object onto the stack (as a reference)
        ret.prevObject = this;

        ret.context = this.context;

        if ( name === "find" ) {
            ret.selector = this.selector + ( this.selector ? " " : "" ) + selector;
        } else if ( name ) {
            ret.selector = this.selector + "." + name + "(" + selector + ")";
        }

        // Return the newly-formed element set
        return ret;
    }

jQuery.prototype.map = function (callback) {
        return this.pushStack( jQuery.map(this, function( elem, i ) {
                    return callback.call( elem, i, elem );
                            }));
                            };
jQuery.prototype.domManip = 
function (args, table, callback) {

        // Flatten any nested arrays
        args = [].concat.apply( [], args );

        var results, first, fragment, iNoClone,
            i = 0,
            value = args[0],
            scripts = [],
            l = this.length;

        // We can't cloneNode fragments that contain checked, in WebKit
        if ( !jQuery.support.checkClone && l > 1 && typeof value === "string" && rchecked.test( value ) ) {
            return this.each(function() {
                jQuery(this).domManip( args, table, callback );
            });
        }

        if ( jQuery.isFunction(value) ) {
            return this.each(function(i) {
                var self = jQuery(this);
                args[0] = value.call( this, i, table ? self.html() : undefined );
                self.domManip( args, table, callback );
            });
        }

        if ( this[0] ) {
            results = jQuery.buildFragment( args, this, scripts );
            fragment = results.fragment;
            first = fragment.firstChild;

            if ( fragment.childNodes.length === 1 ) {
                fragment = first;
            }

            if ( first ) {
                table = table && jQuery.nodeName( first, "tr" );

                // Use the original fragment for the last item instead of the first because it can end up
                // being emptied incorrectly in certain situations (#8070).
                // Fragments from the fragment cache must always be cloned and never used in place.
                for ( iNoClone = results.cacheable || l - 1; i < l; i++ ) {
                    callback.call(
                        table && jQuery.nodeName( this[i], "table" ) ?
                            findOrAppend( this[i], "tbody" ) :
                            this[i],
                        i === iNoClone ?
                            fragment :
                            jQuery.clone( fragment, true, true )
                    );
                }
            }

            // Fix #11809: Avoid leaking memory
            fragment = first = null;

            if ( scripts.length ) {
                jQuery.each( scripts, function( i, elem ) {
                    if ( elem.src ) {
                        if ( jQuery.ajax ) {
                            jQuery.ajax({
                                url: elem.src,
                                type: "GET",
                                dataType: "script",
                                async: false,
                                global: false,
                                "throws": true
                            });
                        } else {
                            jQuery.error("no ajax");
                        }
                    } else {
                        jQuery.globalEval( ( elem.text || elem.textContent || elem.innerHTML || "" ).replace( rcleanScript, "" ) );
                    }

                    if ( elem.parentNode ) {
                        elem.parentNode.removeChild( elem );
                    }
                });
            }
        }

        return this;
    };
jQuery.prototype.append = 
function () {
        return this.domManip(arguments, true, function( elem ) {
            if ( this.nodeType === 1 || this.nodeType === 11 ) {
                this.appendChild( elem );
            }
        });
    };
jQuery.prototype.appendTo = 
function (e) {
        var n, r = 0, i = [], o = jQuery(e), a = o.length - 1;
        for(;a >= r;r ++)
        {
          n = r === a ? this : this.clone(! 0), jQuery(o[r])["append"](n), 
          Array.prototype.push.apply(i, n.get());
        }
        return this.pushStack(i);
      };

})();
