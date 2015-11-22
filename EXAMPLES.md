## Examples

* You want to validate a `:failed-login-count` key. The key should be present in
the map and it's value should be less than 5.

```clojure

(let [v-set (validation-set [:failed-login-count required?
                             :failed-login-count [lesser? 5]])]

  (validate v-set {:failed-login-count 5})
  ;; {:results (), :valid? true}

  (validate v-set {:failed-login-count 5})
  ;; {:results ("failed-login-count is 5 but it must be less than 5."),
  ;;  :valid? false}

  (validate v-set {}))
  ;; {:results ("failed-login-count is required."), :valid? false}

```

* You want to validate a `:quantity` key. The key should be present in the map and
it's value should be between 5 and 10.

```clojure

(let [v-set (validation-set [:quantity required?
                             :quantity [between? 5 10]])]

  (validate v-set {:quantity 5})
  ;; {:results (), :valid? true}

  (validate v-set {:quantity 4})
  ;; {:results ("quantity is 4 but it must be between 5 and 10."), :valid? false}

  (validate v-set {:quantity 11})
  ;; {:results ("quantity is 11 but it must be between 5 and 10."), :valid? false}

  (validate v-set {}))
  ;; {:results ("quantity is required."), :valid? false}

```


