QUnit.module('flow methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isFlow = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var fixed = function (n) {
                return n.toFixed(__num_top__);
            }, combined = isFlow ? func(add, square, fixed) : func(fixed, square, add);
        assert.strictEqual(combined(__num_top__, __num_top__), __str_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.notStrictEqual(func(noop), noop);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(6);
        _.times(__num_top__, function (index) {
            try {
                var combined = index ? func([]) : func();
                assert.strictEqual(combined(__str_top__), __str_top__);
            } catch (e) {
                assert.ok(__bool_top__, e.message);
            }
            assert.strictEqual(combined.length, __num_top__);
            assert.notStrictEqual(combined, identity);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var curried = _.curry(identity);
        var combined = isFlow ? func(_.head, curried) : func(curried, _.head);
        assert.strictEqual(combined([__num_top__]), __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(6);
        var filterCount, mapCount, array = lodashStable.range(LARGE_ARRAY_SIZE), iteratee = function (value) {
                mapCount++;
                return square(value);
            }, predicate = function (value) {
                filterCount++;
                return isEven(value);
            };
        lodashStable.times(__num_top__, function (index) {
            var filter1 = _.filter, filter2 = _.curry(_.rearg(_.ary(_.filter, __num_top__), __num_top__, __num_top__), __num_top__), filter3 = (_.filter = index ? filter2 : filter1, filter2(predicate));
            var map1 = _.map, map2 = _.curry(_.rearg(_.ary(_.map, __num_top__), __num_top__, __num_top__), __num_top__), map3 = (_.map = index ? map2 : map1, map2(iteratee));
            var take1 = _.take, take2 = _.curry(_.rearg(_.ary(_.take, __num_top__), __num_top__, __num_top__), __num_top__), take3 = (_.take = index ? take2 : take1, take2(__num_top__));
            var combined = isFlow ? func(map3, filter3, _.compact, take3) : func(take3, _.compact, filter3, map3);
            filterCount = mapCount = __num_top__;
            assert.deepEqual(combined(array), [
                __num_top__,
                __num_top__
            ]);
            if (!isNpm && WeakMap && WeakMap.name) {
                assert.strictEqual(filterCount, __num_top__, __str_top__);
                assert.strictEqual(mapCount, __num_top__, __str_top__);
            } else {
                skipAssert(assert, 2);
            }
            _.filter = filter1;
            _.map = map1;
            _.take = take1;
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var curried = _.curry(_.ary(_.map, __num_top__), __num_top__), getProp = curried(curried.placeholder, __str_top__), objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ },
                { 'a': __num_top__ }
            ];
        var combined = isFlow ? func(getProp, _.uniq) : func(_.uniq, getProp);
        assert.deepEqual(combined(objects), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _(noop)[methodName]();
            assert.ok(wrapped instanceof _);
        } else {
            skipAssert(assert);
        }
    });
});