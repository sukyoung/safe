QUnit.module('lodash.mixin');
(function () {
    function reset(wrapper) {
        delete wrapper.a;
        delete wrapper.prototype.a;
        delete wrapper.b;
        delete wrapper.prototype.b;
    }
    function Wrapper(value) {
        if (!(this instanceof Wrapper)) {
            return new Wrapper(value);
        }
        if (_.has(value, '__wrapped__')) {
            var actions = slice.call(value.__actions__), chain = value.__chain__;
            value = value.__wrapped__;
        }
        this.__wrapped__ = value;
        this.__actions__ = actions || [];
        this.__chain__ = chain || false;
    }
    Wrapper.prototype.value = function () {
        return getUnwrappedValue(this);
    };
    var array = ['a'], source = {
            'a': function (array) {
                return array[0];
            },
            'b': 'B'
        };
    QUnit.test('should mixin `source` methods into lodash', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            _.mixin(source);
            assert.strictEqual(_.a(array), 'a');
            assert.strictEqual(_(array).a().value(), 'a');
            assert.notOk('b' in _);
            assert.notOk('b' in _.prototype);
            reset(_);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should mixin chaining methods by reference', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            _.mixin(source);
            _.a = stubB;
            assert.strictEqual(_.a(array), 'b');
            assert.strictEqual(_(array).a().value(), 'a');
            reset(_);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should use a default `object` of `this`', function (assert) {
        assert.expect(3);
        var object = lodashStable.create(_);
        object.mixin(source);
        assert.strictEqual(object.a(array), 'a');
        assert.notOk('a' in _);
        assert.notOk('a' in _.prototype);
        reset(_);
    });
    QUnit.test('should accept an `object`', function (assert) {
        assert.expect(1);
        var object = {};
        _.mixin(object, source);
        assert.strictEqual(object.a(array), 'a');
    });
    QUnit.test('should accept a function `object`', function (assert) {
        assert.expect(2);
        _.mixin(Wrapper, source);
        var wrapped = Wrapper(array), actual = wrapped.a();
        assert.strictEqual(actual.value(), 'a');
        assert.ok(actual instanceof Wrapper);
        reset(Wrapper);
    });
    QUnit.test('should return `object`', function (assert) {
        assert.expect(3);
        var object = {};
        assert.strictEqual(_.mixin(object, source), object);
        assert.strictEqual(_.mixin(Wrapper, source), Wrapper);
        assert.strictEqual(_.mixin(), _);
        reset(Wrapper);
    });
    QUnit.test('should not assign inherited `source` methods', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.prototype.a = noop;
        var object = {};
        assert.strictEqual(_.mixin(object, new Foo()), object);
    });
    QUnit.test('should accept an `options`', function (assert) {
        assert.expect(8);
        function message(func, chain) {
            return (func === _ ? 'lodash' : 'given') + ' function should ' + (chain ? '' : 'not ') + __str_top__;
        }
        lodashStable.each([
            _,
            Wrapper
        ], function (func) {
            lodashStable.each([
                { 'chain': false },
                { 'chain': true }
            ], function (options) {
                if (!isNpm) {
                    if (func === _) {
                        _.mixin(source, options);
                    } else {
                        _.mixin(func, source, options);
                    }
                    var wrapped = func(array), actual = wrapped.a();
                    if (options.chain) {
                        assert.strictEqual(actual.value(), 'a', message(func, true));
                        assert.ok(actual instanceof func, message(func, true));
                    } else {
                        assert.strictEqual(actual, 'a', message(func, false));
                        assert.notOk(actual instanceof func, message(func, false));
                    }
                    reset(func);
                } else {
                    skipAssert(assert, 2);
                }
            });
        });
    });
    QUnit.test('should not extend lodash when an `object` is given with an empty `options` object', function (assert) {
        assert.expect(1);
        _.mixin({ 'a': noop }, {});
        assert.notOk('a' in _);
        reset(_);
    });
    QUnit.test('should not error for non-object `options` values', function (assert) {
        assert.expect(2);
        var pass = true;
        try {
            _.mixin({}, source, 1);
        } catch (e) {
            pass = false;
        }
        assert.ok(pass);
        pass = true;
        try {
            _.mixin(source, 1);
        } catch (e) {
            pass = false;
        }
        assert.ok(pass);
        reset(_);
    });
    QUnit.test('should not return the existing wrapped value when chaining', function (assert) {
        assert.expect(2);
        lodashStable.each([
            _,
            Wrapper
        ], function (func) {
            if (!isNpm) {
                if (func === _) {
                    var wrapped = _(source), actual = wrapped.mixin();
                    assert.strictEqual(actual.value(), _);
                } else {
                    wrapped = _(func);
                    actual = wrapped.mixin(source);
                    assert.notStrictEqual(actual, wrapped);
                }
                reset(func);
            } else {
                skipAssert(assert);
            }
        });
    });
    QUnit.test('should produce methods that work in a lazy sequence', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            _.mixin({
                'a': _.countBy,
                'b': _.filter
            });
            var array = lodashStable.range(LARGE_ARRAY_SIZE), actual = _(array).a().map(square).b(isEven).take().value();
            assert.deepEqual(actual, _.take(_.b(_.map(_.a(array), square), isEven)));
            reset(_);
        } else {
            skipAssert(assert);
        }
    });
}());