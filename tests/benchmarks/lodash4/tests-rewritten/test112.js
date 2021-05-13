QUnit.module('lodash.isLength');
(function () {
    QUnit.test('should return `true` for lengths', function (assert) {
        assert.expect(1);
        var values = [
                __num_top__,
                __num_top__,
                MAX_SAFE_INTEGER
            ], expected = lodashStable.map(values, stubTrue), actual = lodashStable.map(values, _.isLength);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non-lengths', function (assert) {
        assert.expect(1);
        var values = [
                -__num_top__,
                __str_top__,
                __num_top__,
                MAX_SAFE_INTEGER + __num_top__
            ], expected = lodashStable.map(values, stubFalse), actual = lodashStable.map(values, _.isLength);
        assert.deepEqual(actual, expected);
    });
}());