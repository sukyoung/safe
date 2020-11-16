QUnit.module('lodash.takeRight');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should take the last two elements', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.takeRight(array, __num_top__), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should treat falsey `n` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [__num_top__] : [];
        });
        var actual = lodashStable.map(falsey, function (n) {
            return _.takeRight(array, n);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return an empty array when `n` < `1`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            __num_top__,
            -__num_top__,
            -Infinity
        ], function (n) {
            assert.deepEqual(_.takeRight(array, n), []);
        });
    });
    QUnit.test('should return all elements when `n` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __num_top__,
            __num_top__,
            Math.pow(__num_top__, __num_top__),
            Infinity
        ], function (n) {
            assert.deepEqual(_.takeRight(array, n), array);
        });
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
            ], actual = lodashStable.map(array, _.takeRight);
        assert.deepEqual(actual, [
            [__num_top__],
            [__num_top__],
            [__num_top__]
        ]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE), predicate = function (value) {
                    values.push(value);
                    return isEven(value);
                }, values = [], actual = _(array).takeRight(__num_top__).takeRight().value();
            assert.deepEqual(actual, _.takeRight(_.takeRight(array)));
            actual = _(array).filter(predicate).takeRight(__num_top__).takeRight().value();
            assert.deepEqual(values, array);
            assert.deepEqual(actual, _.takeRight(_.takeRight(_.filter(array, predicate), __num_top__)));
            actual = _(array).takeRight(__num_top__).take(__num_top__).takeRight(__num_top__).take().value();
            assert.deepEqual(actual, _.take(_.takeRight(_.take(_.takeRight(array, __num_top__), __num_top__), __num_top__)));
            values = [];
            actual = _(array).filter(predicate).takeRight(__num_top__).take(__num_top__).takeRight(__num_top__).take().value();
            assert.deepEqual(values, array);
            assert.deepEqual(actual, _.take(_.takeRight(_.take(_.takeRight(_.filter(array, predicate), __num_top__), __num_top__), __num_top__)));
        } else {
            skipAssert(assert, 6);
        }
    });
}());