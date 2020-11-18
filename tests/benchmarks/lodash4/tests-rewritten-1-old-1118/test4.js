QUnit.module('lodash constructor');
(function () {
    var values = empties.concat(true, __num_top__, 'a'), expected = lodashStable.map(values, stubTrue);
    QUnit.test('should create a new instance when called without the `new` operator', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = lodashStable.map(values, function (value) {
                return _(value) instanceof _;
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return the given `lodash` instances', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = lodashStable.map(values, function (value) {
                var wrapped = _(value);
                return _(wrapped) === wrapped;
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should convert foreign wrapped values to `lodash` instances', function (assert) {
        assert.expect(1);
        if (!isNpm && lodashBizarro) {
            var actual = lodashStable.map(values, function (value) {
                var wrapped = _(lodashBizarro(value)), unwrapped = wrapped.value();
                return wrapped instanceof _ && (unwrapped === value || unwrapped !== unwrapped && value !== value);
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
}());