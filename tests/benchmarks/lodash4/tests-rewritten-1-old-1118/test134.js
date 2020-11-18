QUnit.module('lodash.join');
(function () {
    var array = [
        'a',
        'b',
        'c'
    ];
    QUnit.test('should return join all array elements into a string', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.join(array, '~'), 'a~b~c');
    });
    QUnit.test('should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _(array);
            assert.strictEqual(wrapped.join('~'), 'a~b~c');
            assert.strictEqual(wrapped.value(), array);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(array).chain().join(__str_top__) instanceof _);
        } else {
            skipAssert(assert);
        }
    });
}());