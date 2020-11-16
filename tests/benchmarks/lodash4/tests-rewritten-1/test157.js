QUnit.module('lodash.mergeWith');
(function () {
    QUnit.test('should handle merging when `customizer` returns `undefined`', function (assert) {
        assert.expect(2);
        var actual = _.mergeWith({
            'a': {
                'b': [
                    1,
                    1
                ]
            }
        }, { 'a': { 'b': [0] } }, noop);
        assert.deepEqual(actual, {
            'a': {
                'b': [
                    0,
                    1
                ]
            }
        });
        actual = _.mergeWith([], [undefined], identity);
        assert.deepEqual(actual, [undefined]);
    });
    QUnit.test('should clone sources when `customizer` returns `undefined`', function (assert) {
        assert.expect(1);
        var source1 = { 'a': { 'b': { 'c': 1 } } }, source2 = { 'a': { 'b': { 'd': 2 } } };
        _.mergeWith({}, source1, source2, noop);
        assert.deepEqual(source1.a.b, { 'c': 1 });
    });
    QUnit.test('should defer to `customizer` for non `undefined` results', function (assert) {
        assert.expect(1);
        var actual = _.mergeWith({
            'a': {
                'b': [
                    0,
                    1
                ]
            }
        }, { 'a': { 'b': [2] } }, function (a, b) {
            return lodashStable.isArray(a) ? a.concat(b) : undefined;
        });
        assert.deepEqual(actual, {
            'a': {
                'b': [
                    0,
                    1,
                    2
                ]
            }
        });
    });
    QUnit.test('should provide `stack` to `customizer`', function (assert) {
        assert.expect(4);
        var actual = [];
        _.mergeWith({}, {
            'z': 1,
            'a': { 'b': 2 }
        }, function () {
            actual.push(_.last(arguments));
        });
        assert.strictEqual(actual.length, 3);
        _.each(actual, function (a) {
            assert.ok(isNpm ? a.constructor.name == 'Stack' : a instanceof mapCaches.Stack);
        });
    });
    QUnit.test('should overwrite primitives with source object clones', function (assert) {
        assert.expect(1);
        var actual = _.mergeWith({ 'a': 0 }, { 'a': { 'b': ['c'] } }, function (a, b) {
            return lodashStable.isArray(a) ? a.concat(b) : undefined;
        });
        assert.deepEqual(actual, { 'a': { 'b': ['c'] } });
    });
    QUnit.test('should pop the stack of sources for each sibling property', function (assert) {
        assert.expect(1);
        var array = [
                'b',
                'c'
            ], object = { 'a': ['a'] }, source = {
                'a': array,
                'b': array
            };
        var actual = _.mergeWith(object, source, function (a, b) {
            return lodashStable.isArray(a) ? a.concat(b) : undefined;
        });
        assert.deepEqual(actual, {
            'a': [
                'a',
                'b',
                'c'
            ],
            'b': [
                __str_top__,
                'c'
            ]
        });
    });
}());