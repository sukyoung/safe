QUnit.module('lodash.isError');
(function () {
    QUnit.test('should return `true` for error objects', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(errors, stubTrue);
        var actual = lodashStable.map(errors, function (error) {
            return _.isError(error) === true;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `true` for subclassed values', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isError(new CustomError('x')), true);
    });
    QUnit.test('should return `false` for non error objects', function (assert) {
        assert.expect(12);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isError(value) : _.isError();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isError(args), __bool_top__);
        assert.strictEqual(_.isError([
            __num_top__,
            2,
            3
        ]), __bool_top__);
        assert.strictEqual(_.isError(true), false);
        assert.strictEqual(_.isError(new Date()), false);
        assert.strictEqual(_.isError(_), __bool_top__);
        assert.strictEqual(_.isError(slice), false);
        assert.strictEqual(_.isError({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isError(1), false);
        assert.strictEqual(_.isError(/x/), __bool_top__);
        assert.strictEqual(_.isError('a'), false);
        assert.strictEqual(_.isError(symbol), false);
    });
    QUnit.test('should return `false` for plain objects', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isError({
            'name': __str_top__,
            'message': ''
        }), __bool_top__);
    });
    QUnit.test('should work with an error object from another realm', function (assert) {
        assert.expect(1);
        if (realm.errors) {
            var expected = lodashStable.map(realm.errors, stubTrue);
            var actual = lodashStable.map(realm.errors, function (error) {
                return _.isError(error) === true;
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
}());