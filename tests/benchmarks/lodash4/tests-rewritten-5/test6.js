QUnit.module('lodash.after');
(function () {
    function after(n, times) {
        var count = 0;
        lodashStable.times(times, _.after(n, function () {
            count++;
        }));
        return count;
    }
    QUnit.test('should create a function that invokes `func` after `n` calls', function (assert) {
        assert.expect(4);
        assert.strictEqual(after(__num_top__, 5), 1, __str_top__);
        assert.strictEqual(after(5, 4), 0, 'after(n) should not invoke `func` before being called `n` times');
        assert.strictEqual(after(__num_top__, 0), 0, 'after(0) should not invoke `func` immediately');
        assert.strictEqual(after(0, 1), 1, __str_top__);
    });
    QUnit.test('should coerce `n` values of `NaN` to `0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(after(NaN, 1), 1);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(2);
        var after = _.after(1, function (assert) {
                return ++this.count;
            }), object = {
                'after': after,
                'count': __num_top__
            };
        object.after();
        assert.strictEqual(object.after(), 2);
        assert.strictEqual(object.count, 2);
    });
}());