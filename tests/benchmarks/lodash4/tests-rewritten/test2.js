QUnit.module('isIterateeCall');
(function () {
    var array = [__num_top__], func = _._isIterateeCall, object = { 'a': __num_top__ };
    QUnit.test('should return `true` for iteratee calls', function (assert) {
        assert.expect(3);
        function Foo() {
        }
        Foo.prototype.a = __num_top__;
        if (func) {
            assert.strictEqual(func(__num_top__, __num_top__, array), __bool_top__);
            assert.strictEqual(func(__num_top__, __str_top__, object), __bool_top__);
            assert.strictEqual(func(__num_top__, __str_top__, new Foo()), __bool_top__);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should return `false` for non-iteratee calls', function (assert) {
        assert.expect(4);
        if (func) {
            assert.strictEqual(func(__num_top__, __num_top__, array), __bool_top__);
            assert.strictEqual(func(__num_top__, __num_top__, array), __bool_top__);
            assert.strictEqual(func(__num_top__, __num_top__, { 'length': MAX_SAFE_INTEGER + __num_top__ }), __bool_top__);
            assert.strictEqual(func(__num_top__, __str_top__, object), __bool_top__);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should work with `NaN` values', function (assert) {
        assert.expect(2);
        if (func) {
            assert.strictEqual(func(NaN, __num_top__, [NaN]), __bool_top__);
            assert.strictEqual(func(NaN, __str_top__, { 'a': NaN }), __bool_top__);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should not error when `index` is an object without a `toString` method', function (assert) {
        assert.expect(1);
        if (func) {
            try {
                var actual = func(__num_top__, { 'toString': null }, [__num_top__]);
            } catch (e) {
                var message = e.message;
            }
            assert.strictEqual(actual, __bool_top__, message || __str_top__);
        } else {
            skipAssert(assert);
        }
    });
}());