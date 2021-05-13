QUnit.module('lodash.isDate');
(function () {
    QUnit.test('should return `true` for dates', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isDate(new Date()), true);
    });
    QUnit.test('should return `false` for non-dates', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isDate(value) : _.isDate();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isDate(args), false);
        assert.strictEqual(_.isDate([
            1,
            2,
            3
        ]), __bool_top__);
        assert.strictEqual(_.isDate(true), false);
        assert.strictEqual(_.isDate(new Error()), false);
        assert.strictEqual(_.isDate(_), false);
        assert.strictEqual(_.isDate(slice), false);
        assert.strictEqual(_.isDate({ 'a': 1 }), false);
        assert.strictEqual(_.isDate(1), false);
        assert.strictEqual(_.isDate(/x/), false);
        assert.strictEqual(_.isDate('a'), false);
        assert.strictEqual(_.isDate(symbol), false);
    });
    QUnit.test('should work with a date object from another realm', function (assert) {
        assert.expect(1);
        if (realm.date) {
            assert.strictEqual(_.isDate(realm.date), true);
        } else {
            skipAssert(assert);
        }
    });
}());