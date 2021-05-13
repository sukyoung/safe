QUnit.module('lodash.isPlainObject');
(function () {
    var element = document && document.createElement('div');
    QUnit.test('should detect plain objects', function (assert) {
        assert.expect(5);
        function Foo(a) {
            this.a = 1;
        }
        assert.strictEqual(_.isPlainObject({}), true);
        assert.strictEqual(_.isPlainObject({ 'a': 1 }), true);
        assert.strictEqual(_.isPlainObject({ 'constructor': Foo }), true);
        assert.strictEqual(_.isPlainObject([
            1,
            2,
            3
        ]), false);
        assert.strictEqual(_.isPlainObject(new Foo(1)), false);
    });
    QUnit.test('should return `true` for objects with a `[[Prototype]]` of `null`', function (assert) {
        assert.expect(2);
        var object = create(null);
        assert.strictEqual(_.isPlainObject(object), true);
        object.constructor = objectProto.constructor;
        assert.strictEqual(_.isPlainObject(object), true);
    });
    QUnit.test('should return `true` for objects with a `valueOf` property', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isPlainObject({ 'valueOf': 0 }), true);
    });
    QUnit.test('should return `true` for objects with a writable `Symbol.toStringTag` property', function (assert) {
        assert.expect(1);
        if (Symbol && Symbol.toStringTag) {
            var object = {};
            object[Symbol.toStringTag] = 'X';
            assert.deepEqual(_.isPlainObject(object), true);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for objects with a custom `[[Prototype]]`', function (assert) {
        assert.expect(1);
        var object = create({ 'a': 1 });
        assert.strictEqual(_.isPlainObject(object), false);
    });
    QUnit.test('should return `false` for DOM elements', function (assert) {
        assert.expect(1);
        if (element) {
            assert.strictEqual(_.isPlainObject(element), false);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non-Object objects', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.isPlainObject(arguments), false);
        assert.strictEqual(_.isPlainObject(Error), false);
        assert.strictEqual(_.isPlainObject(Math), false);
    });
    QUnit.test('should return `false` for non-objects', function (assert) {
        assert.expect(4);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isPlainObject(value) : _.isPlainObject();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isPlainObject(true), false);
        assert.strictEqual(_.isPlainObject('a'), false);
        assert.strictEqual(_.isPlainObject(symbol), false);
    });
    QUnit.test('should return `false` for objects with a read-only `Symbol.toStringTag` property', function (assert) {
        assert.expect(1);
        if (Symbol && Symbol.toStringTag) {
            var object = {};
            defineProperty(object, Symbol.toStringTag, {
                'configurable': true,
                'enumerable': false,
                'writable': false,
                'value': 'X'
            });
            assert.deepEqual(_.isPlainObject(object), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should not mutate `value`', function (assert) {
        assert.expect(2);
        if (Symbol && Symbol.toStringTag) {
            var proto = {};
            proto[Symbol.toStringTag] = undefined;
            var object = create(proto);
            assert.strictEqual(_.isPlainObject(object), false);
            assert.notOk(lodashStable.has(object, Symbol.toStringTag));
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should work with objects from another realm', function (assert) {
        assert.expect(1);
        if (realm.object) {
            assert.strictEqual(_.isPlainObject(realm.object), true);
        } else {
            skipAssert(assert);
        }
    });
}());