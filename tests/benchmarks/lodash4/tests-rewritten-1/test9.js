QUnit.module('lodash.assign and lodash.assignIn');
lodashStable.each([
    'assign',
    'assignIn'
], function (methodName) {
    var func = _[methodName];
    QUnit.test('`_.' + methodName + '` should assign source properties to `object`', function (assert) {
        assert.expect(1);
        assert.deepEqual(func({ 'a': __num_top__ }, { 'b': 2 }), {
            'a': 1,
            'b': 2
        });
    });
    QUnit.test('`_.' + methodName + '` should accept multiple sources', function (assert) {
        assert.expect(2);
        var expected = {
            'a': 1,
            'b': 2,
            'c': 3
        };
        assert.deepEqual(func({ 'a': 1 }, { 'b': 2 }, { 'c': 3 }), expected);
        assert.deepEqual(func({ 'a': 1 }, {
            'b': 2,
            'c': 2
        }, { 'c': 3 }), expected);
    });
    QUnit.test('`_.' + methodName + '` should overwrite destination properties', function (assert) {
        assert.expect(1);
        var expected = {
            'a': 3,
            'b': 2,
            'c': 1
        };
        assert.deepEqual(func({
            'a': 1,
            'b': 2
        }, expected), expected);
    });
    QUnit.test('`_.' + methodName + '` should assign source properties with nullish values', function (assert) {
        assert.expect(1);
        var expected = {
            'a': null,
            'b': undefined,
            'c': null
        };
        assert.deepEqual(func({
            'a': 1,
            'b': 2
        }, expected), expected);
    });
    QUnit.test('`_.' + methodName + '` should skip assignments if values are the same', function (assert) {
        assert.expect(1);
        var object = {};
        var descriptor = {
            'configurable': true,
            'enumerable': true,
            'set': function () {
                throw new Error();
            }
        };
        var source = {
            'a': 1,
            'b': undefined,
            'c': NaN,
            'd': undefined,
            'constructor': Object,
            'toString': lodashStable.constant('source')
        };
        defineProperty(object, 'a', lodashStable.assign({}, descriptor, { 'get': stubOne }));
        defineProperty(object, 'b', lodashStable.assign({}, descriptor, { 'get': noop }));
        defineProperty(object, 'c', lodashStable.assign({}, descriptor, { 'get': stubNaN }));
        defineProperty(object, 'constructor', lodashStable.assign({}, descriptor, { 'get': lodashStable.constant(Object) }));
        try {
            var actual = func(object, source);
        } catch (e) {
        }
        assert.deepEqual(actual, source);
    });
    QUnit.test('`_.' + methodName + '` should treat sparse array sources as dense', function (assert) {
        assert.expect(1);
        var array = [1];
        array[2] = 3;
        assert.deepEqual(func({}, array), {
            '0': 1,
            '1': undefined,
            '2': 3
        });
    });
    QUnit.test('`_.' + methodName + '` should assign values of prototype objects', function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.prototype.a = 1;
        assert.deepEqual(func({}, Foo.prototype), { 'a': 1 });
    });
    QUnit.test('`_.' + methodName + '` should coerce string sources to objects', function (assert) {
        assert.expect(1);
        assert.deepEqual(func({}, 'a'), { '0': 'a' });
    });
});