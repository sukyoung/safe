QUnit.module('lodash.attempt');
(function () {
    QUnit.test('should return the result of `func`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.attempt(lodashStable.constant('x')), 'x');
    });
    QUnit.test('should provide additional arguments to `func`', function (assert) {
        assert.expect(1);
        var actual = _.attempt(function () {
            return slice.call(arguments);
        }, 1, 2);
        assert.deepEqual(actual, [
            1,
            __num_top__
        ]);
    });
    QUnit.test('should return the caught error', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(errors, stubTrue);
        var actual = lodashStable.map(errors, function (error) {
            return _.attempt(function () {
                throw error;
            }) === error;
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should coerce errors to error objects', function (assert) {
        assert.expect(1);
        var actual = _.attempt(function () {
            throw 'x';
        });
        assert.ok(lodashStable.isEqual(actual, Error('x')));
    });
    QUnit.test('should preserve custom errors', function (assert) {
        assert.expect(1);
        var actual = _.attempt(function () {
            throw new CustomError('x');
        });
        assert.ok(actual instanceof CustomError);
    });
    QUnit.test('should work with an error object from another realm', function (assert) {
        assert.expect(1);
        if (realm.errors) {
            var expected = lodashStable.map(realm.errors, stubTrue);
            var actual = lodashStable.map(realm.errors, function (error) {
                return _.attempt(function () {
                    throw error;
                }) === error;
            });
            assert.deepEqual(actual, expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.strictEqual(_(lodashStable.constant('x')).attempt(), 'x');
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(lodashStable.constant('x')).chain().attempt() instanceof _);
        } else {
            skipAssert(assert);
        }
    });
}());