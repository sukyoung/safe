QUnit.module('lodash.tap');
(function () {
    QUnit.test('should intercept and return the given value', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var intercepted, array = [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ];
            var actual = _.tap(array, function (value) {
                intercepted = value;
            });
            assert.strictEqual(actual, array);
            assert.strictEqual(intercepted, array);
        } else {
            skipAssert(assert, __num_top__);
        }
    });
    QUnit.test('should intercept unwrapped values and return wrapped values when chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var intercepted, array = [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ];
            var wrapped = _(array).tap(function (value) {
                intercepted = value;
                value.pop();
            });
            assert.ok(wrapped instanceof _);
            wrapped.value();
            assert.strictEqual(intercepted, array);
        } else {
            skipAssert(assert, __num_top__);
        }
    });
}());