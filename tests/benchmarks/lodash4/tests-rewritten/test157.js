QUnit.module('lodash.mergeWith');
(function () {
    QUnit.test('should handle merging when `customizer` returns `undefined`', function (assert) {
        assert.expect(2);
        var actual = _.mergeWith({
            'a': {
                'b': [
                    __num_top__,
                    __num_top__
                ]
            }
        }, { 'a': { 'b': [__num_top__] } }, noop);
        assert.deepEqual(actual, {
            'a': {
                'b': [
                    __num_top__,
                    __num_top__
                ]
            }
        });
        actual = _.mergeWith([], [undefined], identity);
        assert.deepEqual(actual, [undefined]);
    });
    QUnit.test('should clone sources when `customizer` returns `undefined`', function (assert) {
        assert.expect(1);
        var source1 = { 'a': { 'b': { 'c': __num_top__ } } }, source2 = { 'a': { 'b': { 'd': __num_top__ } } };
        _.mergeWith({}, source1, source2, noop);
        assert.deepEqual(source1.a.b, { 'c': __num_top__ });
    });
    QUnit.test('should defer to `customizer` for non `undefined` results', function (assert) {
        assert.expect(1);
        var actual = _.mergeWith({
            'a': {
                'b': [
                    __num_top__,
                    __num_top__
                ]
            }
        }, { 'a': { 'b': [__num_top__] } }, function (a, b) {
            return lodashStable.isArray(a) ? a.concat(b) : undefined;
        });
        assert.deepEqual(actual, {
            'a': {
                'b': [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ]
            }
        });
    });
    QUnit.test('should provide `stack` to `customizer`', function (assert) {
        assert.expect(4);
        var actual = [];
        _.mergeWith({}, {
            'z': __num_top__,
            'a': { 'b': __num_top__ }
        }, function () {
            actual.push(_.last(arguments));
        });
        assert.strictEqual(actual.length, __num_top__);
        _.each(actual, function (a) {
            assert.ok(isNpm ? a.constructor.name == __str_top__ : a instanceof mapCaches.Stack);
        });
    });
    QUnit.test('should overwrite primitives with source object clones', function (assert) {
        assert.expect(1);
        var actual = _.mergeWith({ 'a': __num_top__ }, { 'a': { 'b': [__str_top__] } }, function (a, b) {
            return lodashStable.isArray(a) ? a.concat(b) : undefined;
        });
        assert.deepEqual(actual, { 'a': { 'b': [__str_top__] } });
    });
    QUnit.test('should pop the stack of sources for each sibling property', function (assert) {
        assert.expect(1);
        var array = [
                __str_top__,
                __str_top__
            ], object = { 'a': [__str_top__] }, source = {
                'a': array,
                'b': array
            };
        var actual = _.mergeWith(object, source, function (a, b) {
            return lodashStable.isArray(a) ? a.concat(b) : undefined;
        });
        assert.deepEqual(actual, {
            'a': [
                __str_top__,
                __str_top__,
                __str_top__
            ],
            'b': [
                __str_top__,
                __str_top__
            ]
        });
    });
}());