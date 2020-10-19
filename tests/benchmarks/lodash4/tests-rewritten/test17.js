QUnit.module('lodash.bindKey');
(function () {
    QUnit.test('should work when the target function is overwritten', function (assert) {
        assert.expect(2);
        var object = {
            'user': __str_top__,
            'greet': function (greeting) {
                return this.user + __str_top__ + greeting;
            }
        };
        var bound = _.bindKey(object, __str_top__, __str_top__);
        assert.strictEqual(bound(), __str_top__);
        object.greet = function (greeting) {
            return this.user + __str_top__ + greeting + __str_top__;
        };
        assert.strictEqual(bound(), __str_top__);
    });
    QUnit.test('should support placeholders', function (assert) {
        assert.expect(4);
        var object = {
            'fn': function () {
                return slice.call(arguments);
            }
        };
        var ph = _.bindKey.placeholder, bound = _.bindKey(object, __str_top__, ph, __str_top__, ph);
        assert.deepEqual(bound(__str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(bound(__str_top__), [
            __str_top__,
            __str_top__,
            undefined
        ]);
        assert.deepEqual(bound(__str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(bound(), [
            undefined,
            __str_top__,
            undefined
        ]);
    });
    QUnit.test('should use `_.placeholder` when set', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var object = {
                'fn': function () {
                    return slice.call(arguments);
                }
            };
            var _ph = _.placeholder = {}, ph = _.bindKey.placeholder, bound = _.bindKey(object, __str_top__, _ph, __str_top__, ph);
            assert.deepEqual(bound(__str_top__, __str_top__), [
                __str_top__,
                __str_top__,
                ph,
                __str_top__
            ]);
            delete _.placeholder;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should ensure `new bound` is an instance of `object[key]`', function (assert) {
        assert.expect(2);
        function Foo(value) {
            return value && object;
        }
        var object = { 'Foo': Foo }, bound = _.bindKey(object, __str_top__);
        assert.ok(new bound() instanceof Foo);
        assert.strictEqual(new bound(__bool_top__), object);
    });
}());