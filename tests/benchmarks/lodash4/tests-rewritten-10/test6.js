QUnit.module('lodash.after');
(function () {
    function after(n, times) {
        var count = __num_top__;
        lodashStable.times(times, _.after(n, function () {
            count++;
        }));
        return count;
    }
    QUnit.test('should create a function that invokes `func` after `n` calls', function (assert) {
        assert.expect(4);
        assert.strictEqual(after(5, 5), 1, __str_top__);
        assert.strictEqual(after(__num_top__, 4), 0, 'after(n) should not invoke `func` before being called `n` times');
        assert.strictEqual(after(0, __num_top__), 0, __str_top__);
        assert.strictEqual(after(__num_top__, __num_top__), 1, 'after(0) should invoke `func` when called once');
    });
    QUnit.test('should coerce `n` values of `NaN` to `0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(after(NaN, 1), 1);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(2);
        var after = _.after(__num_top__, function (assert) {
                return ++this.count;
            }), object = {
                'after': after,
                'count': 0
            };
        object.after();
        assert.strictEqual(object.after(), __num_top__);
        assert.strictEqual(object.count, __num_top__);
    });
}());