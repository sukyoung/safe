QUnit.module('lodash.isFinite');
(function () {
    QUnit.test('should return `true` for finite values', function (assert) {
        assert.expect(1);
        var values = [
                0,
                __num_top__,
                3.14,
                -1
            ], expected = lodashStable.map(values, stubTrue), actual = lodashStable.map(values, _.isFinite);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non-finite values', function (assert) {
        assert.expect(1);
        var values = [
                NaN,
                Infinity,
                -Infinity,
                Object(1)
            ], expected = lodashStable.map(values, stubFalse), actual = lodashStable.map(values, _.isFinite);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non-numeric values', function (assert) {
        assert.expect(10);
        var values = [
                undefined,
                [],
                __bool_top__,
                '',
                __str_top__,
                __str_top__
            ], expected = lodashStable.map(values, stubFalse), actual = lodashStable.map(values, _.isFinite);
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isFinite(args), false);
        assert.strictEqual(_.isFinite([
            1,
            2,
            3
        ]), __bool_top__);
        assert.strictEqual(_.isFinite(__bool_top__), false);
        assert.strictEqual(_.isFinite(new Date()), __bool_top__);
        assert.strictEqual(_.isFinite(new Error()), false);
        assert.strictEqual(_.isFinite({ 'a': 1 }), __bool_top__);
        assert.strictEqual(_.isFinite(/x/), false);
        assert.strictEqual(_.isFinite('a'), false);
        assert.strictEqual(_.isFinite(symbol), false);
    });
    QUnit.test('should return `false` for numeric string values', function (assert) {
        assert.expect(1);
        var values = [
                '2',
                '0',
                __str_top__
            ], expected = lodashStable.map(values, stubFalse), actual = lodashStable.map(values, _.isFinite);
        assert.deepEqual(actual, expected);
    });
}());