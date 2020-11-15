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
            -1,
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
            Math.pow(2, 32),
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
                    5,
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
            [9]
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
            assert.deepEqual(actual, _.takeRight(_.takeRight(_.filter(array, predicate), 2)));
            actual = _(array).takeRight(6).take(4).takeRight(2).take().value();
            assert.deepEqual(actual, _.take(_.takeRight(_.take(_.takeRight(array, 6), 4), 2)));
            values = [];
            actual = _(array).filter(predicate).takeRight(6).take(4).takeRight(2).take().value();
            assert.deepEqual(values, array);
            assert.deepEqual(actual, _.take(_.takeRight(_.take(_.takeRight(_.filter(array, predicate), 6), 4), 2)));
        } else {
            skipAssert(assert, __num_top__);
        }
    });
}());