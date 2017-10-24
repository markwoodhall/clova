# clova

A "minimal" validation library for Clojure and ClojureScript.

- [API Docs](http://markwoodhall.github.io/clova)
- [Change Log](https://github.com/markwoodhall/clova/blob/master/doc/CHANGES.md)

## Status

[![Build Status](https://api.travis-ci.org/markwoodhall/clova.svg?branch=master)](https://api.travis-ci.org/repositories/markwoodhall/clova)
[![Dependency Status](https://www.versioneye.com/user/projects/566b4d044e049b003b000704/badge.svg?style=flat)](https://www.versioneye.com/user/projects/566b4d044e049b003b000704)
[![Clojars Project](https://img.shields.io/clojars/v/clova.svg)](http://clojars.org/clova)


## Installation

`clova` is available from [Clojars](https://clojars.org/clova)

#

Add the following to `project.clj` `:dependencies`:

[![Clojars Project](http://clojars.org/clova/latest-version.svg)](http://clojars.org/clova)

## Usage

Validation sets are pairs of keys to validate and the functions used to validate them. When a map conforms
to the validation set then the `validate` function returns the original map.

```clojure
(validate
  [:email email?
   :age [between? 18 40]
   [:nested :value] [between? 0 10]] 
   {:email "test@email.com" :age 20 :nested {:value 9}})

;; {:email "test@email.com", :age 20, :nested {:value 9}}

```

When a map does not conform to the validation set then the `validate` function returns the original map
with a sequence of validation errors transposed onto the applicable keys. All validation errors are available
using the `:clova.core/results` key and the validation status is available using the `:clova.core/invalid?` key.

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

You don't have to use pre-defined validator functions exposed by clova, you can also use arbitrary functions. 

Arbitrary functions will not generate scenario specific failure messages but a generic message format of `"%s has value %s, which is invalid."` will be used.

```clojure
(validate [:age [> 18]] {:age 21})

;; {:age 21}
```

If you want to compose multiple validators you can.

```clojure
(validate [:age required? [greater? 18] [lesser? 30]] {:age 29})

;; {:age 29}
```

Most of the time it is useful to only apply and fail validation if a given key is present in the map under validation, this is
the default behaviour in clova. However if this is not the case and you wish to make a validator fail if the key is not present you can do so
by using a `required?` validator.


```clojure
(validate [:email required? email?] {:email "email@somedomain.com"})

;; {:email "email@somedomain.com"}

(validate [:age required? [between? 18 30]] {:age 29})

;; {:age 29}
```

Get the validation status:

```clojure
(:clova.core/invalid? (validate [:email required? email?] {:email "notanemail"}))

;; true

(:clova.core/invalid? (validate [:email required? email?] {:email "email@somedomain.com"}))

;; nil
```

or
```clojure
(valid? [:email required? email?] {:email "email@somedomain.com"})

;; true

(valid? [:email required? email?] {:email "notanemail"})

;; false

```

Get the validation results (error messages):

```clojure
(:clova.core/results (validate [:email required? email?] {:email "email@somedomain.com"}))

;; nil
(:clova.core/results (validate [:email required? email?] {:email "notanemail"}))

;; ("email should be a valid email address.")
```

or
```clojure
(results [:email required? email?] {:email "email@somedomain.com"})

;; nil

(results [:email required? email?] {:email "notanemail"})

;; ("email should be a valid email address.")
```

You can also specify a custom function for providing validation error messages. This function will
be called with the validator type, the target value and any arguments passed to the validator specified as arguments,
if the custom function returns nil then the default validation message will be used.

For example, we can use the `between?` validator with a custom error message, like so:

```clojure
(let [message-func (fn [v-type value args]
                    (case v-type
                      :between (str "Age is " value " but it must be between " (first args) " and " (second args))
                       nil))]
    (validate v-set {:age 9} {:default-message-fn message-func}))
```

By default clova will execute all validators and provide validation messages for all failures. You
can override this behaviour using the `:short-circuit?` option. This will stop execution of subsequent
validators after the first validation failure and will therefore only return one validation failure
message.

```clojure
(validate v-set {:email ""} {:short-circuit? true })
```

[See more usage examples.](https://github.com/markwoodhall/clova/blob/master/doc/EXAMPLES.md)

## Validators

clova has the following built in validators

1. [between?](http://markwoodhall.github.io/clova/clova.core.html#var-between.3F)
2. [email?](http://markwoodhall.github.io/clova/clova.core.html#var-email.3F)
3. [greater?](http://markwoodhall.github.io/clova/clova.core.html#var-greater.3F)
4. [lesser?](http://markwoodhall.github.io/clova/clova.core.html#var-lesser.3F)
5. [matches?](http://markwoodhall.github.io/clova/clova.core.html#var-matches.3F)
6. [negative?](http://markwoodhall.github.io/clova/clova.core.html#var-negative.3F)
7. [positive?](http://markwoodhall.github.io/clova/clova.core.html#var-positive.3F)
8. [post-code?](http://markwoodhall.github.io/clova/clova.core.html#var-post-code.3F)
9. [not-nil?](http://markwoodhall.github.io/clova/clova.core.html#var-not-nil.3F)
10. [required?](http://markwoodhall.github.io/clova/clova.core.html#var-required.3F)
11. [url?](http://markwoodhall.github.io/clova/clova.core.html#var-url.3F)
12. [zip-code?](http://markwoodhall.github.io/clova/clova.core.html#var-zip-code.3F)
13. [length?](http://markwoodhall.github.io/clova/clova.core.html#var-length.3F)
14. [longer?](http://markwoodhall.github.io/clova/clova.core.html#var-longer.3F)
15. [shorter?](http://markwoodhall.github.io/clova/clova.core.html#var-shorter.3F)
16. [one-of?](http://markwoodhall.github.io/clova/clova.core.html#var-one-of.3F)
17. [all?](http://markwoodhall.github.io/clova/clova.core.html#var-all.3F)
18. [credit-card?](http://markwoodhall.github.io/clova/clova.core.html#var-credit-card.3F)
19. [numeric?](http://markwoodhall.github.io/clova/clova.core.html#var-numeric.3F)
20. [stringy?](http://markwoodhall.github.io/clova/clova.core.html#var-stringy.3F)
21. [date?](http://markwoodhall.github.io/clova/clova.core.html#var-date.3F)
22. [before?](http://markwoodhall.github.io/clova/clova.core.html#var-before.3F)
23. [after?](http://markwoodhall.github.io/clova/clova.core.html#var-after.3F)
24. [=date?](http://markwoodhall.github.io/clova/clova.core.html#var-.3Ddate.3F)
25. [=?](http://markwoodhall.github.io/clova/clova.core.html#var-.3D.3F)
26. [alphanumeric?](http://markwoodhall.github.io/clova/clova.core.html#var-alphanumeric.3F)
27. [not-exists?](http://markwoodhall.github.io/clova/clova.core.html#var-not-exists.3F)
27. [exists?](http://markwoodhall.github.io/clova/clova.core.html#var-exists.3F)

## License

Copyright © 2017 Mark Woodhall

Released under the MIT License: http://www.opensource.org/licenses/mit-license.php
