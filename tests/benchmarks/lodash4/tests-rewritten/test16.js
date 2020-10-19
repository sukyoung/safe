QUnit.module('lodash.bindAll');
(function () {
    var args = toArgs([__str_top__]);
    var source = {
        '_n0': -__num_top__,
        '_p0': -__num_top__,
        '_a': __num_top__,
        '_b': __num_top__,
        '_c': __num_top__,
        '_d': __num_top__,
        '-0': function () {
            return this._n0;
        },
        '0': function () {
            return this._p0;
        },
        'a': function () {
            return this._a;
        },
        'b': function () {
            return this._b;
        },
        'c': function () {
            return this._c;
        },
        'd': function () {
            return this._d;
        }
    };
    QUnit.test('should accept individual method names', function (assert) {
        assert.expect(1);
        var object = lodashStable.cloneDeep(source);
        _.bindAll(object, __str_top__, __str_top__);
        var actual = lodashStable.map([
            __str_top__,
            __str_top__,
            __str_top__
        ], function (key) {
            return object[key].call({});
        });
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            undefined
        ]);
    });
    QUnit.test('should accept arrays of method names', function (assert) {
        assert.expect(1);
        var object = lodashStable.cloneDeep(source);
        _.bindAll(object, [
            __str_top__,
            __str_top__
        ], [__str_top__]);
        var actual = lodashStable.map([
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ], function (key) {
            return object[key].call({});
        });
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__,
            undefined
        ]);
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var props = [
            -__num_top__,
            Object(-__num_top__),
            __num_top__,
            Object(__num_top__)
        ];
        var actual = lodashStable.map(props, function (key) {
            var object = lodashStable.cloneDeep(source);
            _.bindAll(object, key);
            return object[lodashStable.toString(key)].call({});
        });
        assert.deepEqual(actual, [
            -__num_top__,
            -__num_top__,
            -__num_top__,
            -__num_top__
        ]);
    });
    QUnit.test('should work with an array `object`', function (assert) {
        assert.expect(1);
        var array = [
            __str_top__,
            __str_top__
        ];
        _.bindAll(array);
        assert.strictEqual(array.pop, arrayProto.pop);
    });
    QUnit.test('should work with `arguments` objects as secondary arguments', function (assert) {
        assert.expect(1);
        var object = lodashStable.cloneDeep(source);
        _.bindAll(object, args);
        var actual = lodashStable.map(args, function (key) {
            return object[key].call({});
        });
        assert.deepEqual(actual, [__num_top__]);
    });
}());