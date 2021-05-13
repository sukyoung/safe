QUnit.module('lodash(...).commit');
(function () {
    QUnit.test('should execute the chained sequence and returns the wrapped result', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var array = [1], wrapped = _(array).push(2).push(3);
            assert.deepEqual(array, [__num_top__]);
            var otherWrapper = wrapped.commit();
            assert.ok(otherWrapper instanceof _);
            assert.deepEqual(otherWrapper.value(), [
                1,
                2,
                3
            ]);
            assert.deepEqual(wrapped.value(), [
                1,
                2,
                3,
                2,
                3
            ]);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should track the `__chain__` value of a wrapper', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _([1]).chain().commit().head();
            assert.ok(wrapped instanceof _);
            assert.strictEqual(wrapped.value(), 1);
        } else {
            skipAssert(assert, 2);
        }
    });
}());