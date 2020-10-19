QUnit.module('lodash.isDate');
(function () {
    QUnit.test('should return `true` for dates', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isDate(new Date()), __bool_top__);
    });
    QUnit.test('should return `false` for non-dates', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isDate(value) : _.isDate();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isDate(args), __bool_top__);
        assert.strictEqual(_.isDate([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isDate(__bool_top__), __bool_top__);
        assert.strictEqual(_.isDate(new Error()), __bool_top__);
        assert.strictEqual(_.isDate(_), __bool_top__);
        assert.strictEqual(_.isDate(slice), __bool_top__);
        assert.strictEqual(_.isDate({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isDate(__num_top__), __bool_top__);
        assert.strictEqual(_.isDate(/x/), __bool_top__);
        assert.strictEqual(_.isDate(__str_top__), __bool_top__);
        assert.strictEqual(_.isDate(symbol), __bool_top__);
    });
    QUnit.test('should work with a date object from another realm', function (assert) {
        assert.expect(1);
        if (realm.date) {
            assert.strictEqual(_.isDate(realm.date), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());