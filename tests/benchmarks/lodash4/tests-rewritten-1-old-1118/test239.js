QUnit.module('lodash.take');
(function () {
    var array = [
        1,
        2,
        3
    ];
    QUnit.test('should take the first two elements', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.take(array, 2), [
            1,
            2
        ]);
    });
    QUnit.test('should treat falsey `n` values, except `undefined`, as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? [1] : [];
        });
        var actual = lodashStable.map(falsey, function (n) {
            return _.take(array, n);
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
            assert.deepEqual(_.take(array, n), []);
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
            assert.deepEqual(_.take(array, n), array);
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
            ], actual = lodashStable.map(array, _.take);
        assert.deepEqual(actual, [
            [1],
            [4],
            [7]
        ]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var array = lodashStable.range(1, LARGE_ARRAY_SIZE + 1), predicate = function (value) {
                    values.push(value);
                    return isEven(value);
                }, values = [], actual = _(array).take(2).take().value();
            assert.deepEqual(actual, _.take(_.take(array, 2)));
            actual = _(array).filter(predicate).take(2).take().value();
            assert.deepEqual(values, [
                1,
                2
            ]);
            assert.deepEqual(actual, _.take(_.take(_.filter(array, predicate), 2)));
            actual = _(array).take(6).takeRight(4).take(2).takeRight().value();
            assert.deepEqual(actual, _.takeRight(_.take(_.takeRight(_.take(array, __num_top__), 4), 2)));
            values = [];
            actual = _(array).take(array.length - 1).filter(predicate).take(6).takeRight(4).take(2).takeRight().value();
            assert.deepEqual(values, [
                1,
                2,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                12
            ]);
            assert.deepEqual(actual, _.takeRight(_.take(_.takeRight(_.take(_.filter(_.take(array, array.length - 1), predicate), 6), 4), 2)));
        } else {
            skipAssert(assert, 6);
        }
    });
}());