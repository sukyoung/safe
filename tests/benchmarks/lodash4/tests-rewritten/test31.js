QUnit.module('lodash.constant');
(function () {
    QUnit.test('should create a function that returns `value`', function (assert) {
        assert.expect(1);
        var object = { 'a': __num_top__ }, values = Array(__num_top__).concat(empties, __bool_top__, __num_top__, __str_top__), constant = _.constant(object);
        var results = lodashStable.map(values, function (value, index) {
            if (index < __num_top__) {
                return index ? constant.call({}) : constant();
            }
            return constant(value);
        });
        assert.ok(lodashStable.every(results, function (result) {
            return result === object;
        }));
    });
    QUnit.test('should work with falsey values', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubTrue);
        var actual = lodashStable.map(falsey, function (value, index) {
            var constant = index ? _.constant(value) : _.constant(), result = constant();
            return result === value || result !== result && value !== value;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return a wrapped value when chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _(__bool_top__).constant();
            assert.ok(wrapped instanceof _);
        } else {
            skipAssert(assert);
        }
    });
}());