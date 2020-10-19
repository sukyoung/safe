QUnit.module('lodash.assign and lodash.assignIn');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func({ 'a': __num_top__ }, { 'b': __num_top__ }), {
            'a': __num_top__,
            'b': __num_top__
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var expected = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        };
        assert.deepEqual(func({ 'a': __num_top__ }, { 'b': __num_top__ }, { 'c': __num_top__ }), expected);
        assert.deepEqual(func({ 'a': __num_top__ }, {
            'b': __num_top__,
            'c': __num_top__
        }, { 'c': __num_top__ }), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        };
        assert.deepEqual(func({
            'a': __num_top__,
            'b': __num_top__
        }, expected), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = {
            'a': null,
            'b': undefined,
            'c': null
        };
        assert.deepEqual(func({
            'a': __num_top__,
            'b': __num_top__
        }, expected), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {};
        var descriptor = {
            'configurable': __bool_top__,
            'enumerable': __bool_top__,
            'set': function () {
                throw new Error();
            }
        };
        var source = {
            'a': __num_top__,
            'b': undefined,
            'c': NaN,
            'd': undefined,
            'constructor': Object,
            'toString': lodashStable.constant(__str_top__)
        };
        defineProperty(object, __str_top__, lodashStable.assign({}, descriptor, { 'get': stubOne }));
        defineProperty(object, __str_top__, lodashStable.assign({}, descriptor, { 'get': noop }));
        defineProperty(object, __str_top__, lodashStable.assign({}, descriptor, { 'get': stubNaN }));
        defineProperty(object, __str_top__, lodashStable.assign({}, descriptor, { 'get': lodashStable.constant(Object) }));
        try {
            var actual = func(object, source);
        } catch (e) {
        }
        assert.deepEqual(actual, source);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [__num_top__];
        array[__num_top__] = __num_top__;
        assert.deepEqual(func({}, array), {
            '0': __num_top__,
            '1': undefined,
            '2': __num_top__
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.prototype.a = __num_top__;
        assert.deepEqual(func({}, Foo.prototype), { 'a': __num_top__ });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func({}, __str_top__), { '0': __str_top__ });
    });
});