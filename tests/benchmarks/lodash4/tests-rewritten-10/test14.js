QUnit.module('lodash.before');
(function () {
    function before(n, times) {
        var count = __num_top__;
        lodashStable.times(times, _.before(n, function () {
            count++;
        }));
        return count;
    }
    QUnit.test('should create a function that invokes `func` after `n` calls', function (assert) {
        assert.expect(4);
        assert.strictEqual(before(5, 4), 4, __str_top__);
        assert.strictEqual(before(__num_top__, __num_top__), 4, 'before(n) should not invoke `func` after being called `n - 1` times');
        assert.strictEqual(before(0, __num_top__), 0, __str_top__);
        assert.strictEqual(before(0, __num_top__), __num_top__, 'before(0) should not invoke `func` when called');
    });
    QUnit.test('should coerce `n` values of `NaN` to `0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(before(NaN, 1), 0);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(2);
        var before = _.before(2, function (assert) {
                return ++this.count;
            }), object = {
                'before': before,
                'count': __num_top__
            };
        object.before();
        assert.strictEqual(object.before(), __num_top__);
        assert.strictEqual(object.count, 1);
    });
}());