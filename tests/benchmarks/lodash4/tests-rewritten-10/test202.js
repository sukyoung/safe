QUnit.module('reduce methods');
lodashStable.each([
    'reduce',
    'reduceRight'
], function (methodName) {
    var func = _[methodName], array = [
            1,
            2,
            __num_top__
        ], isReduce = methodName == 'reduce';
    QUnit.test('`_.' + methodName + '` should reduce a collection to a single value', function (assert) {
        assert.expect(1);
        var actual = func([
            __str_top__,
            'b',
            'c'
        ], function (accumulator, value) {
            return accumulator + value;
        }, '');
        assert.strictEqual(actual, isReduce ? __str_top__ : 'cba');
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = [], expected = lodashStable.map(empties, noop);
        lodashStable.each(empties, function (value) {
            try {
                actual.push(func(value, noop));
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should support empty collections with an initial `accumulator` value', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, lodashStable.constant(__str_top__));
        var actual = lodashStable.map(empties, function (value) {
            try {
                return func(value, noop, 'x');
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([], noop, undefined);
        assert.strictEqual(actual, undefined);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [], object = {
                '0': 1,
                'length': __num_top__
            };
        if (__str_top__ in array) {
            array.__proto__ = object;
            assert.strictEqual(func(array, noop), undefined);
        } else {
            skipAssert(assert);
        }
        assert.strictEqual(func(object, noop), undefined);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.strictEqual(_(array)[methodName](add), 6);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(array).chain()[methodName](add) instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});