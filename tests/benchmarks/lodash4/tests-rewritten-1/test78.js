QUnit.module('`__proto__` property bugs');
(function () {
    QUnit.test('should work with the "__proto__" key in internal data objects', function (assert) {
        assert.expect(4);
        var stringLiteral = '__proto__', stringObject = Object(stringLiteral), expected = [
                stringLiteral,
                stringObject
            ];
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, function (count) {
            return isEven(count) ? stringLiteral : stringObject;
        });
        assert.deepEqual(_.difference(largeArray, largeArray), []);
        assert.deepEqual(_.intersection(largeArray, largeArray), expected);
        assert.deepEqual(_.uniq(largeArray), expected);
        assert.deepEqual(_.without.apply(_, [largeArray].concat(largeArray)), []);
    });
    QUnit.test('should treat "__proto__" as a regular key in assignments', function (assert) {
        assert.expect(2);
        var methods = [
            'assign',
            'assignIn',
            'defaults',
            'defaultsDeep',
            'merge'
        ];
        var source = create(null);
        source.__proto__ = [];
        var expected = lodashStable.map(methods, stubFalse);
        var actual = lodashStable.map(methods, function (methodName) {
            var result = _[methodName]({}, source);
            return result instanceof Array;
        });
        assert.deepEqual(actual, expected);
        actual = _.groupBy([{ 'a': '__proto__' }], 'a');
        assert.notOk(actual instanceof Array);
    });
    QUnit.test('should not merge "__proto__" properties', function (assert) {
        assert.expect(1);
        if (JSON) {
            _.merge({}, JSON.parse(__str_top__));
            var actual = 'a' in objectProto;
            delete objectProto.a;
            assert.notOk(actual);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should not indirectly merge builtin prototype properties', function (assert) {
        assert.expect(2);
        _.merge({}, { 'toString': { 'constructor': { 'prototype': { 'a': 1 } } } });
        var actual = 'a' in funcProto;
        delete funcProto.a;
        assert.notOk(actual);
        _.merge({}, { 'constructor': { 'prototype': { 'a': 1 } } });
        actual = 'a' in objectProto;
        delete objectProto.a;
        assert.notOk(actual);
    });
    QUnit.test('should not indirectly merge `Object` properties', function (assert) {
        assert.expect(1);
        _.merge({}, { 'constructor': { 'a': 1 } });
        var actual = 'a' in Object;
        delete Object.a;
        assert.notOk(actual);
    });
}());