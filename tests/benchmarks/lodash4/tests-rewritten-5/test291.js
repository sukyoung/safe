QUnit.module('lodash(...).commit');
(function () {
    QUnit.test('should execute the chained sequence and returns the wrapped result', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var array = [1], wrapped = _(array).push(2).push(__num_top__);
            assert.deepEqual(array, [1]);
            var otherWrapper = wrapped.commit();
            assert.ok(otherWrapper instanceof _);
            assert.deepEqual(otherWrapper.value(), [
                1,
                2,
                3
            ]);
            assert.deepEqual(wrapped.value(), [
                __num_top__,
                2,
                __num_top__,
                __num_top__,
                3
            ]);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should track the `__chain__` value of a wrapper', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _([__num_top__]).chain().commit().head();
            assert.ok(wrapped instanceof _);
            assert.strictEqual(wrapped.value(), 1);
        } else {
            skipAssert(assert, 2);
        }
    });
}());