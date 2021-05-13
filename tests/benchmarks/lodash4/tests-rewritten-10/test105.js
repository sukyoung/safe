QUnit.module('lodash.isEmpty');
(function () {
    QUnit.test('should return `true` for empty values', function (assert) {
        assert.expect(10);
        var expected = lodashStable.map(empties, stubTrue), actual = lodashStable.map(empties, _.isEmpty);
        assert.deepEqual(actual, expected);
        assert.strictEqual(_.isEmpty(__bool_top__), true);
        assert.strictEqual(_.isEmpty(slice), true);
        assert.strictEqual(_.isEmpty(1), true);
        assert.strictEqual(_.isEmpty(NaN), true);
        assert.strictEqual(_.isEmpty(/x/), true);
        assert.strictEqual(_.isEmpty(symbol), __bool_top__);
        assert.strictEqual(_.isEmpty(), true);
        if (Buffer) {
            assert.strictEqual(_.isEmpty(new Buffer(0)), true);
            assert.strictEqual(_.isEmpty(new Buffer(__num_top__)), false);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should return `false` for non-empty values', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.isEmpty([__num_top__]), false);
        assert.strictEqual(_.isEmpty({ 'a': 0 }), __bool_top__);
        assert.strictEqual(_.isEmpty('a'), __bool_top__);
    });
    QUnit.test('should work with an object that has a `length` property', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isEmpty({ 'length': 0 }), false);
    });
    QUnit.test('should work with `arguments` objects', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isEmpty(args), false);
    });
    QUnit.test('should work with prototytpe objects', function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.prototype = { 'constructor': Foo };
        assert.strictEqual(_.isEmpty(Foo.prototype), true);
        Foo.prototype.a = 1;
        assert.strictEqual(_.isEmpty(Foo.prototype), false);
    });
    QUnit.test('should work with jQuery/MooTools DOM query collections', function (assert) {
        assert.expect(1);
        function Foo(elements) {
            push.apply(this, elements);
        }
        Foo.prototype = {
            'length': __num_top__,
            'splice': arrayProto.splice
        };
        assert.strictEqual(_.isEmpty(new Foo([])), __bool_top__);
    });
    QUnit.test('should work with maps', function (assert) {
        assert.expect(4);
        if (Map) {
            lodashStable.each([
                new Map(),
                realm.map
            ], function (map) {
                assert.strictEqual(_.isEmpty(map), true);
                map.set('a', 1);
                assert.strictEqual(_.isEmpty(map), false);
                map.clear();
            });
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should work with sets', function (assert) {
        assert.expect(4);
        if (Set) {
            lodashStable.each([
                new Set(),
                realm.set
            ], function (set) {
                assert.strictEqual(_.isEmpty(set), true);
                set.add(1);
                assert.strictEqual(_.isEmpty(set), false);
                set.clear();
            });
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should not treat objects with negative lengths as array-like', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.prototype.length = -__num_top__;
        assert.strictEqual(_.isEmpty(new Foo()), true);
    });
    QUnit.test('should not treat objects with lengths larger than `MAX_SAFE_INTEGER` as array-like', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.prototype.length = MAX_SAFE_INTEGER + __num_top__;
        assert.strictEqual(_.isEmpty(new Foo()), true);
    });
    QUnit.test('should not treat objects with non-number lengths as array-like', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.isEmpty({ 'length': '0' }), false);
    });
    QUnit.test('should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.strictEqual(_({}).isEmpty(), true);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_({}).chain().isEmpty() instanceof _);
        } else {
            skipAssert(assert);
        }
    });
}());