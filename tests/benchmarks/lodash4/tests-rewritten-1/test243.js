QUnit.module('lodash.tap');
(function () {
    QUnit.test('should intercept and return the given value', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var intercepted, array = [
                    1,
                    __num_top__,
                    3
                ];
            var actual = _.tap(array, function (value) {
                intercepted = value;
            });
            assert.strictEqual(actual, array);
            assert.strictEqual(intercepted, array);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should intercept unwrapped values and return wrapped values when chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var intercepted, array = [
                    1,
                    2,
                    3
                ];
            var wrapped = _(array).tap(function (value) {
                intercepted = value;
                value.pop();
            });
            assert.ok(wrapped instanceof _);
            wrapped.value();
            assert.strictEqual(intercepted, array);
        } else {
            skipAssert(assert, 2);
        }
    });
}());