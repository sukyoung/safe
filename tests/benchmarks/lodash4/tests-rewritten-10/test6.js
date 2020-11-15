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
        assert.strictEqual(after(__num_top__, 5), __num_top__, __str_top__);
        assert.strictEqual(after(5, 4), 0, __str_top__);
        assert.strictEqual(after(0, __num_top__), 0, __str_top__);
        assert.strictEqual(after(0, 1), 1, __str_top__);
    });
    QUnit.test('should coerce `n` values of `NaN` to `0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(after(NaN, __num_top__), __num_top__);
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
        assert.strictEqual(object.after(), 2);
        assert.strictEqual(object.count, 2);
    });
}());