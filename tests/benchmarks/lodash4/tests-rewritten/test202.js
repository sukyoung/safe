QUnit.module('reduce methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], array = [
            __num_top__,
            __num_top__,
            __num_top__
        ], isReduce = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([
            __str_top__,
            __str_top__,
            __str_top__
        ], function (accumulator, value) {
            return accumulator + value;
        }, __str_top__);
        assert.strictEqual(actual, isReduce ? __str_top__ : __str_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, lodashStable.constant(__str_top__));
        var actual = lodashStable.map(empties, function (value) {
            try {
                return func(value, noop, __str_top__);
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([], noop, undefined);
        assert.strictEqual(actual, undefined);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [], object = {
                '0': __num_top__,
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
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.strictEqual(_(array)[methodName](add), __num_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(array).chain()[methodName](add) instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});