# Change Log

### 0.13.0 (01-12-2015)

* Add `numeric?` validator.

### 0.13.0 (29-11-2015)

* Add much better support for using multiple validators against a key. It is now
possible to do the following.

```clojure
(let [validation-set (core/validation-set [:age required? [greater? 18] [lesser? 30]]))
```

### 0.12.0 (29-11-2015)

* Add `:short-circuit?` option to validate option map. Can be used to break validation
on the first validation failure.

### 0.11.0 (27-11-2015)

* Add `credit-card?` validator based on the Luhn algorithm.

### 0.10.5 (26-11-2015)
### 0.10.4 (26-11-2015)

* No functional difference. Enable package signing for Clojars deployment.

### 0.10.3 (26-11-2015)

* Protect against nil values in numerical validators. Where either the
value being validated or any of the required validator arguments are nil then
that validator will be falsey.

### 0.10.1 (24-11-2015)

* Qualify clova specific meta data keys with the clova.core namespace.

    `:type` becomes `:clova.core/type`
    `:default-message` becomes `:clova.core/default-message`
    `:allow-missing-key?` becomes `:clova.core/allow-missing-key?`
    `:target` becomes `:clova.core/target`
    `:args` becomes `:clova.core/args`

### 0.10.0 (23-11-2015)

* Add support for using validator functions as predicates to `all?`.

```clojure
(let [validation-set (core/validation-set [:age [all? [[greater? 18] [lesser? 30]]]])]

```

### 0.9.0 (23-11-2015)

* Add all? validator.
* Minor readability fixes to remove use of annonymous functions where using an internal Clojure function
makes more sense.

### 0.8.1 (22-11-2015)

* Fix issue where the `one-of?` validator does not return a true/false value and when used in
isolation can result in erroneous results."

### 0.8.0 (21-11-2015)

* Remove `required` wrapper function and replace it with a `required?` validator.
    Instead of wrapping a validator with the required function we can just compose validators. e.g.

    ```clojure

    ;; This will failed because the :age key is required
    (validate (core/validation-set
                   [:age core/required?
                    :age [core/between? 18 30]]) {})

    ;; This will fail because age is not between 18 and 30
    (validate (core/validation-set
                   [:age core/required?
                    :age [core/between? 18 30]]) {:age 1})
    ```

* Rename `present?` validator to `not-nil?`.

### 0.7.0 (20-11-2015)

* Add :clova.core/allow-missing-key? to enable default behaviour where validators do not fail if a key is not present.
* Add `required` function to disable :clova.core/allow-missing-key? and force a key to be present.

### 0.6.0 (19-11-2015)

* Add longer? validator.
* Add shorter? validator.

### 0.5.0 (19-11-2015)

* Fixed issue where comparing a nil 'value' with numeric type validators would cause a NullPointerException.
* Add option to pass :default-message-fn to validate in order to support custom validation messages.