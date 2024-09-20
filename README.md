# clova

A "minimal" validation library for Clojure and ClojureScript.

- [API Docs](https://cljdoc.xyz/d/clova/clova/0.49.0/api/clova)
- [Change Log](https://github.com/markwoodhall/clova/blob/master/doc/CHANGES.md)

## Status

[![CircleCI](https://circleci.com/gh/markwoodhall/clova.svg?style=svg)](https://circleci.com/gh/markwoodhall/clova)
[![Clojars Project](https://img.shields.io/clojars/v/clova.svg)](http://clojars.org/clova)


## Installation

`clova` is available from [Clojars](https://clojars.org/clova)

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

1. [between?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#between?)
2. [email?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#email?)
3. [greater?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#greater?)
4. [lesser?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#lesser?)
5. [matches?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#matches?)
6. [negative?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#negative?)
7. [positive?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#positive?)
8. [post-code?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#post-code?)
9. [not-nil?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#not-nil?)
10. [required?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#required?)
11. [url?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#url?)
12. [zip-code?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#zip-code?)
13. [length?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#length?)
14. [longer?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#longer?)
15. [shorter?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#shorter?)
16. [one-of?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#one-of?)
17. [all?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#all?)
18. [credit-card?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#credit-card?)
19. [numeric?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#numeric?)
20. [stringy?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#stringy?)
21. [date?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#date?)
22. [before?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#before?)
23. [after?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#after?)
24. [=date?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#=date?)
25. [=?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#=?)
26. [alphanumeric?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#alphanumeric?)
27. [not-exists?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#not-exists?)
27. [exists?](https://cljdoc.org/d/clova/clova/0.40.2/api/clova.core#exists?)

## License

Copyright © 2015-2024 Mark Woodhall

Released under the MIT License: http://www.opensource.org/licenses/mit-license.php
