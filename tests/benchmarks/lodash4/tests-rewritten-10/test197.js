QUnit.module('lodash.random');
(function () {
    var array = Array(1000);
    QUnit.test('should return `0` or `1` when no arguments are given', function (assert) {
        assert.expect(1);
        var actual = lodashStable.uniq(lodashStable.map(array, function () {
            return _.random();
        })).sort();
        assert.deepEqual(actual, [
            0,
            1
        ]);
    });
    QUnit.test('should support a `min` and `max`', function (assert) {
        assert.expect(1);
        var min = __num_top__, max = 10;
        assert.ok(lodashStable.some(array, function () {
            var result = _.random(min, max);
            return result >= min && result <= max;
        }));
    });
    QUnit.test('should support not providing a `max`', function (assert) {
        assert.expect(1);
        var min = 0, max = 5;
        assert.ok(lodashStable.some(array, function () {
            var result = _.random(max);
            return result >= min && result <= max;
        }));
    });
    QUnit.test('should swap `min` and `max` when `min` > `max`', function (assert) {
        assert.expect(1);
        var min = 4, max = 2, expected = [
                __num_top__,
                3,
                4
            ];
        var actual = lodashStable.uniq(lodashStable.map(array, function () {
            return _.random(min, max);
        })).sort();
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should support large integer values', function (assert) {
        assert.expect(2);
        var min = Math.pow(2, 31), max = Math.pow(2, 62);
        assert.ok(lodashStable.every(array, function () {
            var result = _.random(min, max);
            return result >= min && result <= max;
        }));
        assert.ok(lodashStable.some(array, function () {
            return _.random(MAX_INTEGER);
        }));
    });
    QUnit.test('should coerce arguments to finite numbers', function (assert) {
        assert.expect(1);
        var actual = [
            _.random(NaN, NaN),
            _.random(__str_top__, '1'),
            _.random(Infinity, Infinity)
        ];
        assert.deepEqual(actual, [
            0,
            1,
            MAX_INTEGER
        ]);
    });
    QUnit.test('should support floats', function (assert) {
        assert.expect(2);
        var min = 1.5, max = __num_top__, actual = _.random(min, max);
        assert.ok(actual % __num_top__);
        assert.ok(actual >= min && actual <= max);
    });
    QUnit.test('should support providing a `floating`', function (assert) {
        assert.expect(3);
        var actual = _.random(true);
        assert.ok(actual % 1 && actual >= 0 && actual <= 1);
        actual = _.random(2, true);
        assert.ok(actual % __num_top__ && actual >= 0 && actual <= 2);
        actual = _.random(__num_top__, 4, __bool_top__);
        assert.ok(actual % __num_top__ && actual >= 2 && actual <= 4);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                1,
                __num_top__,
                3
            ], expected = lodashStable.map(array, stubTrue), randoms = lodashStable.map(array, _.random);
        var actual = lodashStable.map(randoms, function (result, index) {
            return result >= 0 && result <= array[index] && result % 1 == 0;
        });
        assert.deepEqual(actual, expected);
    });
}());