QUnit.module('lodash.isPlainObject');
(function () {
    var element = document && document.createElement(__str_top__);
    QUnit.test('should detect plain objects', function (assert) {
        assert.expect(5);
        function Foo(a) {
            this.a = __num_top__;
        }
        assert.strictEqual(_.isPlainObject({}), __bool_top__);
        assert.strictEqual(_.isPlainObject({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(_.isPlainObject({ 'constructor': Foo }), __bool_top__);
        assert.strictEqual(_.isPlainObject([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(_.isPlainObject(new Foo(__num_top__)), __bool_top__);
    });
    QUnit.test('should return `true` for objects with a `[[Prototype]]` of `null`', function (assert) {
        assert.expect(2);
        var object = create(null);
        assert.strictEqual(_.isPlainObject(object), __bool_top__);
        object.constructor = objectProto.constructor;
        assert.strictEqual(_.isPlainObject(object), __bool_top__);
    });
    QUnit.test('should return `true` for objects with a `valueOf` property', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isPlainObject({ 'valueOf': __num_top__ }), __bool_top__);
    });
    QUnit.test('should return `true` for objects with a writable `Symbol.toStringTag` property', function (assert) {
        assert.expect(1);
        if (Symbol && Symbol.toStringTag) {
            var object = {};
            object[Symbol.toStringTag] = __str_top__;
            assert.deepEqual(_.isPlainObject(object), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for objects with a custom `[[Prototype]]`', function (assert) {
        assert.expect(1);
        var object = create({ 'a': __num_top__ });
        assert.strictEqual(_.isPlainObject(object), __bool_top__);
    });
    QUnit.test('should return `false` for DOM elements', function (assert) {
        assert.expect(1);
        if (element) {
            assert.strictEqual(_.isPlainObject(element), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return `false` for non-Object objects', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.isPlainObject(arguments), __bool_top__);
        assert.strictEqual(_.isPlainObject(Error), __bool_top__);
        assert.strictEqual(_.isPlainObject(Math), __bool_top__);
    });
    QUnit.test('should return `false` for non-objects', function (assert) {
        assert.expect(4);
        var expected = lodashStable.map(falsey, stubFalse);
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? _.isPlainObject(value) : _.isPlainObject();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isPlainObject(__bool_top__), __bool_top__);
        assert.strictEqual(_.isPlainObject(__str_top__), __bool_top__);
        assert.strictEqual(_.isPlainObject(symbol), __bool_top__);
    });
    QUnit.test('should return `false` for objects with a read-only `Symbol.toStringTag` property', function (assert) {
        assert.expect(1);
        if (Symbol && Symbol.toStringTag) {
            var object = {};
            defineProperty(object, Symbol.toStringTag, {
                'configurable': __bool_top__,
                'enumerable': __bool_top__,
                'writable': __bool_top__,
                'value': __str_top__
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
            assert.strictEqual(_.isPlainObject(object), __bool_top__);
            assert.notOk(lodashStable.has(object, Symbol.toStringTag));
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should work with objects from another realm', function (assert) {
        assert.expect(1);
        if (realm.object) {
            assert.strictEqual(_.isPlainObject(realm.object), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());