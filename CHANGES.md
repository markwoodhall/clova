## 0.8.0 (21-11-2015)

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

## 0.7.0 (20-11-2015)

* Add :allow-missing-key? to enable default behaviour where validators do not fail if a key is not present.
* Add `required` function to disable :allow-missing-key? and force a key to be present.

## 0.6.0 (19-11-2015)

* Add longer? validator.
* Add shorter? validator.

## 0.5.0 (19-11-2015)

* Fixed issue where comparing a nil 'value' with numeric type validators would cause a NullPointerException.
* Add option to pass :default-message-fn to validate in order to support custom validation messages.
