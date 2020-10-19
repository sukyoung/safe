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
        assert.strictEqual(before(__num_top__, __num_top__), __num_top__, __str_top__);
        assert.strictEqual(before(__num_top__, __num_top__), __num_top__, __str_top__);
        assert.strictEqual(before(__num_top__, __num_top__), __num_top__, __str_top__);
        assert.strictEqual(before(__num_top__, __num_top__), __num_top__, __str_top__);
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
                'count': __num_top__
            };
        object.before();
        assert.strictEqual(object.before(), __num_top__);
        assert.strictEqual(object.count, __num_top__);
    });
}());