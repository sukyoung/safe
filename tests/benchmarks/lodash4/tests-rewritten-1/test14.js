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
        assert.strictEqual(before(5, 4), 4, 'before(n) should invoke `func` before being called `n` times');
        assert.strictEqual(before(5, 6), 4, 'before(n) should not invoke `func` after being called `n - 1` times');
        assert.strictEqual(before(0, 0), 0, 'before(0) should not invoke `func` immediately');
        assert.strictEqual(before(0, 1), 0, 'before(0) should not invoke `func` when called');
    });
    QUnit.test('should coerce `n` values of `NaN` to `0`', function (assert) {
        assert.expect(1);
        assert.strictEqual(before(NaN, __num_top__), 0);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(2);
        var before = _.before(2, function (assert) {
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