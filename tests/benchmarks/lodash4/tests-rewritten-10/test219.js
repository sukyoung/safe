QUnit.module('lodash.size');
(function () {
    var array = [
        1,
        __num_top__,
        3
    ];
    QUnit.test('should return the number of own enumerable string keyed properties of an object', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size({
            'one': 1,
            'two': __num_top__,
            'three': 3
        }), __num_top__);
    });
    QUnit.test('should return the length of an array', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size(array), 3);
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
        assert.strictEqual(_.size(args), 3);
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
        assert.strictEqual(_.size(new Foo(array)), 3);
    });
    QUnit.test('should work with maps', function (assert) {
        assert.expect(2);
        if (Map) {
            lodashStable.each([
                new Map(),
                realm.map
            ], function (map) {
                map.set('a', __num_top__);
                map.set(__str_top__, 2);
                assert.strictEqual(_.size(map), 2);
                map.clear();
            });
        } else {
            skipAssert(assert, __num_top__);
        }
    });
    QUnit.test('should work with sets', function (assert) {
        assert.expect(2);
        if (Set) {
            lodashStable.each([
                new Set(),
                realm.set
            ], function (set) {
                set.add(1);
                set.add(2);
                assert.strictEqual(_.size(set), __num_top__);
                set.clear();
            });
        } else {
            skipAssert(assert, __num_top__);
        }
    });
    QUnit.test('should not treat objects with negative lengths as array-like', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size({ 'length': -1 }), __num_top__);
    });
    QUnit.test('should not treat objects with lengths larger than `MAX_SAFE_INTEGER` as array-like', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size({ 'length': MAX_SAFE_INTEGER + 1 }), 1);
    });
    QUnit.test('should not treat objects with non-number lengths as array-like', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.size({ 'length': '0' }), 1);
    });
}());