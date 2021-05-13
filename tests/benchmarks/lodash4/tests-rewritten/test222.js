QUnit.module('lodash.sortBy');
(function () {
    var objects = [
        {
            'a': __str_top__,
            'b': __num_top__
        },
        {
            'a': __str_top__,
            'b': __num_top__
        },
        {
            'a': __str_top__,
            'b': __num_top__
        },
        {
            'a': __str_top__,
            'b': __num_top__
        }
    ];
    QUnit.test('should sort in ascending order by `iteratee`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(_.sortBy(objects, function (object) {
            return object.b;
        }), __str_top__);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                __num_top__,
                __num_top__,
                __num_top__
            ]));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.sortBy(array, value) : _.sortBy(array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(_.sortBy(objects.concat(undefined), __str_top__), __str_top__);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            undefined
        ]);
    });
    QUnit.test('should work with an object for `collection`', function (assert) {
        assert.expect(1);
        var actual = _.sortBy({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }, Math.sin);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should move `NaN`, nullish, and symbol values to the end', function (assert) {
        assert.expect(2);
        var symbol1 = Symbol ? Symbol(__str_top__) : null, symbol2 = Symbol ? Symbol(__str_top__) : null, array = [
                NaN,
                undefined,
                null,
                __num_top__,
                symbol1,
                null,
                __num_top__,
                symbol2,
                undefined,
                __num_top__,
                NaN,
                __num_top__
            ], expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                symbol1,
                symbol2,
                null,
                null,
                undefined,
                undefined,
                NaN,
                NaN
            ];
        assert.deepEqual(_.sortBy(array), expected);
        array = [
            NaN,
            undefined,
            symbol1,
            null,
            __str_top__,
            null,
            __str_top__,
            symbol2,
            undefined,
            __str_top__,
            NaN,
            __str_top__
        ];
        expected = [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__,
            symbol1,
            symbol2,
            null,
            null,
            undefined,
            undefined,
            NaN,
            NaN
        ];
        assert.deepEqual(_.sortBy(array), expected);
    });
    QUnit.test('should treat number values for `collection` as empty', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.sortBy(__num_top__), []);
    });
    QUnit.test('should coerce arrays returned from `iteratee`', function (assert) {
        assert.expect(1);
        var actual = _.sortBy(objects, function (object) {
            var result = [
                object.a,
                object.b
            ];
            result.toString = function () {
                return String(this[__num_top__]);
            };
            return result;
        });
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__]
        ]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([
            [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__,
                __num_top__
            ]
        ], _.sortBy);
        assert.deepEqual(actual, [
            [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__,
                __num_top__
            ]
        ]);
    });
}());