# Change Log

### 0.48.0 (13-09-2024)

* Add `gov-uk-post-code` validation

### 0.47.0 (13-09-2024)

* Remove unused `get-possibly-unbound-var` function to fix warnings
* Upgrade base clojure version to resolve security vulnerability - https://github.com/markwoodhall/clova/pull/10

### 0.37.0 (13-12-2017)

* Update clj-time dependency.
* Update cljs-time dependency.

### 0.35.2 (10-12-2017)

* Update Clojure and ClojureScript dependencies.

### 0.35.1 (16-11-2017)

* Fix issue where `::invalid?` can sometimes be incorrect.

### 0.34.0 (23-10-2017)

* Breaking change to improve value returned from `validate`. Maps that conform
to a validation set are now returned wihtout modification. Maps that do not conform 
to a validation set are returned with a validation error sequence transposed onto the
applicable keys, see example below. 

```clojure
(validate
  [:email email?
   :age [between? 18 40]
   [:nested :value] [between? 0 10]] 
   {:email "testemail.com" :age 10 :nested {:value 19}})

;; {:clova.core/results ("email should be a valid email address." "age is 10 but it must be between 18 and 40." "nested value is 19 but it must be between 0 and 10.") 
;;  :clova.core/invalid? true 
;;  :email ("email should be a valid email address.") 
;;  :age ("age is 10 but it must be between 18 and 40."), 
;;  :nested {:value ("nested value is 19 but it must be between 0 and 10.")}}
```


### 0.33.0 (20-10-2017)

* Added `results` convenience function

```clojure
(results [:email required? email?] map-to-validate)
```

### 0.32.0 (19-10-2017)

* Added functonality to support plain vector validators and added `exists?` validator.

### 0.31.0 (19-10-2017)

* Added functonality to support "functional args" to validators, and added `not-exists?` validator.

```clojure

(let [database {:emails ["test@email.com"]}
      emails (fn [email] (filter #{email} (:emails database)))
      v-set (validation-set [:email required? [not-exists? emails]])]

  (validate v-set {:email "test2@email.com"})
  ;; {:results (), :valid? true}

  (validate v-set {:email "anothertest@email.com"})
  ;; {:results (), :valid? true}

  (validate v-set {:email "test@email.com"})
  ;; {:results ("test@email.com already exists."),
  ;;  :valid? false}

  (validate v-set {})
  ;; {:results ("email is required."), :valid? false}

```

### 0.30.0 (11-08-2016)

* Added the ability to use arbitrary functions (not specially defined validators) in validation sets.

```clojure
(let [validation-set (validation-set [:age [> 18]])])
```
In failure scenarios a generic message format of `"%s has value %s, which is invalid."` is used.

### 0.29.0 (10-08-2016)

* Add validated value and validator arguments as arguments to custom message functions. e.g.

```clojure
(let [message-func (fn [v-type value args]
                    (case v-type
                      :between (str "Age is " value " but it must be between " (first args) " and " (second args))
                       nil))]
    (validate v-set {:age 9} {:default-message-fn message-func}))
```

### 0.28.0 (08-08-2016)

* Add `alphanumeric?` validator.

```clojure
(alphanumeric? "abc")
```

### 0.27.0 (04-08-2016)

* Upgrade clojurescript dependency from 1.8.51 > 1.9.93
* Upgrade clj-time dependency from 0.11.0 > 0.12.0

### 0.26.0 (11-05-2016)

* Upgrade clojurescript dependency from 1.8.40 > 1.8.51

### 0.25.0 (28-04-2016)

* Upgrade clojurescript dependency from 1.7.228 > 1.8.40

### 0.24.0 (13-03-2016)

* Upgrade clojure dependency from 1.7.0 > 1.8.0.

### 0.22.0 (04-01-2016)

* Add =? validator.

```clojure
(=? {:a 1} {:a 1})
```

### 0.21.0 (08-12-2015)

* Add =date? validator.

Not to be confused with the `date?` validator which tests that a value is a date. The
`=date?` validator checks that a value is equal to a given date.

```clojure
(=date? "01-01-2011" "01-01-2011")
;; or specify an optional date formatter
(=date? "01-01-2011" "02-01-2011" {:formatter "dd-MM-yyyy"})
```

### 0.20.0 (08-12-2015)

* Add after? validator.

```clojure
(after? "01-01-2011" "02-01-2012")
;; or specify an optional date formatter
(after? "01-01-2011" "02-01-2012" {:formatter "dd-MM-yyyy"})
```

### 0.19.0 (07-12-2015)

* Add before? validator.

```clojure
(before? "01-01-2011" "01-01-2012")
;; or specify an optional date formatter
(before? "01-01-2011" "01-01-2012" {:formatter "dd-MM-yyyy"})
```

### 0.18.2 (05-12-2015)

* Fix issue where date validator is unable to handle values that are
already "dates". Consider java.util.Date org.joda.time.DateTime and js/Date goog.date.Date goog.date.DateTime as
valid dates without requiring any additonal validation.

### 0.18.1 (04-12-2015)

* Fix bug where a custom string `:formatter` for the `date?` validator was never
coverted to a clj-time/cljs-time formatter.

### 0.18.0 (04-12-2015)

* Add `date?` validator. It is now possible to do:

```clojure
(validate (validation-set [:date date?]) {:date "20150101"})
```

### 0.17.0 (04-12-2015)

* Rename `anon` to `as-validator`.

### 0.16.0 (03-12-2015)

* Add `anon` function. This can be used to declare an on the fly annonymous validator.

```clojure
(validate (validation-set [:name (anon #(= % "mark"))]) {:name "mark"})
```

### 0.15.0 (02-12-2015)

* Add `stringy?` validator.

### 0.14.0 (01-12-2015)

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
