## Examples

* You want to validate a :failed-login-count key. The key should be present in
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
