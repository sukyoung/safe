QUnit.module('lodash.remove');
(function () {
    QUnit.test('should modify the array and return removed elements', function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ], actual = _.remove(array, isEven);
        assert.deepEqual(array, [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should provide correct `predicate` arguments', function (assert) {
        assert.expect(1);
        var argsList = [], array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], clone = array.slice();
        _.remove(array, function (n, index) {
            var args = slice.call(arguments);
            args[__num_top__] = args[__num_top__].slice();
            argsList.push(args);
            return isEven(index);
        });
        assert.deepEqual(argsList, [
            [
                __num_top__,
                __num_top__,
                clone
            ],
            [
                __num_top__,
                __num_top__,
                clone
            ],
            [
                __num_top__,
                __num_top__,
                clone
            ]
        ]);
    });
    QUnit.test('should work with `_.matches` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': __num_top__,
                'b': __num_top__
            },
            {
                'a': __num_top__,
                'b': __num_top__
            }
        ];
        _.remove(objects, { 'a': __num_top__ });
        assert.deepEqual(objects, [{
                'a': __num_top__,
                'b': __num_top__
            }]);
    });
    QUnit.test('should work with `_.matchesProperty` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': __num_top__,
                'b': __num_top__
            },
            {
                'a': __num_top__,
                'b': __num_top__
            }
        ];
        _.remove(objects, [
            __str_top__,
            __num_top__
        ]);
        assert.deepEqual(objects, [{
                'a': __num_top__,
                'b': __num_top__
            }]);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
            { 'a': __num_top__ },
            { 'a': __num_top__ }
        ];
        _.remove(objects, __str_top__);
        assert.deepEqual(objects, [{ 'a': __num_top__ }]);
    });
    QUnit.test('should preserve holes in arrays', function (assert) {
        assert.expect(2);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
        delete array[__num_top__];
        delete array[__num_top__];
        _.remove(array, function (n) {
            return n === __num_top__;
        });
        assert.notOk(__str_top__ in array);
        assert.notOk(__str_top__ in array);
    });
    QUnit.test('should treat holes as `undefined`', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        delete array[__num_top__];
        _.remove(array, function (n) {
            return n == null;
        });
        assert.deepEqual(array, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should not mutate the array until all elements to remove are determined', function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        _.remove(array, function (n, index) {
            return isEven(index);
        });
        assert.deepEqual(array, [__num_top__]);
    });
}());