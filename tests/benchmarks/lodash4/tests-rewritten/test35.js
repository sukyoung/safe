QUnit.module('lodash.curryRight');
(function () {
    function fn(a, b, c, d) {
        return slice.call(arguments);
    }
    QUnit.test('should curry based on the number of arguments given', function (assert) {
        assert.expect(3);
        var curried = _.curryRight(fn), expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ];
        assert.deepEqual(curried(__num_top__)(__num_top__)(__num_top__)(__num_top__), expected);
        assert.deepEqual(curried(__num_top__, __num_top__)(__num_top__, __num_top__), expected);
        assert.deepEqual(curried(__num_top__, __num_top__, __num_top__, __num_top__), expected);
    });
    QUnit.test('should allow specifying `arity`', function (assert) {
        assert.expect(3);
        var curried = _.curryRight(fn, __num_top__), expected = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        assert.deepEqual(curried(__num_top__)(__num_top__, __num_top__), expected);
        assert.deepEqual(curried(__num_top__, __num_top__)(__num_top__), expected);
        assert.deepEqual(curried(__num_top__, __num_top__, __num_top__), expected);
    });
    QUnit.test('should coerce `arity` to an integer', function (assert) {
        assert.expect(2);
        var values = [
                __str_top__,
                __num_top__,
                __str_top__
            ], expected = lodashStable.map(values, stubArray);
        var actual = lodashStable.map(values, function (arity) {
            return _.curryRight(fn, arity)();
        });
        assert.deepEqual(actual, expected);
        assert.deepEqual(_.curryRight(fn, __str_top__)(__num_top__)(__num_top__), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should support placeholders', function (assert) {
        assert.expect(4);
        var curried = _.curryRight(fn), expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ], ph = curried.placeholder;
        assert.deepEqual(curried(__num_top__)(__num_top__, ph)(__num_top__, ph)(__num_top__), expected);
        assert.deepEqual(curried(__num_top__, ph)(__num_top__)(__num_top__, ph)(__num_top__), expected);
        assert.deepEqual(curried(ph, ph, __num_top__)(ph, __num_top__)(ph, __num_top__)(__num_top__), expected);
        assert.deepEqual(curried(ph, ph, ph, __num_top__)(ph, ph, __num_top__)(ph, __num_top__)(__num_top__), expected);
    });
    QUnit.test('should persist placeholders', function (assert) {
        assert.expect(1);
        var curried = _.curryRight(fn), ph = curried.placeholder, actual = curried(__str_top__, ph, ph, ph)(__str_top__)(ph)(__str_top__)(__str_top__);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should use `_.placeholder` when set', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var curried = _.curryRight(fn), _ph = _.placeholder = {}, ph = curried.placeholder;
            assert.deepEqual(curried(__num_top__)(__num_top__, _ph)(__num_top__, ph), [
                __num_top__,
                __num_top__,
                ph,
                __num_top__
            ]);
            delete _.placeholder;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should provide additional arguments after reaching the target arity', function (assert) {
        assert.expect(3);
        var curried = _.curryRight(fn, __num_top__);
        assert.deepEqual(curried(__num_top__)(__num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(curried(__num_top__, __num_top__)(__num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(curried(__num_top__, __num_top__, __num_top__, __num_top__, __num_top__, __num_top__), [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should create a function with a `length` of `0`', function (assert) {
        assert.expect(6);
        lodashStable.times(__num_top__, function (index) {
            var curried = index ? _.curryRight(fn, __num_top__) : _.curryRight(fn);
            assert.strictEqual(curried.length, __num_top__);
            assert.strictEqual(curried(__num_top__).length, __num_top__);
            assert.strictEqual(curried(__num_top__, __num_top__).length, __num_top__);
        });
    });
    QUnit.test('should ensure `new curried` is an instance of `func`', function (assert) {
        assert.expect(2);
        function Foo(value) {
            return value && object;
        }
        var curried = _.curryRight(Foo), object = {};
        assert.ok(new curried(__bool_top__) instanceof Foo);
        assert.strictEqual(new curried(__bool_top__), object);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(9);
        var fn = function (a, b, c) {
            var value = this || {};
            return [
                value[a],
                value[b],
                value[c]
            ];
        };
        var object = {
                'a': __num_top__,
                'b': __num_top__,
                'c': __num_top__
            }, expected = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        assert.deepEqual(_.curryRight(_.bind(fn, object), __num_top__)(__str_top__)(__str_top__)(__str_top__), expected);
        assert.deepEqual(_.curryRight(_.bind(fn, object), __num_top__)(__str_top__, __str_top__)(__str_top__), expected);
        assert.deepEqual(_.curryRight(_.bind(fn, object), __num_top__)(__str_top__, __str_top__, __str_top__), expected);
        assert.deepEqual(_.bind(_.curryRight(fn), object)(__str_top__)(__str_top__)(__str_top__), Array(__num_top__));
        assert.deepEqual(_.bind(_.curryRight(fn), object)(__str_top__, __str_top__)(__str_top__), Array(__num_top__));
        assert.deepEqual(_.bind(_.curryRight(fn), object)(__str_top__, __str_top__, __str_top__), expected);
        object.curried = _.curryRight(fn);
        assert.deepEqual(object.curried(__str_top__)(__str_top__)(__str_top__), Array(__num_top__));
        assert.deepEqual(object.curried(__str_top__, __str_top__)(__str_top__), Array(__num_top__));
        assert.deepEqual(object.curried(__str_top__, __str_top__, __str_top__), expected);
    });
    QUnit.test('should work with partialed methods', function (assert) {
        assert.expect(2);
        var curried = _.curryRight(fn), expected = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ];
        var a = _.partialRight(curried, __num_top__), b = _.partialRight(a, __num_top__), c = _.bind(b, null, __num_top__), d = _.partial(b(__num_top__), __num_top__);
        assert.deepEqual(c(__num_top__), expected);
        assert.deepEqual(d(), expected);
    });
}());