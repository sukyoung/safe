QUnit.module('lodash.random');
(function () {
    var array = Array(__num_top__);
    QUnit.test('should return `0` or `1` when no arguments are given', function (assert) {
        assert.expect(1);
        var actual = lodashStable.uniq(lodashStable.map(array, function () {
            return _.random();
        })).sort();
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should support a `min` and `max`', function (assert) {
        assert.expect(1);
        var min = __num_top__, max = __num_top__;
        assert.ok(lodashStable.some(array, function () {
            var result = _.random(min, max);
            return result >= min && result <= max;
        }));
    });
    QUnit.test('should support not providing a `max`', function (assert) {
        assert.expect(1);
        var min = __num_top__, max = __num_top__;
        assert.ok(lodashStable.some(array, function () {
            var result = _.random(max);
            return result >= min && result <= max;
        }));
    });
    QUnit.test('should swap `min` and `max` when `min` > `max`', function (assert) {
        assert.expect(1);
        var min = __num_top__, max = __num_top__, expected = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        var actual = lodashStable.uniq(lodashStable.map(array, function () {
            return _.random(min, max);
        })).sort();
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should support large integer values', function (assert) {
        assert.expect(2);
        var min = Math.pow(__num_top__, __num_top__), max = Math.pow(__num_top__, __num_top__);
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
            _.random(__str_top__, __str_top__),
            _.random(Infinity, Infinity)
        ];
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            MAX_INTEGER
        ]);
    });
    QUnit.test('should support floats', function (assert) {
        assert.expect(2);
        var min = __num_top__, max = __num_top__, actual = _.random(min, max);
        assert.ok(actual % __num_top__);
        assert.ok(actual >= min && actual <= max);
    });
    QUnit.test('should support providing a `floating`', function (assert) {
        assert.expect(3);
        var actual = _.random(__bool_top__);
        assert.ok(actual % __num_top__ && actual >= __num_top__ && actual <= __num_top__);
        actual = _.random(__num_top__, __bool_top__);
        assert.ok(actual % __num_top__ && actual >= __num_top__ && actual <= __num_top__);
        actual = _.random(__num_top__, __num_top__, __bool_top__);
        assert.ok(actual % __num_top__ && actual >= __num_top__ && actual <= __num_top__);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], expected = lodashStable.map(array, stubTrue), randoms = lodashStable.map(array, _.random);
        var actual = lodashStable.map(randoms, function (result, index) {
            return result >= __num_top__ && result <= array[index] && result % __num_top__ == __num_top__;
        });
        assert.deepEqual(actual, expected);
    });
}());