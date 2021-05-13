QUnit.module('lodash.isFinite');
(function () {
    QUnit.test('should return `true` for finite values', function (assert) {
        assert.expect(1);
        var values = [
                __num_top__,
                __num_top__,
                __num_top__,
                -__num_top__
            ], expected = lodashStable.map(values, stubTrue), actual = lodashStable.map(values, _.isFinite);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non-finite values', function (assert) {
        assert.expect(1);
        var values = [
                NaN,
                Infinity,
                -Infinity,
                Object(__num_top__)
            ], expected = lodashStable.map(values, stubFalse), actual = lodashStable.map(values, _.isFinite);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non-numeric values', function (assert) {
        assert.expect(10);
        var values = [
                undefined,
                [],
                __bool_top__,
                __str_top__,
                __str_top__,
                __str_top__
            ], expected = lodashStable.map(values, stubFalse), actual = lodashStable.map(values, _.isFinite);
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isFinite(args), __bool_top__);
        assert.strictEqual(_.isFinite([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isFinite(__bool_top__), __bool_top__);
        assert.strictEqual(_.isFinite(new Date()), __bool_top__);
        assert.strictEqual(_.isFinite(new Error()), __bool_top__);
        assert.strictEqual(_.isFinite({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isFinite(/x/), __bool_top__);
        assert.strictEqual(_.isFinite(__str_top__), __bool_top__);
        assert.strictEqual(_.isFinite(symbol), __bool_top__);
    });
    QUnit.test('should return `false` for numeric string values', function (assert) {
        assert.expect(1);
        var values = [
                __str_top__,
                __str_top__,
                __str_top__
            ], expected = lodashStable.map(values, stubFalse), actual = lodashStable.map(values, _.isFinite);
        assert.deepEqual(actual, expected);
    });
}());