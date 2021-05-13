QUnit.module('lodash.rearg');
(function () {
    function fn() {
        return slice.call(arguments);
    }
    QUnit.test('should reorder arguments provided to `func`', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [
            2,
            0,
            1
        ]);
        assert.deepEqual(rearged('b', 'c', 'a'), [
            'a',
            'b',
            'c'
        ]);
    });
    QUnit.test('should work with repeated indexes', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [
            __num_top__,
            1,
            1
        ]);
        assert.deepEqual(rearged('c', 'a', 'b'), [
            'a',
            'a',
            'a'
        ]);
    });
    QUnit.test('should use `undefined` for nonexistent indexes', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [
            1,
            __num_top__
        ]);
        assert.deepEqual(rearged('b', 'a', 'c'), [
            'a',
            undefined,
            'c'
        ]);
    });
    QUnit.test('should use `undefined` for non-index values', function (assert) {
        assert.expect(1);
        var values = lodashStable.reject(empties, function (value) {
            return value === 0 || lodashStable.isArray(value);
        }).concat(-1, 1.1);
        var expected = lodashStable.map(values, lodashStable.constant([
            undefined,
            'b',
            __str_top__
        ]));
        var actual = lodashStable.map(values, function (value) {
            var rearged = _.rearg(fn, [value]);
            return rearged('a', 'b', 'c');
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not rearrange arguments when no indexes are given', function (assert) {
        assert.expect(2);
        var rearged = _.rearg(fn);
        assert.deepEqual(rearged('a', 'b', 'c'), [
            'a',
            __str_top__,
            'c'
        ]);
        rearged = _.rearg(fn, [], []);
        assert.deepEqual(rearged('a', 'b', 'c'), [
            'a',
            'b',
            'c'
        ]);
    });
    QUnit.test('should accept multiple index arguments', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, 2, 0, 1);
        assert.deepEqual(rearged('b', 'c', __str_top__), [
            'a',
            'b',
            __str_top__
        ]);
    });
    QUnit.test('should accept multiple arrays of indexes', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [2], [
            0,
            1
        ]);
        assert.deepEqual(rearged('b', 'c', 'a'), [
            __str_top__,
            'b',
            'c'
        ]);
    });
    QUnit.test('should work with fewer indexes than arguments', function (assert) {
        assert.expect(1);
        var rearged = _.rearg(fn, [
            1,
            0
        ]);
        assert.deepEqual(rearged(__str_top__, 'a', 'c'), [
            'a',
            'b',
            'c'
        ]);
    });
    QUnit.test('should work on functions that have been rearged', function (assert) {
        assert.expect(1);
        var rearged1 = _.rearg(fn, 2, 1, 0), rearged2 = _.rearg(rearged1, __num_top__, 0, 2);
        assert.deepEqual(rearged2('b', 'c', 'a'), [
            __str_top__,
            'b',
            'c'
        ]);
    });
}());