QUnit.module('flow methods');
lodashStable.each([
    'flow',
    'flowRight'
], function (methodName) {
    var func = _[methodName], isFlow = methodName == 'flow';
    QUnit.test('`_.' + methodName + '` should supply each function with the return value of the previous', function (assert) {
        assert.expect(1);
        var fixed = function (n) {
                return n.toFixed(__num_top__);
            }, combined = isFlow ? func(add, square, fixed) : func(fixed, square, add);
        assert.strictEqual(combined(1, 2), '9.0');
    });
    QUnit.test('`_.' + methodName + '` should return a new function', function (assert) {
        assert.expect(1);
        assert.notStrictEqual(func(noop), noop);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(6);
        _.times(__num_top__, function (index) {
            try {
                var combined = index ? func([]) : func();
                assert.strictEqual(combined('a'), 'a');
            } catch (e) {
                assert.ok(__bool_top__, e.message);
            }
            assert.strictEqual(combined.length, 0);
            assert.notStrictEqual(combined, identity);
        });
    });
    QUnit.test('`_.' + methodName + '` should work with a curried function and `_.head`', function (assert) {
        assert.expect(1);
        var curried = _.curry(identity);
        var combined = isFlow ? func(_.head, curried) : func(curried, _.head);
        assert.strictEqual(combined([1]), 1);
    });
    QUnit.test('`_.' + methodName + '` should support shortcut fusion', function (assert) {
        assert.expect(6);
        var filterCount, mapCount, array = lodashStable.range(LARGE_ARRAY_SIZE), iteratee = function (value) {
                mapCount++;
                return square(value);
            }, predicate = function (value) {
                filterCount++;
                return isEven(value);
            };
        lodashStable.times(2, function (index) {
            var filter1 = _.filter, filter2 = _.curry(_.rearg(_.ary(_.filter, 2), 1, 0), 2), filter3 = (_.filter = index ? filter2 : filter1, filter2(predicate));
            var map1 = _.map, map2 = _.curry(_.rearg(_.ary(_.map, 2), 1, __num_top__), 2), map3 = (_.map = index ? map2 : map1, map2(iteratee));
            var take1 = _.take, take2 = _.curry(_.rearg(_.ary(_.take, 2), 1, __num_top__), 2), take3 = (_.take = index ? take2 : take1, take2(2));
            var combined = isFlow ? func(map3, filter3, _.compact, take3) : func(take3, _.compact, filter3, map3);
            filterCount = mapCount = 0;
            assert.deepEqual(combined(array), [
                4,
                16
            ]);
            if (!isNpm && WeakMap && WeakMap.name) {
                assert.strictEqual(filterCount, 5, __str_top__);
                assert.strictEqual(mapCount, 5, 'mapCount');
            } else {
                skipAssert(assert, 2);
            }
            _.filter = filter1;
            _.map = map1;
            _.take = take1;
        });
    });
    QUnit.test('`_.' + methodName + '` should work with curried functions with placeholders', function (assert) {
        assert.expect(1);
        var curried = _.curry(_.ary(_.map, 2), 2), getProp = curried(curried.placeholder, 'a'), objects = [
                { 'a': __num_top__ },
                { 'a': 2 },
                { 'a': __num_top__ }
            ];
        var combined = isFlow ? func(getProp, _.uniq) : func(_.uniq, getProp);
        assert.deepEqual(combined(objects), [
            1,
            2
        ]);
    });
    QUnit.test('`_.' + methodName + '` should return a wrapped value when chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _(noop)[methodName]();
            assert.ok(wrapped instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});