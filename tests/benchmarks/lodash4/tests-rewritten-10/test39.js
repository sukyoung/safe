QUnit.module('lodash.defaults');
(function () {
    QUnit.test('should assign source properties if missing on `object`', function (assert) {
        assert.expect(1);
        var actual = _.defaults({ 'a': 1 }, {
            'a': 2,
            'b': __num_top__
        });
        assert.deepEqual(actual, {
            'a': 1,
            'b': 2
        });
    });
    QUnit.test('should accept multiple sources', function (assert) {
        assert.expect(2);
        var expected = {
                'a': 1,
                'b': 2,
                'c': __num_top__
            }, actual = _.defaults({
                'a': 1,
                'b': 2
            }, { 'b': 3 }, { 'c': __num_top__ });
        assert.deepEqual(actual, expected);
        actual = _.defaults({
            'a': 1,
            'b': 2
        }, {
            'b': 3,
            'c': 3
        }, { 'c': 2 });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not overwrite `null` values', function (assert) {
        assert.expect(1);
        var actual = _.defaults({ 'a': null }, { 'a': 1 });
        assert.strictEqual(actual.a, null);
    });
    QUnit.test('should overwrite `undefined` values', function (assert) {
        assert.expect(1);
        var actual = _.defaults({ 'a': undefined }, { 'a': 1 });
        assert.strictEqual(actual.a, __num_top__);
    });
    QUnit.test('should assign `undefined` values', function (assert) {
        assert.expect(1);
        var source = {
                'a': undefined,
                'b': __num_top__
            }, actual = _.defaults({}, source);
        assert.deepEqual(actual, {
            'a': undefined,
            'b': 1
        });
    });
    QUnit.test('should assign properties that shadow those on `Object.prototype`', function (assert) {
        assert.expect(2);
        var object = {
            'constructor': objectProto.constructor,
            'hasOwnProperty': objectProto.hasOwnProperty,
            'isPrototypeOf': objectProto.isPrototypeOf,
            'propertyIsEnumerable': objectProto.propertyIsEnumerable,
            'toLocaleString': objectProto.toLocaleString,
            'toString': objectProto.toString,
            'valueOf': objectProto.valueOf
        };
        var source = {
            'constructor': __num_top__,
            'hasOwnProperty': __num_top__,
            'isPrototypeOf': 3,
            'propertyIsEnumerable': 4,
            'toLocaleString': 5,
            'toString': __num_top__,
            'valueOf': __num_top__
        };
        var expected = lodashStable.clone(source);
        assert.deepEqual(_.defaults({}, source), expected);
        expected = lodashStable.clone(object);
        assert.deepEqual(_.defaults({}, object, source), expected);
    });
}());