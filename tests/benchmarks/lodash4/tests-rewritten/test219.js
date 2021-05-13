QUnit.module('lodash.size');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should return the number of own enumerable string keyed properties of an object', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size({
            'one': __num_top__,
            'two': __num_top__,
            'three': __num_top__
        }), __num_top__);
    });
    QUnit.test('should return the length of an array', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size(array), __num_top__);
    });
    QUnit.test('should accept a falsey `object`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubZero);
        var actual = lodashStable.map(falsey, function (object, index) {
            try {
                return index ? _.size(object) : _.size();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `arguments` objects', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size(args), __num_top__);
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
        assert.strictEqual(_.size(new Foo(array)), __num_top__);
    });
    QUnit.test('should work with maps', function (assert) {
        assert.expect(2);
        if (Map) {
            lodashStable.each([
                new Map(),
                realm.map
            ], function (map) {
                map.set(__str_top__, __num_top__);
                map.set(__str_top__, __num_top__);
                assert.strictEqual(_.size(map), __num_top__);
                map.clear();
            });
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should work with sets', function (assert) {
        assert.expect(2);
        if (Set) {
            lodashStable.each([
                new Set(),
                realm.set
            ], function (set) {
                set.add(__num_top__);
                set.add(__num_top__);
                assert.strictEqual(_.size(set), __num_top__);
                set.clear();
            });
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should not treat objects with negative lengths as array-like', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size({ 'length': -__num_top__ }), __num_top__);
    });
    QUnit.test('should not treat objects with lengths larger than `MAX_SAFE_INTEGER` as array-like', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size({ 'length': MAX_SAFE_INTEGER + __num_top__ }), __num_top__);
    });
    QUnit.test('should not treat objects with non-number lengths as array-like', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size({ 'length': __str_top__ }), __num_top__);
    });
}());