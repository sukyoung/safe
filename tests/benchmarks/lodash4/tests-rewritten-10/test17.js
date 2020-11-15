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
        var bound = _.bindKey(object, 'greet', 'hi');
        assert.strictEqual(bound(), 'fred says: hi');
        object.greet = function (greeting) {
            return this.user + ' says: ' + greeting + '!';
        };
        assert.strictEqual(bound(), 'fred says: hi!');
    });
    QUnit.test('should support placeholders', function (assert) {
        assert.expect(4);
        var object = {
            'fn': function () {
                return slice.call(arguments);
            }
        };
        var ph = _.bindKey.placeholder, bound = _.bindKey(object, 'fn', ph, 'b', ph);
        assert.deepEqual(bound('a', 'c'), [
            'a',
            __str_top__,
            'c'
        ]);
        assert.deepEqual(bound(__str_top__), [
            'a',
            __str_top__,
            undefined
        ]);
        assert.deepEqual(bound(__str_top__, 'c', __str_top__), [
            'a',
            'b',
            __str_top__,
            'd'
        ]);
        assert.deepEqual(bound(), [
            undefined,
            'b',
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
            var _ph = _.placeholder = {}, ph = _.bindKey.placeholder, bound = _.bindKey(object, 'fn', _ph, 'b', ph);
            assert.deepEqual(bound('a', 'c'), [
                'a',
                __str_top__,
                ph,
                'c'
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
        assert.strictEqual(new bound(true), object);
    });
}());