QUnit.module('"Arrays" category methods');
(function () {
    var args = toArgs([
            __num_top__,
            null,
            [__num_top__],
            null,
            __num_top__
        ]), sortedArgs = toArgs([
            __num_top__,
            [__num_top__],
            __num_top__,
            null,
            null
        ]), array = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
    QUnit.test('should work with `arguments` objects', function (assert) {
        assert.expect(30);
        function message(methodName) {
            return __str_top__ + methodName + __str_top__;
        }
        assert.deepEqual(_.difference(args, [null]), [
            __num_top__,
            [__num_top__],
            __num_top__
        ], message(__str_top__));
        assert.deepEqual(_.difference(array, args), [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], __str_top__);
        assert.deepEqual(_.union(args, [
            null,
            __num_top__
        ]), [
            __num_top__,
            null,
            [__num_top__],
            __num_top__,
            __num_top__
        ], message(__str_top__));
        assert.deepEqual(_.union(array, args), array.concat([
            null,
            [__num_top__]
        ]), __str_top__);
        assert.deepEqual(_.compact(args), [
            __num_top__,
            [__num_top__],
            __num_top__
        ], message(__str_top__));
        assert.deepEqual(_.drop(args, __num_top__), [
            null,
            __num_top__
        ], message(__str_top__));
        assert.deepEqual(_.dropRight(args, __num_top__), [
            __num_top__,
            null
        ], message(__str_top__));
        assert.deepEqual(_.dropRightWhile(args, identity), [
            __num_top__,
            null,
            [__num_top__],
            null
        ], message(__str_top__));
        assert.deepEqual(_.dropWhile(args, identity), [
            null,
            [__num_top__],
            null,
            __num_top__
        ], message(__str_top__));
        assert.deepEqual(_.findIndex(args, identity), __num_top__, message(__str_top__));
        assert.deepEqual(_.findLastIndex(args, identity), __num_top__, message(__str_top__));
        assert.deepEqual(_.flatten(args), [
            __num_top__,
            null,
            __num_top__,
            null,
            __num_top__
        ], message(__str_top__));
        assert.deepEqual(_.head(args), __num_top__, message(__str_top__));
        assert.deepEqual(_.indexOf(args, __num_top__), __num_top__, message(__str_top__));
        assert.deepEqual(_.initial(args), [
            __num_top__,
            null,
            [__num_top__],
            null
        ], message(__str_top__));
        assert.deepEqual(_.intersection(args, [__num_top__]), [__num_top__], message(__str_top__));
        assert.deepEqual(_.last(args), __num_top__, message(__str_top__));
        assert.deepEqual(_.lastIndexOf(args, __num_top__), __num_top__, message(__str_top__));
        assert.deepEqual(_.sortedIndex(sortedArgs, __num_top__), __num_top__, message(__str_top__));
        assert.deepEqual(_.sortedIndexOf(sortedArgs, __num_top__), __num_top__, message(__str_top__));
        assert.deepEqual(_.sortedLastIndex(sortedArgs, __num_top__), __num_top__, message(__str_top__));
        assert.deepEqual(_.sortedLastIndexOf(sortedArgs, __num_top__), __num_top__, message(__str_top__));
        assert.deepEqual(_.tail(args, __num_top__), [
            null,
            [__num_top__],
            null,
            __num_top__
        ], message(__str_top__));
        assert.deepEqual(_.take(args, __num_top__), [
            __num_top__,
            null
        ], message(__str_top__));
        assert.deepEqual(_.takeRight(args, __num_top__), [__num_top__], message(__str_top__));
        assert.deepEqual(_.takeRightWhile(args, identity), [__num_top__], message(__str_top__));
        assert.deepEqual(_.takeWhile(args, identity), [__num_top__], message(__str_top__));
        assert.deepEqual(_.uniq(args), [
            __num_top__,
            null,
            [__num_top__],
            __num_top__
        ], message(__str_top__));
        assert.deepEqual(_.without(args, null), [
            __num_top__,
            [__num_top__],
            __num_top__
        ], message(__str_top__));
        assert.deepEqual(_.zip(args, args), [
            [
                __num_top__,
                __num_top__
            ],
            [
                null,
                null
            ],
            [
                [__num_top__],
                [__num_top__]
            ],
            [
                null,
                null
            ],
            [
                __num_top__,
                __num_top__
            ]
        ], message(__str_top__));
    });
    QUnit.test('should accept falsey primary arguments', function (assert) {
        assert.expect(4);
        function message(methodName) {
            return __str_top__ + methodName + __str_top__;
        }
        assert.deepEqual(_.difference(null, array), [], message(__str_top__));
        assert.deepEqual(_.intersection(null, array), [], message(__str_top__));
        assert.deepEqual(_.union(null, array), array, message(__str_top__));
        assert.deepEqual(_.xor(null, array), array, message(__str_top__));
    });
    QUnit.test('should accept falsey secondary arguments', function (assert) {
        assert.expect(3);
        function message(methodName) {
            return __str_top__ + methodName + __str_top__;
        }
        assert.deepEqual(_.difference(array, null), array, message(__str_top__));
        assert.deepEqual(_.intersection(array, null), [], message(__str_top__));
        assert.deepEqual(_.union(array, null), array, message(__str_top__));
    });
}());