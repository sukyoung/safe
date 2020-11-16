QUnit.module('lodash.takeRight');
(function () {
    var array = [
        1,
        2,
        3
    ];
    QUnit.test('should take the last two elements', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.takeRight(array, 2), [
            2,
            3
        ]);
    });
    QUnit.test('should treat falsey `n` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [3] : [];
        });
        var actual = lodashStable.map(falsey, function (n) {
            return _.takeRight(array, n);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return an empty array when `n` < `1`', function (assert) {
        assert.expect(3);
        lodashStable.each([
            0,
            -__num_top__,
            -Infinity
        ], function (n) {
            assert.deepEqual(_.takeRight(array, n), []);
        });
    });
    QUnit.test('should return all elements when `n` >= `length`', function (assert) {
        assert.expect(4);
        lodashStable.each([
            3,
            4,
            Math.pow(2, __num_top__),
            Infinity
        ], function (n) {
            assert.deepEqual(_.takeRight(array, n), array);
        });
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                [
                    1,
                    2,
                    3
                ],
                [
                    4,
                    __num_top__,
                    6
                ],
                [
                    7,
                    8,
                    9
                ]
            ], actual = lodashStable.map(array, _.takeRight);
        assert.deepEqual(actual, [
            [3],
            [6],
            [__num_top__]
        ]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE), predicate = function (value) {
                    values.push(value);
                    return isEven(value);
                }, values = [], actual = _(array).takeRight(2).takeRight().value();
            assert.deepEqual(actual, _.takeRight(_.takeRight(array)));
            actual = _(array).filter(predicate).takeRight(2).takeRight().value();
            assert.deepEqual(values, array);
            assert.deepEqual(actual, _.takeRight(_.takeRight(_.filter(array, predicate), __num_top__)));
            actual = _(array).takeRight(6).take(4).takeRight(__num_top__).take().value();
            assert.deepEqual(actual, _.take(_.takeRight(_.take(_.takeRight(array, 6), __num_top__), __num_top__)));
            values = [];
            actual = _(array).filter(predicate).takeRight(__num_top__).take(4).takeRight(__num_top__).take().value();
            assert.deepEqual(values, array);
            assert.deepEqual(actual, _.take(_.takeRight(_.take(_.takeRight(_.filter(array, predicate), 6), 4), 2)));
        } else {
            skipAssert(assert, 6);
        }
    });
}());