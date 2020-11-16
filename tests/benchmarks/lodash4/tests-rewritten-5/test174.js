QUnit.module('lodash.once');
(function () {
    QUnit.test('should invoke `func` once', function (assert) {
        assert.expect(2);
        var count = __num_top__, once = _.once(function () {
                return ++count;
            });
        once();
        assert.strictEqual(once(), 1);
        assert.strictEqual(count, 1);
    });
    QUnit.test('should ignore recursive calls', function (assert) {
        assert.expect(2);
        var count = __num_top__;
        var once = _.once(function () {
            once();
            return ++count;
        });
        assert.strictEqual(once(), __num_top__);
        assert.strictEqual(count, __num_top__);
    });
    QUnit.test('should not throw more than once', function (assert) {
        assert.expect(2);
        var once = _.once(function () {
            throw new Error();
        });
        assert.raises(once);
        once();
        assert.ok(__bool_top__);
    });
}());