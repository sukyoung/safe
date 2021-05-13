QUnit.module('lodash.rearg');
(function () {
    function fn() {
        return slice.call(arguments);
    }
    QUnit.test('should reorder arguments provided to `func`', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(rearged(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with repeated indexes', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(rearged(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should use `undefined` for nonexistent indexes', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(rearged(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            undefined,
            __str_top__
        ]);
    });
    QUnit.test('should use `undefined` for non-index values', function (assert) {
        assert.expect(1);
        var values = lodashStable.reject(empties, function (value) {
            return value === __num_top__ || lodashStable.isArray(value);
        }).concat(-__num_top__, __num_top__);
        var expected = lodashStable.map(values, lodashStable.constant([
            undefined,
            __str_top__,
            __str_top__
        ]));
        var actual = lodashStable.map(values, function (value) {
            var rearged = _.rearg(fn, [value]);
            return rearged(__str_top__, __str_top__, __str_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not rearrange arguments when no indexes are given', function (assert) {
        assert.expect(2);
        var rearged = _.rearg(fn);
        assert.deepEqual(rearged(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        rearged = _.rearg(fn, [], []);
        assert.deepEqual(rearged(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should accept multiple index arguments', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, __num_top__, __num_top__, __num_top__);
        assert.deepEqual(rearged(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should accept multiple arrays of indexes', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [__num_top__], [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(rearged(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work with fewer indexes than arguments', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(rearged(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work on functions that have been rearged', function (assert) {
        assert.expect(1);
        var rearged1 = _.rearg(fn, __num_top__, __num_top__, __num_top__), rearged2 = _.rearg(rearged1, __num_top__, __num_top__, __num_top__);
        assert.deepEqual(rearged2(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
}());