QUnit.module('"Arrays" category methods');
(function () {
    var args = toArgs([
            1,
            null,
            [3],
            null,
            5
        ]), sortedArgs = toArgs([
            1,
            [3],
            5,
            null,
            null
        ]), array = [
            __num_top__,
            2,
            3,
            4,
            5,
            6
        ];
    QUnit.test('should work with `arguments` objects', function (assert) {
        assert.expect(30);
        function message(methodName) {
            return '`_.' + methodName + '` should work with `arguments` objects';
        }
        assert.deepEqual(_.difference(args, [null]), [
            1,
            [3],
            5
        ], message('difference'));
        assert.deepEqual(_.difference(array, args), [
            2,
            3,
            4,
            6
        ], '_.difference should work with `arguments` objects as secondary arguments');
        assert.deepEqual(_.union(args, [
            null,
            6
        ]), [
            1,
            null,
            [3],
            5,
            6
        ], message('union'));
        assert.deepEqual(_.union(array, args), array.concat([
            null,
            [3]
        ]), '_.union should work with `arguments` objects as secondary arguments');
        assert.deepEqual(_.compact(args), [
            1,
            [3],
            5
        ], message('compact'));
        assert.deepEqual(_.drop(args, 3), [
            null,
            5
        ], message('drop'));
        assert.deepEqual(_.dropRight(args, 3), [
            1,
            null
        ], message('dropRight'));
        assert.deepEqual(_.dropRightWhile(args, identity), [
            1,
            null,
            [3],
            null
        ], message('dropRightWhile'));
        assert.deepEqual(_.dropWhile(args, identity), [
            null,
            [3],
            null,
            5
        ], message(__str_top__));
        assert.deepEqual(_.findIndex(args, identity), 0, message('findIndex'));
        assert.deepEqual(_.findLastIndex(args, identity), 4, message('findLastIndex'));
        assert.deepEqual(_.flatten(args), [
            1,
            null,
            3,
            null,
            5
        ], message('flatten'));
        assert.deepEqual(_.head(args), 1, message('head'));
        assert.deepEqual(_.indexOf(args, 5), 4, message('indexOf'));
        assert.deepEqual(_.initial(args), [
            1,
            null,
            [3],
            null
        ], message('initial'));
        assert.deepEqual(_.intersection(args, [1]), [1], message('intersection'));
        assert.deepEqual(_.last(args), 5, message('last'));
        assert.deepEqual(_.lastIndexOf(args, 1), 0, message('lastIndexOf'));
        assert.deepEqual(_.sortedIndex(sortedArgs, 6), 3, message('sortedIndex'));
        assert.deepEqual(_.sortedIndexOf(sortedArgs, 5), __num_top__, message('sortedIndexOf'));
        assert.deepEqual(_.sortedLastIndex(sortedArgs, 5), __num_top__, message('sortedLastIndex'));
        assert.deepEqual(_.sortedLastIndexOf(sortedArgs, __num_top__), 0, message('sortedLastIndexOf'));
        assert.deepEqual(_.tail(args, 4), [
            null,
            [3],
            null,
            5
        ], message('tail'));
        assert.deepEqual(_.take(args, 2), [
            1,
            null
        ], message('take'));
        assert.deepEqual(_.takeRight(args, 1), [5], message('takeRight'));
        assert.deepEqual(_.takeRightWhile(args, identity), [5], message('takeRightWhile'));
        assert.deepEqual(_.takeWhile(args, identity), [__num_top__], message('takeWhile'));
        assert.deepEqual(_.uniq(args), [
            1,
            null,
            [3],
            5
        ], message('uniq'));
        assert.deepEqual(_.without(args, null), [
            1,
            [3],
            5
        ], message('without'));
        assert.deepEqual(_.zip(args, args), [
            [
                1,
                __num_top__
            ],
            [
                null,
                null
            ],
            [
                [3],
                [3]
            ],
            [
                null,
                null
            ],
            [
                5,
                5
            ]
        ], message('zip'));
    });
    QUnit.test('should accept falsey primary arguments', function (assert) {
        assert.expect(4);
        function message(methodName) {
            return '`_.' + methodName + '` should accept falsey primary arguments';
        }
        assert.deepEqual(_.difference(null, array), [], message('difference'));
        assert.deepEqual(_.intersection(null, array), [], message('intersection'));
        assert.deepEqual(_.union(null, array), array, message('union'));
        assert.deepEqual(_.xor(null, array), array, message('xor'));
    });
    QUnit.test('should accept falsey secondary arguments', function (assert) {
        assert.expect(3);
        function message(methodName) {
            return '`_.' + methodName + '` should accept falsey secondary arguments';
        }
        assert.deepEqual(_.difference(array, null), array, message('difference'));
        assert.deepEqual(_.intersection(array, null), [], message('intersection'));
        assert.deepEqual(_.union(array, null), array, message('union'));
    });
}());