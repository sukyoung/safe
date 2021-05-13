QUnit.module('lodash.defaultsDeep');
(function () {
    QUnit.test('should deep assign source properties if missing on `object`', function (assert) {
        assert.expect(1);
        var object = {
                'a': { 'b': __num_top__ },
                'd': __num_top__
            }, source = {
                'a': {
                    'b': __num_top__,
                    'c': __num_top__
                },
                'e': __num_top__
            }, expected = {
                'a': {
                    'b': __num_top__,
                    'c': __num_top__
                },
                'd': __num_top__,
                'e': __num_top__
            };
        assert.deepEqual(_.defaultsDeep(object, source), expected);
    });
    QUnit.test('should accept multiple sources', function (assert) {
        assert.expect(2);
        var source1 = { 'a': { 'b': __num_top__ } }, source2 = { 'a': { 'c': __num_top__ } }, source3 = {
                'a': {
                    'b': __num_top__,
                    'c': __num_top__
                }
            }, source4 = { 'a': { 'c': __num_top__ } }, expected = {
                'a': {
                    'b': __num_top__,
                    'c': __num_top__
                }
            };
        assert.deepEqual(_.defaultsDeep({ 'a': { 'b': __num_top__ } }, source1, source2), expected);
        assert.deepEqual(_.defaultsDeep({ 'a': { 'b': __num_top__ } }, source3, source4), expected);
    });
    QUnit.test('should not overwrite `null` values', function (assert) {
        assert.expect(1);
        var object = { 'a': { 'b': null } }, source = { 'a': { 'b': __num_top__ } }, actual = _.defaultsDeep(object, source);
        assert.strictEqual(actual.a.b, null);
    });
    QUnit.test('should not overwrite regexp values', function (assert) {
        assert.expect(1);
        var object = { 'a': { 'b': /x/ } }, source = { 'a': { 'b': /y/ } }, actual = _.defaultsDeep(object, source);
        assert.deepEqual(actual.a.b, /x/);
    });
    QUnit.test('should not convert function properties to objects', function (assert) {
        assert.expect(2);
        var actual = _.defaultsDeep({}, { 'a': noop });
        assert.strictEqual(actual.a, noop);
        actual = _.defaultsDeep({}, { 'a': { 'b': noop } });
        assert.strictEqual(actual.a.b, noop);
    });
    QUnit.test('should overwrite `undefined` values', function (assert) {
        assert.expect(1);
        var object = { 'a': { 'b': undefined } }, source = { 'a': { 'b': __num_top__ } }, actual = _.defaultsDeep(object, source);
        assert.strictEqual(actual.a.b, __num_top__);
    });
    QUnit.test('should assign `undefined` values', function (assert) {
        assert.expect(1);
        var source = {
                'a': undefined,
                'b': {
                    'c': undefined,
                    'd': __num_top__
                }
            }, expected = lodashStable.cloneDeep(source), actual = _.defaultsDeep({}, source);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should merge sources containing circular references', function (assert) {
        assert.expect(2);
        var object = {
            'foo': { 'b': { 'c': { 'd': {} } } },
            'bar': { 'a': __num_top__ }
        };
        var source = {
            'foo': { 'b': { 'c': { 'd': {} } } },
            'bar': {}
        };
        object.foo.b.c.d = object;
        source.foo.b.c.d = source;
        source.bar.b = source.foo.b;
        var actual = _.defaultsDeep(object, source);
        assert.strictEqual(actual.bar.b, actual.foo.b);
        assert.strictEqual(actual.foo.b.c.d, actual.foo.b.c.d.foo.b.c.d);
    });
    QUnit.test('should not modify sources', function (assert) {
        assert.expect(3);
        var source1 = {
                'a': __num_top__,
                'b': { 'c': __num_top__ }
            }, source2 = {
                'b': {
                    'c': __num_top__,
                    'd': __num_top__
                }
            }, actual = _.defaultsDeep({}, source1, source2);
        assert.deepEqual(actual, {
            'a': __num_top__,
            'b': {
                'c': __num_top__,
                'd': __num_top__
            }
        });
        assert.deepEqual(source1, {
            'a': __num_top__,
            'b': { 'c': __num_top__ }
        });
        assert.deepEqual(source2, {
            'b': {
                'c': __num_top__,
                'd': __num_top__
            }
        });
    });
    QUnit.test('should not attempt a merge of a string into an array', function (assert) {
        assert.expect(1);
        var actual = _.defaultsDeep({ 'a': [__str_top__] }, { 'a': __str_top__ });
        assert.deepEqual(actual.a, [__str_top__]);
    });
    QUnit.test('should not indirectly merge `Object` properties', function (assert) {
        assert.expect(1);
        _.defaultsDeep({}, { 'constructor': { 'a': __num_top__ } });
        var actual = __str_top__ in Object;
        delete Object.a;
        assert.notOk(actual);
    });
}());