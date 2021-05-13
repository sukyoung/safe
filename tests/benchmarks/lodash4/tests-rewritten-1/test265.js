QUnit.module('lodash.unary');
(function () {
    function fn() {
        return slice.call(arguments);
    }
    QUnit.test('should cap the number of arguments provided to `func`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([
            '6',
            '8',
            __str_top__
        ], _.unary(parseInt));
        assert.deepEqual(actual, [
            6,
            8,
            10
        ]);
    });
    QUnit.test('should not force a minimum argument count', function (assert) {
        assert.expect(1);
        var capped = _.unary(fn);
        assert.deepEqual(capped(), []);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(1);
        var capped = _.unary(function (a, b) {
                return this;
            }), object = { 'capped': capped };
        assert.strictEqual(object.capped(), object);
    });
}());