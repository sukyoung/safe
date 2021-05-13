QUnit.module('lodash(...).commit');
(function () {
    QUnit.test('should execute the chained sequence and returns the wrapped result', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var array = [__num_top__], wrapped = _(array).push(__num_top__).push(__num_top__);
            assert.deepEqual(array, [__num_top__]);
            var otherWrapper = wrapped.commit();
            assert.ok(otherWrapper instanceof _);
            assert.deepEqual(otherWrapper.value(), [
                __num_top__,
                __num_top__,
                __num_top__
            ]);
            assert.deepEqual(wrapped.value(), [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
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
            assert.strictEqual(wrapped.value(), __num_top__);
        } else {
            skipAssert(assert, 2);
        }
    });
}());