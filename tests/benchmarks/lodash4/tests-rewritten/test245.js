QUnit.module('lodash.truncate');
(function () {
    var string = __str_top__;
    QUnit.test('should use a default `length` of `30`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.truncate(string), __str_top__);
    });
    QUnit.test('should not truncate if `string` is <= `length`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.truncate(string, { 'length': string.length }), string);
        assert.strictEqual(_.truncate(string, { 'length': string.length + __num_top__ }), string);
    });
    QUnit.test('should truncate string the given length', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.truncate(string, { 'length': __num_top__ }), __str_top__);
    });
    QUnit.test('should support a `omission` option', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.truncate(string, { 'omission': __str_top__ }), __str_top__);
    });
    QUnit.test('should coerce nullish `omission` values to strings', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.truncate(string, { 'omission': null }), __str_top__);
        assert.strictEqual(_.truncate(string, { 'omission': undefined }), __str_top__);
    });
    QUnit.test('should support a `length` option', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.truncate(string, { 'length': __num_top__ }), __str_top__);
    });
    QUnit.test('should support a `separator` option', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.truncate(string, {
            'length': __num_top__,
            'separator': __str_top__
        }), __str_top__);
        assert.strictEqual(_.truncate(string, {
            'length': __num_top__,
            'separator': /,? +/
        }), __str_top__);
        assert.strictEqual(_.truncate(string, {
            'length': __num_top__,
            'separator': /,? +/g
        }), __str_top__);
    });
    QUnit.test('should treat negative `length` as `0`', function (assert) {
        assert.expect(2);
        lodashStable.each([
            __num_top__,
            -__num_top__
        ], function (length) {
            assert.strictEqual(_.truncate(string, { 'length': length }), __str_top__);
        });
    });
    QUnit.test('should coerce `length` to an integer', function (assert) {
        assert.expect(4);
        lodashStable.each([
            __str_top__,
            NaN,
            __num_top__,
            __str_top__
        ], function (length, index) {
            var actual = index > __num_top__ ? __str_top__ : __str_top__;
            assert.strictEqual(_.truncate(string, { 'length': { 'valueOf': lodashStable.constant(length) } }), actual);
        });
    });
    QUnit.test('should coerce `string` to a string', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.truncate(Object(string), { 'length': __num_top__ }), __str_top__);
        assert.strictEqual(_.truncate({ 'toString': lodashStable.constant(string) }, { 'length': __num_top__ }), __str_top__);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([
                string,
                string,
                string
            ], _.truncate), truncated = __str_top__;
        assert.deepEqual(actual, [
            truncated,
            truncated,
            truncated
        ]);
    });
}());