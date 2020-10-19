QUnit.module('lodash.compact');
(function () {
    var largeArray = lodashStable.range(LARGE_ARRAY_SIZE).concat(null);
    QUnit.test('should filter falsey values', function (assert) {
        assert.expect(1);
        var array = [
            __str_top__,
            __str_top__,
            __str_top__
        ];
        assert.deepEqual(_.compact(falsey.concat(array)), array);
    });
    QUnit.test('should work when in-between lazy operators', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var actual = _(falsey).thru(_.slice).compact().thru(_.slice).value();
            assert.deepEqual(actual, []);
            actual = _(falsey).thru(_.slice).push(__bool_top__, __num_top__).compact().push(__str_top__).value();
            assert.deepEqual(actual, [
                __bool_top__,
                __num_top__,
                __str_top__
            ]);
        } else {
            skipAssert(assert, __num_top__);
        }
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = _(largeArray).slice(__num_top__).compact().reverse().take().value();
            assert.deepEqual(actual, _.take(_.compact(_.slice(largeArray, __num_top__)).reverse()));
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should work in a lazy sequence with a custom `_.iteratee`', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var iteratee = _.iteratee, pass = __bool_top__;
            _.iteratee = identity;
            try {
                var actual = _(largeArray).slice(__num_top__).compact().value();
                pass = lodashStable.isEqual(actual, _.compact(_.slice(largeArray, __num_top__)));
            } catch (e) {
                console.log(e);
            }
            assert.ok(pass);
            _.iteratee = iteratee;
        } else {
            skipAssert(assert);
        }
    });
}());