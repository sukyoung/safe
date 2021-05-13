QUnit.module('lodash.drop');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should drop the first two elements', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.drop(array, __num_top__), [__num_top__]);
    });
    QUnit.test('should treat falsey `n` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [
                __num_top__,
                __num_top__
            ] : array;
        });
        var actual = lodashStable.map(falsey, function (n) {
            return _.drop(array, n);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return all elements when `n` < `1`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            __num_top__,
            -__num_top__,
            -Infinity
        ], function (n) {
            assert.deepEqual(_.drop(array, n), array);
        });
    });
    QUnit.test('should return an empty array when `n` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __num_top__,
            __num_top__,
            Math.pow(__num_top__, __num_top__),
            Infinity
        ], function (n) {
            assert.deepEqual(_.drop(array, n), []);
        });
    });
    QUnit.test('should coerce `n` to an integer', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.drop(array, __num_top__), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ],
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
            ], actual = lodashStable.map(array, _.drop);
        assert.deepEqual(actual, [
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var array = lodashStable.range(__num_top__, LARGE_ARRAY_SIZE + __num_top__), predicate = function (value) {
                    values.push(value);
                    return isEven(value);
                }, values = [], actual = _(array).drop(__num_top__).drop().value();
            assert.deepEqual(actual, array.slice(__num_top__));
            actual = _(array).filter(predicate).drop(__num_top__).drop().value();
            assert.deepEqual(values, array);
            assert.deepEqual(actual, _.drop(_.drop(_.filter(array, predicate), __num_top__)));
            actual = _(array).drop(__num_top__).dropRight().drop().dropRight(__num_top__).value();
            assert.deepEqual(actual, _.dropRight(_.drop(_.dropRight(_.drop(array, __num_top__))), __num_top__));
            values = [];
            actual = _(array).drop().filter(predicate).drop(__num_top__).dropRight().drop().dropRight(__num_top__).value();
            assert.deepEqual(values, array.slice(__num_top__));
            assert.deepEqual(actual, _.dropRight(_.drop(_.dropRight(_.drop(_.filter(_.drop(array), predicate), __num_top__))), __num_top__));
        } else {
            skipAssert(assert, 6);
        }
    });
}());