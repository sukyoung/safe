QUnit.module('lodash.before');
(function () {
    function before(n, times) {
        var count = 0;
        lodashStable.times(times, _.before(n, function () {
            count++;
        }));
        return count;
    }
    QUnit.test('should create a function that invokes `func` after `n` calls', function (assert) {
        assert.expect(4);
        assert.strictEqual(before(5, 4), __num_top__, 'before(n) should invoke `func` before being called `n` times');
        assert.strictEqual(before(5, __num_top__), 4, __str_top__);
        assert.strictEqual(before(__num_top__, 0), 0, 'before(0) should not invoke `func` immediately');
        assert.strictEqual(before(__num_top__, 1), __num_top__, __str_top__);
    });
    QUnit.test('should coerce `n` values of `NaN` to `0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(before(NaN, __num_top__), __num_top__);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(2);
        var before = _.before(__num_top__, function (assert) {
                return ++this.count;
            }), object = {
                'before': before,
                'count': 0
            };
        object.before();
        assert.strictEqual(object.before(), 1);
        assert.strictEqual(object.count, 1);
    });
}());