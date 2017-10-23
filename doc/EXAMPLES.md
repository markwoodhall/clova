# Examples

* You want to validate a `:failed-login-count` key. The key should be present in
the map and it's value should be less than 5.

```clojure

(let [v-set [:failed-login-count required? [lesser? 5]]]

  (validate v-set {:failed-login-count 4})
  ;; {:failed-login-count 4}

  (validate v-set {:failed-login-count 5})
  ;; {:clova.core/results ("failed-login-count is 5 but it must be less than 5.")
  ;;  :clova.core/invalid? true 
  ;;  :failed-login-count ("failed-login-count is 5 but it must be less than 5.")}

  (validate v-set {}))
  ;; {:clova.core/results ("failed-login-count is required.")
  ;;  :clova.core/invalid? true,
  ;;  :failed-login-count ("failed-login-count is required.")}
 
```

* You want to validate a `:quantity` key. The key should be present in the map and
it's value should be between 5 and 10.

```clojure

(let [v-set [:quantity required? [between? 5 10]]]

  (validate v-set {:quantity 5})
  ;; {:quantity 5}

  (validate v-set {:quantity 4})
  ;; {:results ("quantity is 4 but it must be between 5 and 10."), :valid? false}
  ;; {:clova.core/results ("quantity is 4 but it must be between 5 and 10.") 
  ;;  :clova.core/invalid? true 
  ;;  :quantity ("quantity is 4 but it must be between 5 and 10.")}

  (validate v-set {:quantity 11})
  ;; {:clova.core/results ("quantity is 11 but it must be between 5 and 10.") 
  ;;  :clova.core/invalid? true 
  ;;  :quantity ("quantity is 11 but it must be between 5 and 10.")}

  (validate v-set {}))
  ;; {:clova.core/results ("quantity is required.")
  ;;  :clova.core/invalid? true
  ;;  :quantity ("quantity is required.")}

```

* You want to validate an `:action` key. The key *does not* have to be present
but if it is then it must be one of "GET" or "POST".

```clojure

(let [v-set [:action [one-of? ["GET" "POST"]]]]

  (validate v-set {:action "POST"})
  ;; {:action "POST"}

  (validate v-set {:action "GET"})
  ;; {:action "GET"}

  (validate v-set {:action "DEL"})
  ;; {:clova.core/results ("action is DEL but should be one of [\"GET\" \"POST\"].") 
  ;;  :clova.core/invalid? true 
  ;;  :action ("action is DEL but should be one of [\"GET\" \"POST\"].")}

  (validate v-set {}))
  ;; {}

```

* You want to validate an email address to check if it already exists in your system

Note, in this example we make use of "functional args". If an argument to a validator is
a function then it will be invoked at validation time.

```clojure

(let [database {:emails ["test@email.com"]}
      emails (fn [email] (filter #{email} (:emails database)))
      v-set [:email required? [not-exists? emails]]]

  (validate v-set {:email "test2@email.com"})
  ;; {:email "test2@email.com"}

  (validate v-set {:email "anothertest@email.com"})
  ;; {:email "anothertest@email.com"}

  (validate v-set {:email "test@email.com"})
  ;; {:clova.core/results ("email test@email.com already exists.")
  ;;  :clova.core/invalid? true 
  ;;  :email ("email test@email.com already exists.")}

  (validate v-set {}))
  ;; {:clova.core/results ("email is required.") 
  ;;  :clova.core/invalid? true 
  ;;  :email ("email is required.")}

```
