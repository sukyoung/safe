QUnit.module('lodash(...).value');
(function () {
    QUnit.test('should execute the chained sequence and extract the unwrapped value', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var array = [__num_top__], wrapped = _(array).push(__num_top__).push(__num_top__);
            assert.deepEqual(array, [__num_top__]);
            assert.deepEqual(wrapped.value(), [
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
            assert.deepEqual(array, [
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
    QUnit.test('should return the `valueOf` result of the wrapped value', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _(__num_top__);
            assert.strictEqual(Number(wrapped), __num_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should stringify the wrapped value when used by `JSON.stringify`', function (assert) {
        assert.expect(1);
        if (!isNpm && JSON) {
            var wrapped = _([
                __num_top__,
                __num_top__,
                __num_top__
            ]);
            assert.strictEqual(JSON.stringify(wrapped), __str_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should be aliased', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var expected = _.prototype.value;
            assert.strictEqual(_.prototype.toJSON, expected);
            assert.strictEqual(_.prototype.valueOf, expected);
        } else {
            skipAssert(assert, 2);
        }
    });
}());